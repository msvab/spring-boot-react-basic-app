CREATE TABLE products (
  id INTEGER,
  name VARCHAR(200) NOT NULL,
  description TEXT NOT NULL,
  CONSTRAINT pk_products PRIMARY KEY (id)
);

CREATE TABLE product_prices (
  id INTEGER,
  product_id INTEGER NOT NULL,
  currency VARCHAR(10) NOT NULL,
  amount DECIMAL(20,2) NOT NULL,
  CONSTRAINT pk_product_prices PRIMARY KEY (id),
  CONSTRAINT fk_product_prices_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE product_tags (
  product_id INTEGER,
  name VARCHAR(100),
  CONSTRAINT pk_product_tags PRIMARY KEY (product_id, name),
  CONSTRAINT fk_product_tags_products FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE SEQUENCE product_id_seq START WITH 1;
CREATE SEQUENCE product_price_id_seq START WITH 1;