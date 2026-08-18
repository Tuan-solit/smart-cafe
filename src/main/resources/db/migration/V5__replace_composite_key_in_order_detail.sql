-- Drop foreign key constraints first
ALTER TABLE order_detail
    DROP FOREIGN KEY fk_order_detail_order;

ALTER TABLE order_detail
    DROP FOREIGN KEY fk_order_detail_product;

-- Drop the composite primary key
ALTER TABLE order_detail
    DROP PRIMARY KEY;

-- Add new single primary key column
ALTER TABLE order_detail
    ADD COLUMN order_detail_id INT NOT NULL AUTO_INCREMENT UNIQUE FIRST;

-- Set it as primary key
ALTER TABLE order_detail
    ADD PRIMARY KEY (order_detail_id);

-- Recreate foreign keys
ALTER TABLE order_detail
    ADD CONSTRAINT fk_order_detail_order
        FOREIGN KEY (order_id) REFERENCES orders (order_id);

ALTER TABLE order_detail
    ADD CONSTRAINT fk_order_detail_product
        FOREIGN KEY (product_id) REFERENCES products (product_id);