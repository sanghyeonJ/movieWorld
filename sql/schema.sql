use movieworlddb;

-- 권한 테이블
CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '권한 ID',
    name VARCHAR(20) NOT NULL UNIQUE COMMENT '권한명 (ROLE_USER, ROLE_ADMIN)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='권한 테이블';

INSERT INTO roles (name) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- 유저테이블
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '회원 ID',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일 (로그인 ID)',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호 (BCrypt)',
    name VARCHAR(50) NOT NULL COMMENT '이름',
    role_id BIGINT NOT NULL COMMENT '권한 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '가입일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_email (email),
    INDEX idx_role_id (role_id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='회원 테이블';


-- 장르테이블
CREATE TABLE genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '장르 ID',
    api_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'API에서 가져온 장르 ID (TMDB 등)',
    name VARCHAR(100) NOT NULL COMMENT '장르 이름 (예: 액션, 드라마)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    INDEX idx_api_id (api_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='장르 테이블';

-- 영화테이블
CREATE TABLE movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '영화 ID (로컬)',
    api_id VARCHAR(50) NOT NULL UNIQUE COMMENT 'API에서 가져온 영화 ID',
    title VARCHAR(200) NOT NULL COMMENT '영화 제목',
    original_title VARCHAR(200) COMMENT '원제',
    poster_url VARCHAR(500) COMMENT '포스터 이미지 URL',
    backdrop_url VARCHAR(500) COMMENT '배경 이미지 URL',
    overview TEXT COMMENT '줄거리',
    release_date DATE COMMENT '개봉일',
    rating DECIMAL(3,1) COMMENT 'API 평점',
    vote_count INT DEFAULT 0 COMMENT '평점 투표 수',
    synced_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'API 동기화 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'DB 저장일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_api_id (api_id),
    INDEX idx_title (title),
    INDEX idx_release_date (release_date),
    INDEX idx_synced_at (synced_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='영화 정보 테이블';

-- 영화 장르 매핑테이블
CREATE TABLE movie_genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '매핑 ID',
    movie_id BIGINT NOT NULL COMMENT '영화 ID',
    genre_id BIGINT NOT NULL COMMENT '장르 ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    UNIQUE KEY uk_movie_genre (movie_id, genre_id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_genre_id (genre_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='영화-장르 다대다 매핑 테이블';

-- 리뷰 테이블
CREATE TABLE reviews (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '리뷰 ID',
    user_id BIGINT NOT NULL COMMENT '작성자 ID',
    movie_id BIGINT NOT NULL COMMENT '영화 ID',
    rating INT NOT NULL COMMENT '평점 (1~5)',
    content TEXT NOT NULL COMMENT '리뷰 내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_user_id (user_id),
    INDEX idx_movie_id (movie_id),
    INDEX idx_updated_at (updated_at),
    UNIQUE KEY uk_user_movie (user_id, movie_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    CHECK (rating >= 1 AND rating <= 5)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='영화 리뷰 테이블';

-- 게시판 테이블
CREATE TABLE boards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '게시글 ID',
    user_id BIGINT NOT NULL COMMENT '작성자 ID',
    title VARCHAR(200) NOT NULL COMMENT '제목',
    content TEXT NOT NULL COMMENT '내용',
    view_count INT DEFAULT 0 COMMENT '조회수',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    INDEX idx_title (title),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시판 테이블';

-- 게시판 댓글 테이블
CREATE TABLE board_comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '댓글 ID',
    board_id BIGINT NOT NULL COMMENT '게시글 ID',
    user_id BIGINT NOT NULL COMMENT '작성자 ID',
    content TEXT NOT NULL COMMENT '댓글 내용',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '작성일',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일',
    INDEX idx_board_id (board_id),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='게시판 댓글 테이블';

-- 추천 결과 저장테이블
CREATE TABLE recommendations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '추천 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    review_count INT NOT NULL COMMENT '추천 시점의 리뷰 개수',
    review_last_updated_at DATETIME NOT NULL COMMENT '추천 시점의 리뷰 최종 수정 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '추천 생성일',
    is_valid BOOLEAN DEFAULT TRUE COMMENT '유효 여부 (리뷰 변경 시 false)',
    INDEX idx_user_id (user_id),
    INDEX idx_user_valid (user_id, is_valid),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 추천 결과 캐시 테이블';

-- 추천 결과에 대한 영화 테이블
CREATE TABLE recommendation_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '추천 항목 ID',
    recommendation_id BIGINT NOT NULL COMMENT '추천 ID',
    movie_id BIGINT NOT NULL COMMENT '영화 ID',
    item_rank INT NOT NULL COMMENT '추천 순위 (1부터)',
    reason TEXT COMMENT '추천 이유',
    confidence_score DECIMAL(3,2) COMMENT '추천 신뢰도 (0.00~1.00)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    INDEX idx_recommendation_id (recommendation_id),
    INDEX idx_movie_id (movie_id),
    FOREIGN KEY (recommendation_id) REFERENCES recommendations(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='추천 결과의 영화별 항목 테이블';

-- 추천 호출 제한 테이블
CREATE TABLE recommend_rate_limits (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '레코드 ID',
    user_id BIGINT NOT NULL COMMENT '사용자 ID',
    date DATE NOT NULL COMMENT '날짜 (1일 제한 기준)',
    count INT DEFAULT 0 COMMENT '해당 날짜 추천 요청 횟수',
    last_request_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '마지막 요청 시간',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '생성일',
    UNIQUE KEY uk_user_date (user_id, date),
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='추천 API 호출 제한(일별) 테이블';

-- 영화 동기화 로그 테이블
CREATE TABLE movie_sync_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '로그 ID',
    sync_type VARCHAR(50) NOT NULL COMMENT '동기화 타입 (POPULAR, NOW_PLAYING, UPCOMING)',
    total_count INT DEFAULT 0 COMMENT '가져온 영화 총 개수',
    success_count INT DEFAULT 0 COMMENT '저장 성공 개수',
    fail_count INT DEFAULT 0 COMMENT '저장 실패 개수',
    started_at DATETIME NOT NULL COMMENT '시작 시간',
    completed_at DATETIME COMMENT '완료 시간',
    status VARCHAR(20) DEFAULT 'RUNNING' COMMENT '상태 (RUNNING, SUCCESS, FAILED)',
    error_message TEXT COMMENT '에러 메시지 (실패 시)',
    INDEX idx_started_at (started_at),
    INDEX idx_status (status),
    INDEX idx_sync_type (sync_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='영화 API 동기화 로그 테이블';

