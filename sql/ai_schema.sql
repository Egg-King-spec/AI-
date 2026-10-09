CREATE DATABASE IF NOT EXISTS mental_health_assistant DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mental_health_assistant;

CREATE TABLE IF NOT EXISTS user (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50) DEFAULT NULL,
    avatar VARCHAR(255) DEFAULT NULL,
    gender TINYINT DEFAULT NULL,
    birthday DATE DEFAULT NULL,
    user_type TINYINT NOT NULL DEFAULT 1 COMMENT '1普通用户 2管理员',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    UNIQUE KEY uk_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS consultation_session (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    session_title VARCHAR(200) DEFAULT NULL,
    started_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    last_emotion_analysis LONGTEXT DEFAULT NULL,
    last_emotion_updated_at DATETIME DEFAULT NULL,
    PRIMARY KEY (id),
    KEY idx_session_user (user_id),
    KEY idx_session_started (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS consultation_message (
    id BIGINT NOT NULL AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    sender_type TINYINT NOT NULL COMMENT '1用户 2AI',
    message_type TINYINT NOT NULL DEFAULT 1 COMMENT '1文本',
    content TEXT NOT NULL,
    emotion_tag VARCHAR(50) DEFAULT NULL,
    ai_model VARCHAR(50) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_message_session (session_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS knowledge_category (
    id BIGINT NOT NULL AUTO_INCREMENT,
    category_name VARCHAR(80) NOT NULL,
    parent_id BIGINT DEFAULT 0,
    sort_order INT DEFAULT 0,
    status TINYINT DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_category_parent (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS knowledge_article (
    id VARCHAR(64) NOT NULL,
    title VARCHAR(200) NOT NULL,
    summary VARCHAR(1000) DEFAULT NULL,
    content LONGTEXT NOT NULL,
    cover_image VARCHAR(512) DEFAULT NULL,
    category_id BIGINT NOT NULL,
    tags VARCHAR(500) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0草稿 1已发布 2已下线',
    author_id BIGINT DEFAULT NULL,
    author_name VARCHAR(80) DEFAULT NULL,
    read_count INT NOT NULL DEFAULT 0,
    published_at DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_article_category (category_id),
    KEY idx_article_status (status),
    KEY idx_article_published (published_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS emotion_diary (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    diary_date DATE NOT NULL,
    emotion_tag VARCHAR(30) NOT NULL,
    emotion_score INT NOT NULL DEFAULT 50,
    content VARCHAR(5000) NOT NULL,
    ai_analysis TEXT DEFAULT NULL,
    risk_level TINYINT NOT NULL DEFAULT 0,
    suggestion VARCHAR(500) DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_diary_user_date (user_id, diary_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO knowledge_category (id, category_name, parent_id, sort_order, status)
VALUES
    (1, '情绪管理', 0, 1, 1),
    (2, '压力缓解', 0, 2, 1),
    (3, '人际关系', 0, 3, 1),
    (4, '自我成长', 0, 4, 1),
    (5, '睡眠与放松', 0, 5, 1)
ON DUPLICATE KEY UPDATE category_name = VALUES(category_name), status = VALUES(status);

INSERT INTO user (username, email, phone, password, nickname, user_type, status)
VALUES ('admin', 'admin@example.com', '13800000000',
        '$2a$10$K8ME2/nFlEo9XbHxJBPm6.jZMhXGVpaKbqT3ss7YP83AJHuoggH/y',
        '平台管理员', 2, 1)
ON DUPLICATE KEY UPDATE user_type = 2, status = 1;
