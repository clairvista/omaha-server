SELECT ' ====  Add Application Version Uniqueness Constraint  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.setup_application_version_constraint;

delimiter //
CREATE PROCEDURE leomaha.setup_application_version_constraint() BEGIN
   
   IF EXISTS (SELECT 1
                FROM information_schema.tables
               WHERE table_name = 'application_versions'
                 AND table_schema = 'leomaha') THEN
      
      IF NOT EXISTS (SELECT 1
                       FROM information_schema.table_constraints
                      WHERE constraint_schema = 'leomaha'
		        AND constraint_name = 'unique_application_version_numbers' ) THEN

         ALTER TABLE leomaha.application_versions
            ADD CONSTRAINT unique_application_version_numbers UNIQUE (application_id, version_id);
      
      ELSE 
         SELECT 'Application version numbers uniqueness constraint already exists.' AS 'debug';
      END IF;
      
   ELSE 
      SELECT 'Application versions table does not exist.' AS 'ERROR';
   END IF;   
	
END //
delimiter ;

call leomaha.setup_application_version_constraint();

DROP PROCEDURE IF EXISTS leomaha.setup_application_version_constraint;
COMMIT;
