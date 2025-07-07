-- -- tạo roles
-- CREATE ROLE product_admin WITH LOGIN PASSWORD 'admin_password';
-- CREATE ROLE product_reader WITH LOGIN PASSWORD 'reader_password';

-- tạo databases
CREATE DATABASE user_service;
CREATE DATABASE product_service;
CREATE DATABASE order_service;

-- cấu hình user_service
\connect user_service;

CREATE TABLE IF NOT EXISTS userinfo (
                                        userId VARCHAR NOT NULL PRIMARY KEY,
                                        address VARCHAR,
                                        phone VARCHAR,
                                        is_verify BOOLEAN DEFAULT FALSE,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

\connect product_service;


