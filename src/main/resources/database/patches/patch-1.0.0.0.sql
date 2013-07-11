SELECT ' ====  Initial Population of Tables  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_content;

delimiter //
CREATE PROCEDURE leomaha.setup_initial_db_content() BEGIN
	
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'applications'
                 AND table_schema = 'leomaha') THEN
      SELECT 'TODO';
   ELSE
      SELECT 'Applications table does not exist.' AS 'ERROR';
   END IF;
   
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'application_versions'
                 AND table_schema = 'leomaha') THEN
      SELECT 'TODO';
   ELSE
      SELECT 'Application Versions table does not exist.' AS 'ERROR';
   END IF;
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.tables
                   WHERE table_name = 'protocols'
                     AND table_schema = 'leomaha') THEN
      
      SELECT 'Protocols table does not exist.' AS 'ERROR';
      
   ELSE 
      IF NOT EXISTS (SELECT 1
                       FROM leomaha.protocols
                      WHERE name = '3.0' ) THEN
                     
         INSERT INTO leomaha.protocols
              ( name
              , created_by )
            VALUES
              ( '3.0'
              , 'hstrowd' );
      
      END IF;
   END IF;
                     
   
	
END //

delimiter ;

call leomaha.setup_initial_db_content();

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_content;
COMMIT;