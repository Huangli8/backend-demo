-- 用户与权限
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role VARCHAR(20) DEFAULT 'USER',
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE user_profiles (
                               id BIGINT PRIMARY KEY,
                               full_name VARCHAR(100),
                               gender VARCHAR(10),
                               age INT,
                               family_size INT,
                               FOREIGN KEY (id) REFERENCES users(id)
);

-- 兴趣标签
CREATE TABLE tags (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(100) NOT NULL,
                      category VARCHAR(50)
);

CREATE TABLE user_tags (
                           user_id BIGINT,
                           tag_id BIGINT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id, tag_id),
                           FOREIGN KEY (user_id) REFERENCES users(id),
                           FOREIGN KEY (tag_id) REFERENCES tags(id)
);

-- 目的地与景点
CREATE TABLE destinations (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              name VARCHAR(100) NOT NULL,
                              country VARCHAR(100),
                              description TEXT,
                              image_url VARCHAR(255)
);

CREATE TABLE attractions (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             destination_id BIGINT,
                             name VARCHAR(100) NOT NULL,
                             description TEXT,
                             image_url VARCHAR(255),
                             popularity_score INT DEFAULT 0,
                             FOREIGN KEY (destination_id) REFERENCES destinations(id)
);

-- 推荐记录
CREATE TABLE recommendations (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 user_id BIGINT,
                                 attraction_id BIGINT,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 FOREIGN KEY (user_id) REFERENCES users(id),
                                 FOREIGN KEY (attraction_id) REFERENCES attractions(id)
);

-- 旅行方案
CREATE TABLE trip_plans (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT,
                            destination_id BIGINT,
                            start_date DATE,
                            end_date DATE,
                            budget_min DECIMAL(10,2),
                            budget_max DECIMAL(10,2),
                            total_estimated_cost DECIMAL(10,2),
                            plan_type VARCHAR(50),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (user_id) REFERENCES users(id),
                            FOREIGN KEY (destination_id) REFERENCES destinations(id)
);

CREATE TABLE trip_days (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           trip_plan_id BIGINT,
                           day_number INT,
                           notes TEXT,
                           FOREIGN KEY (trip_plan_id) REFERENCES trip_plans(id)
);

CREATE TABLE trip_activities (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 trip_day_id BIGINT,
                                 activity_type VARCHAR(50),
                                 reference_id BIGINT,
                                 description TEXT,
                                 estimated_cost DECIMAL(10,2),
                                 time_slot ENUM('morning','afternoon','evening'),
                                 FOREIGN KEY (trip_day_id) REFERENCES trip_days(id)
);

-- 方案对比与修改
CREATE TABLE plan_comparisons (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  user_id BIGINT,
                                  plan_ids JSON,
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE plan_revisions (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                trip_plan_id BIGINT,
                                change_summary JSON,
                                new_estimated_cost DECIMAL(10,2),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (trip_plan_id) REFERENCES trip_plans(id)
);

-- 预订与订单
CREATE TABLE bookings (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          trip_plan_id BIGINT,
                          service_type VARCHAR(50),
                          provider_name VARCHAR(100),
                          provider_url VARCHAR(255),
                          price DECIMAL(10,2),
                          status VARCHAR(20),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (trip_plan_id) REFERENCES trip_plans(id)
);

CREATE TABLE booking_options (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 booking_id BIGINT,
                                 platform_name VARCHAR(100),
                                 price DECIMAL(10,2),
                                 link VARCHAR(255),
                                 extra_info JSON,
                                 FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- 景点门票代抢
CREATE TABLE ticket_reservations (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     user_id BIGINT,
                                     attraction_id BIGINT,
                                     auth_status VARCHAR(20),
                                     valid_until DATE,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (user_id) REFERENCES users(id),
                                     FOREIGN KEY (attraction_id) REFERENCES attractions(id)
);

CREATE TABLE ticket_auto_settings (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      ticket_reservation_id BIGINT,
                                      ticket_count INT,
                                      max_price DECIMAL(10,2),
                                      preferred_date DATE,
                                      status VARCHAR(20),
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      FOREIGN KEY (ticket_reservation_id) REFERENCES ticket_reservations(id)
);
