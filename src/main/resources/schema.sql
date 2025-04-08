CREATE TABLE items (
                       id IDENTITY PRIMARY KEY,
                       title VARCHAR(255),
                       description VARCHAR(1000),
                       price DECIMAL,
                       count INT,
                       img_path VARCHAR(255)
);

CREATE TABLE orders (
                        id IDENTITY PRIMARY KEY
);

CREATE TABLE order_items (
                             id IDENTITY PRIMARY KEY,
                             order_id BIGINT,
                             item_id BIGINT,
                             quantity INT
);
