USE smart_city;
INSERT IGNORE INTO departments(name,description) VALUES
 ('Roads & Infrastructure','Road and infrastructure repairs'),('Sanitation & Waste','Waste collection and sanitation'),
 ('Electricity','Streetlights and electrical hazards'),('Water Supply','Water pipelines and supply'),
 ('Public Safety','Public safety incidents'),('General Administration','General municipal issues');
INSERT IGNORE INTO users(name,email,password_hash,role) VALUES
 ('City Admin','admin@city.com','$2a$10$2gWqVtPz2zGmEo2Q4j5rWe4m8N8P8u7yLz1mA2pQ5vC7rT9sK2dOa','ADMIN'),
 ('Demo Citizen','citizen@city.com','$2a$10$2gWqVtPz2zGmEo2Q4j5rWe4m8N8P8u7yLz1mA2pQ5vC7rT9sK2dOa','CITIZEN');
