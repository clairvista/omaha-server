SELECT ' ====  Initial Population of Tables  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_content;

delimiter //
CREATE PROCEDURE leomaha.setup_initial_db_content() BEGIN
	
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'applications'
                 AND table_schema = 'leomaha') THEN
      IF NOT EXISTS (SELECT 1
                       FROM leomaha.applications
                      WHERE app_id = '{025308fe-32f4-40c3-a3ed-eb00823b47ed}' ) THEN
                     
         INSERT INTO leomaha.applications
              ( name
              , description
              , app_id
              , created_by )
            VALUES
              ( 'Test Prototype'
              , 'A test application for validating the functionality of the omaha update'
              , '{025308fe-32f4-40c3-a3ed-eb00823b47ed}'
              , 'hstrowd' );
      
      END IF;
   ELSE
      SELECT 'Applications table does not exist.' AS 'ERROR';
   END IF;
   
   SET @application_foreign_key = (SELECT id FROM leomaha.applications WHERE app_id = '{025308fe-32f4-40c3-a3ed-eb00823b47ed}');
   
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'application_versions'
                 AND table_schema = 'leomaha') THEN
      IF NOT EXISTS (SELECT 1
                       FROM leomaha.application_versions
                      WHERE application_id = @application_foreign_key
                        AND version_id = '0.0.0.0' ) THEN
                     
         INSERT INTO leomaha.application_versions
              ( version_id
              , application_id
              , download_base_url
              , installer_name
              , installer_hash
              , installer_size
              , created_by )
            VALUES
              ( '0.0.0.0'
              , @application_foreign_key
              , 'http://assets.liveexpert.net/omaha/testInstaller'
              , 'installer.exe'
              , 'foo-bar-1234'
              , '12345'
              , 'hstrowd' );
      END IF;
                     
      IF NOT EXISTS (SELECT 1
                       FROM leomaha.application_versions
                      WHERE application_id = @application_foreign_key
                        AND version_id = '1.0.0.0' ) THEN
         INSERT INTO leomaha.application_versions
              ( version_id
              , application_id
              , download_base_url
              , installer_name
              , installer_hash
              , installer_size
              , created_by )
            VALUES
              ( '1.0.0.0'
              , @application_foreign_key
              , 'http://assets.liveexpert.net/omaha/testInstaller'
              , 'installer.exe'
              , 'foo-bar-1234'
              , '12345'
              , 'hstrowd' );
      
      END IF;
   ELSE
      SELECT 'Application Versions table does not exist.' AS 'ERROR';
   END IF;
   
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'protocols'
                 AND table_schema = 'leomaha') THEN
      
      IF NOT EXISTS (SELECT 1
                       FROM leomaha.protocols
                      WHERE protocol_id = '3.0' ) THEN
                     
         INSERT INTO leomaha.protocols
              ( protocol_id
              , created_by )
            VALUES
              ( '3.0'
              , 'hstrowd' );
      
      END IF;
      
   ELSE 
      SELECT 'Protocols table does not exist.' AS 'ERROR';
   END IF;
                     
   
	
END //

delimiter ;

call leomaha.setup_initial_db_content();

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_content;
COMMIT;