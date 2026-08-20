ALTER TABLE tables
    ADD CONSTRAINT uk_tables_table_number
        UNIQUE (table_number);