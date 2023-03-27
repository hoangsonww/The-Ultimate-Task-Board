USE mysql2;

CREATE TABLE tasks (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(1000),
  status VARCHAR(20) NOT NULL,
  priority VARCHAR(20) NOT NULL,
  due_date DATE NOT NULL,
  PRIMARY KEY (id)
);
