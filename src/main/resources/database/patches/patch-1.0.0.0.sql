SELECT ' ====  Initial Population of Tables  ==== ' AS 'Starting Patch';

DROP PROCEDURE IF EXISTS leomaha.setup_initial_db_content;

delimiter //
CREATE PROCEDURE leomaha.setup_initial_db_content() BEGIN
   
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