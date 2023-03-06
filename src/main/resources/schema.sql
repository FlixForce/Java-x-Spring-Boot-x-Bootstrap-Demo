CREATE TABLE IF NOT EXISTS roles(
                      id INTEGER PRIMARY KEY,
                      name VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS login_user(
                           id INTEGER PRIMARY KEY,
                           name VARCHAR(128) NOT NULL,
                           email VARCHAR(256) NOT NULL,
                           password VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_role(
                          user_id INTEGER,
                          role_id INTEGER,
                          CONSTRAINT fk_user_role PRIMARY KEY (user_id, role_id),
                          CONSTRAINT fk_user_role_id FOREIGN KEY (user_id) REFERENCES login_user(id),
                          CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE IF NOT EXISTS studio_member(
                              id INTEGER PRIMARY KEY NOT NULL,
                              name VARCHAR(128) NOT NULL,
                              furigana VARCHAR(128),
                              address VARCHAR(128) NOT NULL,
                              phone VARCHAR(24) NOT NULL,
                              email VARCHAR(256) NOT NULL,
                              artist_name VARCHAR(128),
                              member_count SMALLINT,
                              note text,
                              registration_date TIMESTAMP without time zone NOT NULL,
                              CONSTRAINT fk_studio_user FOREIGN KEY (id) REFERENCES login_user(id)
);

CREATE TABLE IF NOT EXISTS reserve_info(
                             id SERIAL PRIMARY KEY NOT NULL,
                             member_id INTEGER NOT NULL,
                             studio_type VARCHAR(16) NOT NULL,
                             start_datetime TIMESTAMP without time zone NOT NULL,
                             end_datetime TIMESTAMP without time zone NOT NULL,
                             engineer boolean NOT NULL,
                             mastering boolean NOT NULL,
                             note text
);
