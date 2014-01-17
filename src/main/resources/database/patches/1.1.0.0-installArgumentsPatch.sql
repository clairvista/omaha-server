SELECT ' ====  Add Install Arguments  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.add_install_arguments;

delimiter //
CREATE PROCEDURE leomaha.add_install_arguments() BEGIN
   
   IF NOT EXISTS (SELECT 1
                    FROM information_schema.columns
                   WHERE table_name = 'applications'
                     AND column_name  = 'install_arguments'
                     AND table_schema = 'leomaha') THEN

       ALTER TABLE leomaha.applications
              ADD COLUMN install_arguments VARCHAR(256) AFTER app_id;
      
   ELSE 
      SELECT 'Install arguments colomun already exists.' AS 'ERROR';
   END IF;   
	
END //
delimiter ;

call leomaha.add_install_arguments();

DROP PROCEDURE IF EXISTS leomaha.add_install_arguments;
COMMIT;
