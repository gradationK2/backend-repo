CREATE TABLE IF NOT EXISTS food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    name_ko VARCHAR(255) NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    scoville INT,
    image_url TEXT
);