#* FileName: autoDeploy.ps1
#*=============================================================================
#* Purpose: Deploys the WAR application to a specified environment. This includes
#*    the following:
#*      - Uploading the WAR file
#*      - Backing up the existing version
#*      - Deploying the new version
#*      - Verifying that the deployment was successful
#*
#*=============================================================================

PARAM( [string]$buildOutputDir, 
       [string]$serverHost, 
       [string]$username,
       [string]$puttyKeyFile, 
       [string]$sshKeyFile)

# Enables/disables debugging output
$debug = $TRUE

# Ensure the SSH Module is loaded.
Import-Module SSH-Sessions

# Identify the current script's directory.
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path



#*-----------------------------------------------
#* Function: Upload-Artifact
#* Purpose: Uploads a specified deployment artifact to a set of server.
#*-----------------------------------------------
function Upload-Artifact ($artifactPath, $server, $username, $keyFile, $uploadPath) {
    IF ($debug) {
        Write-Host @"
Function Upload-Artifact Called:
 -- Artifact Path: $artifactPath
 -- Servers: $servers
 -- Username: $username
 -- Upload Path: $uploadPath
"@
}

    IF ($debug) { Write-Host "Uploading artifact to ${server}." }

    # NOTE: The first type pscp.exe is run on a new server, there is a 
    #   mandatory command line input to validate the server's key. To
    #   get around this "ECHO Y |" or "TYPE yes.txt |" must be prepended
    #   to the pscp.exe command. This is only required for the first time.
    #   When this is done, the batch argument must be removed.
    $uploadCommand = "$scriptDir\..\lib\pscp.exe -batch -scp -2 -r -q -i `"$keyFile`" `"$artifactPath`" $username@$server`:$uploadPath"
    IF ($debug) { Write-Host "Upload Command: $uploadCommand" }

    # Push out release artifacts.
    Invoke-Expression $uploadCommand
    
    IF ($debug) { Write-Host "File upload completed." }
    return $TRUE;
}


#*-----------------------------------------------
#* Function: Backup-Current-Version
#* Purpose: Moves the current application version to a specified backup directory.
#*-----------------------------------------------
function Backup-Current-Version($deploymentDir, $backupDir, $appFilename) {
    IF ($debug) {
        Write-Host @"
Function Backup-Current-Version Called:
 -- Deployment Directory: $deploymentDir
 -- Backup Directory: $backupDir
 -- Application Filename: $appFilename
"@
}

    $deploymentFilePath = "$deploymentDir/$appFilename"
    $timestamp = Get-Date -format "yyyyMMddHHmmss"
    $backupFilePath = "${backupDir}/${appFilename}.backup.${timestamp}"

    $backupCommand = "mv $deploymentFilePath $backupFilePath"
    IF ($debug) { Write-Host "Backing up current version with: ${backupCommand}" }
    $backupResult = Invoke-SshCommand -InvokeOnAll -Command $backupCommand

    IF ($backupResult -ne "") {
        "##teamcity[message text='Current version backup result was not as expected: $backupResult' status='WARNING']"
        return $FALSE
    }
    return $TRUE
}


#*-----------------------------------------------
#* Function: Deploy-New-Version
#* Purpose: Moves the uploaded application version into the deployment directory.
#*-----------------------------------------------
function Deploy-New-Version($uploadDir, $deploymentDir, $appFilename) {
    IF ($debug) {
        Write-Host @"
Function Deploy-New-Version Called:
 -- Upload Directory: $uploadDir
 -- Deployment Directory: $deploymentDir
 -- Application Filename: $appFilename
"@
}

    $uploadFilePath = "$uploadDir/$appFilename"
    $deploymentFilePath = "$deploymentDir/$appFilename"

    $deployCommand = "mv $uploadFilePath $deploymentFilePath"
    IF ($debug) { Write-Host "Deploying the new version with: ${deployCommand}" }
    $deployResult = Invoke-SshCommand -InvokeOnAll -Command $deployCommand

    IF ($deployResult -ne "") {
        "##teamcity[message text='New version deployment result was not as expected: $deployResult' status='WARNING']"
        return $FALSE
    }

    $statusIndicatorCleanupCommand = "rm $deploymentFilePath.*"
    IF ($debug) { Write-Host "Cleaning up the status indicator files with: ${statusIndicatorCleanupCommand}" }
    $deployResult = Invoke-SshCommand -InvokeOnAll -Command $statusIndicatorCleanupCommand

    return $TRUE
}


#*-----------------------------------------------
#* Function: Verify-Http-Access
#* Purpose: Verifies that the application is properly responding to HTTP requests.
#*-----------------------------------------------
function Verify-Http-Access($server) {
    FOR ($i=0; $i -lt 5; $i++) {
        try {
            $httpResult = Invoke-WebRequest -UseBasicParsing -Uri "http://${server}/omaha/status"
            IF ($debug) { Write-Host "Status check result: $($httpResult.StatusCode)" }

            IF ($httpResult.StatusCode -eq 200) { return $TRUE }
        } Catch [System.Net.WebException] {
            IF ($debug) { Write-Host "Error during ping check: $($_.Exception.ToString())" }
        }

        # Wait 5 seconds between checks
        Start-Sleep -s 5
    }

    return $FALSE
}




#*-----------------------------------------------
#* SCRIPT BODY
#*-----------------------------------------------

Try {
   # Abstracting the application's name makes this script easier to reuse elsewhere.
   $appName = "omaha.war"
   $buildArtifactPath = Join-Path -Path $buildOutputDir -ChildPath $appName
   
   IF ($debug) {
      Write-Host @"
Auto Deploy Input Arguments:
 -- Build Output Directory: $buildOutputDir
 -- Version Number: $versionNumber
 -- Application Name: $appName
 -- Build Artifact Path: $buildArtifactPath
 -- Server: $serverHost
 -- Username: $username
"@
   }

   # Server Constants
   $serverUploadDir = "~/deploymentUpload"
   $serverDeploymentDir = "$CATALINA_HOME/webapps"
   $serverBackupDir = "~/deploymentBackups"

   IF ($debug) {
      Write-Host @"
Server Constants:
 -- Server Upload Directory: $serverUploadDir
 -- Server Deployment Directory: $serverDeploymentDir
 -- Server Backup Directory: $serverBackupDir
"@
   }


   # Upload the WAR
   $serverUploadPath = "$serverUploadDir/$appname"
   $wasArtifactUploaded = (Upload-Artifact $buildArtifactPath $serverHost $username $puttyKeyFile $serverUploadPath)
   IF (-Not ($wasArtifactUploaded)) {
       Write-Host "##teamcity[message text='Failed to upload ${buildArtifactPath} to ${serverHost}.' status='FAILURE']"
       exit 2
   }


   IF ($debug) { Write-Host "Deploying the application to ${serverHost}." }
   New-SshSession -ComputerName $serverHost -Username $username -KeyFile $sshKeyFile


   # ----------- Deployment Logic --------------

   $backupResult = (Backup-Current-Version $serverDeploymentDir $serverBackupDir $appname)

   $deployResult = (Deploy-New-Version $serverUploadDir $serverDeploymentDir $appName)

   IF ($debug) { Write-Host "Waiting for service to fully initialize." }
   Start-Sleep -s 10

   $wasAppVerified = (Verify-Http-Access $serverHost)
   IF (-Not ($wasAppVerified)) {
       Write-Host "##teamcity[message text='Failed to verify that the application is accessible.' status='FAILURE']"
       exit 5
   }


   # Close SSH connections.
   Remove-SshSession -RemoveAll


   Write-Host "##teamcity[message text='Deployment Completed Successfully.' status='NORMAL']"
} Catch [system.exception] {
    Write-Host "Error encountered during deployment."
    $error
    exit 1
}
