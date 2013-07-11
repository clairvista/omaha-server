SELECT ' ====  Initial Creation of tables  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_structure;

delimiter //
CREATE PROCEDURE leomaha.setup_initial_db_structure() BEGIN
	
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'applications'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.applications
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             name VARCHAR(255) NOT NULL,
             description VARCHAR(255) NOT NULL,
             app_id VARCHAR(255) NOT NULL UNIQUE,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             created_by VARCHAR(255) NOT NULL,
             PRIMARY KEY(id) );
   ELSE
      SELECT 'Applications table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'application_versions'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.application_versions
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             version_id VARCHAR(255) NOT NULL,
             application_id INT UNSIGNED NOT NULL,
             access_token VARCHAR(255),
             download_base_url VARCHAR(255) NOT NULL,
             installer_name VARCHAR(255) NOT NULL,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             created_by VARCHAR(255) NOT NULL,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.application_versions
           ADD FOREIGN KEY (application_id)
           REFERENCES leomaha.applications(id);
   ELSE
      SELECT 'Application Versions table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'operating_systems'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.operating_systems
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             platform VARCHAR(255) NOT NULL,
             version VARCHAR(255) NOT NULL,
             service_pack VARCHAR(255),
             architecture VARCHAR(255),
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
   ELSE
      SELECT 'Operating Systems table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'users'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.users
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             user_id VARCHAR(255) NOT NULL,
             operating_system_id INT UNSIGNED,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.users
           ADD FOREIGN KEY (operating_system_id)
           REFERENCES leomaha.operating_systems(id);
   ELSE
      SELECT 'Users table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'operating_systems_history'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.operating_systems_history
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             user_id INT UNSIGNED NOT NULL,
             platform VARCHAR(255) NOT NULL,
             version VARCHAR(255) NOT NULL,
             service_pack VARCHAR(255),
             architecture VARCHAR(255),
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.operating_systems_history
           ADD FOREIGN KEY (user_id)
           REFERENCES leomaha.users(id);
   ELSE
      SELECT 'Operating Systems History table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'updates'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.updates
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             user_id INT UNSIGNED NOT NULL,
             source_version_id INT UNSIGNED,
             target_version_id INT UNSIGNED NOT NULL,
             language VARCHAR(255)NOT NULL,
             brand VARCHAR(255)NOT NULL,
             additional_params VARCHAR(255)NOT NULL,
             experiments VARCHAR(255)NOT NULL,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.updates
           ADD FOREIGN KEY (source_version_id)
           REFERENCES leomaha.application_versions(id);
             
       ALTER TABLE leomaha.updates
           ADD FOREIGN KEY (target_version_id)
           REFERENCES leomaha.application_versions(id);
   ELSE
      SELECT 'Updates table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'sessions'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.sessions
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             session_id VARCHAR(255) NOT NULL,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
   ELSE
      SELECT 'Sessions table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'protocols'
                     AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.protocols
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             name VARCHAR(255) NOT NULL UNIQUE,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             created_by VARCHAR(255) NOT NULL,
             PRIMARY KEY(id) );
   ELSE
      SELECT 'Protocols table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                     FROM information_schema.tables
                    WHERE table_name = 'client_versions'
                      AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.client_versions
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             version_id VARCHAR(255) NOT NULL UNIQUE,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
   ELSE
      SELECT 'Client Versions table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                     FROM information_schema.tables
                    WHERE table_name = 'requests'
                      AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.requests
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             request_id VARCHAR(255) NOT NULL,
             protocol_id INT UNSIGNED NOT NULL,
             client_version_id INT UNSIGNED NOT NULL,
             is_machine INT(1) UNSIGNED NOT NULL,
             user_id INT UNSIGNED,
             session_id INT UNSIGNED NOT NULL,
             install_source VARCHAR(255),
             origin_url VARCHAR(255),
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.requests
           ADD FOREIGN KEY (session_id)
           REFERENCES leomaha.sessions(id);
   ELSE
      SELECT 'Requests table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                     FROM information_schema.tables
                    WHERE table_name = 'update_checks'
                      AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.update_checks
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             application_version_id INT UNSIGNED,
             request_id INT UNSIGNED NOT NULL,
             update_disabled INT(1),
             target_version_prefix VARCHAR(255),
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.update_checks
           ADD FOREIGN KEY (application_version_id)
           REFERENCES leomaha.application_versions(id);
             
       ALTER TABLE leomaha.update_checks
           ADD FOREIGN KEY (request_id)
           REFERENCES leomaha.requests(id);
   ELSE
      SELECT 'Update Checks table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                     FROM information_schema.tables
                    WHERE table_name = 'pings'
                      AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.pings
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             application_version_id INT UNSIGNED,
             request_id INT UNSIGNED NOT NULL,
             was_active INT(1) UNSIGNED,
             last_active INT,
             last_present INT,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.pings
           ADD FOREIGN KEY (application_version_id)
           REFERENCES leomaha.application_versions(id);
             
       ALTER TABLE leomaha.pings
           ADD FOREIGN KEY (request_id)
           REFERENCES leomaha.requests(id);
   ELSE
      SELECT 'Pings table already exists.' AS 'warning';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                     FROM information_schema.tables
                    WHERE table_name = 'events'
                      AND table_schema = 'leomaha') THEN
      CREATE TABLE leomaha.events
           ( id INT UNSIGNED NOT NULL AUTO_INCREMENT,
             application_version_id INT UNSIGNED,
             request_id INT UNSIGNED NOT NULL,
             event_type INT NOT NULL,
             event_result INT NOT NULL,
             error_code INT,
             extra_code INT,
             update_check_time_ms INT,
             download_time_ms INT,
             install_time_ms INT,
             bytes_downloaded INT,
             total_size INT,
             source_url_index INT,
             state_cancelled INT,
             time_since_update_available INT,
             time_since_download_start INT,
             created_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
             PRIMARY KEY(id) );
             
       ALTER TABLE leomaha.events
           ADD FOREIGN KEY (application_version_id)
           REFERENCES leomaha.application_versions(id);
             
       ALTER TABLE leomaha.events
           ADD FOREIGN KEY (request_id)
           REFERENCES leomaha.requests(id);
   ELSE
      SELECT 'Events table already exists.' AS 'warning';
   END IF;
END //

delimiter ;

call leomaha.setup_initial_db_structure();

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_structure;
COMMIT;