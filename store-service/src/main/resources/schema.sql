CREATE TABLE if not exists items (
                       id IDENTITY PRIMARY KEY,
                       title VARCHAR(255),
                       description VARCHAR(1000),
                       price DECIMAL,
                       count INT,
                       img_path VARCHAR(255)
);

CREATE TABLE if not exists orders (
                        id IDENTITY PRIMARY KEY,
                        CREATED_AT TIMESTAMP,
                        TOTAL_SUM FLOAT
);

CREATE TABLE if not exists order_items (
                             id IDENTITY PRIMARY KEY,
                             order_id BIGINT,
                             item_id BIGINT,
                             quantity INT
);
