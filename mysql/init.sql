-- ─── The Howard's Council - Database Schema ────────────────────────────────
-- This runs automatically when MySQL container starts for the first time.

CREATE DATABASE IF NOT EXISTS howards_council
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE howards_council;

-- ─── Gallery Images ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gallery_images (
  id          BIGINT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(255)    NOT NULL,
  description TEXT,
  image_url   TEXT            NOT NULL,
  s3_key      VARCHAR(500)    NOT NULL,
  category    VARCHAR(100)    DEFAULT 'general',
  is_active   TINYINT(1)      DEFAULT 1,
  sort_order  INT             DEFAULT 0,
  created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_gallery_active   (is_active),
  INDEX idx_gallery_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── Demo Enquiries ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS demo_enquiries (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_name     VARCHAR(255)    NOT NULL,
  age              INT             NOT NULL,
  phone_number     VARCHAR(20)     NOT NULL,
  course_interested VARCHAR(255),
  preferred_timing VARCHAR(100),
  status           ENUM('PENDING','CONTACTED','ENROLLED','CANCELLED') DEFAULT 'PENDING',
  notes            TEXT,
  created_at       DATETIME        DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_enquiry_status (status),
  INDEX idx_enquiry_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── Contact Messages ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS contact_messages (
  id              BIGINT AUTO_INCREMENT PRIMARY KEY,
  name            VARCHAR(255)    NOT NULL,
  phone           VARCHAR(20)     NOT NULL,
  email           VARCHAR(255),
  course_interest VARCHAR(255),
  message         TEXT,
  is_read         TINYINT(1)      DEFAULT 0,
  created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_message_read    (is_read),
  INDEX idx_message_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── Testimonials ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS testimonials (
  id               BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_name     VARCHAR(255)    NOT NULL,
  band_score       VARCHAR(20),
  course_name      VARCHAR(255),
  testimonial_text TEXT,
  photo_url        TEXT,
  photo_s3_key     VARCHAR(500),
  rating           INT             DEFAULT 5,
  is_active        TINYINT(1)      DEFAULT 1,
  sort_order       INT             DEFAULT 0,
  created_at       DATETIME        DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_testi_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ─── Seed: Sample Gallery Images (placeholder until real S3 images added) ───
INSERT IGNORE INTO gallery_images (id, title, description, image_url, s3_key, category, sort_order) VALUES
(1, 'Classroom Session', 'Interactive IELTS coaching class', 'https://howards-council-media.s3.ap-south-1.amazonaws.com/gallery/sample-classroom.jpg', 'gallery/sample-classroom.jpg', 'classroom', 1),
(2, 'Speaking Practice', 'Students practicing IELTS speaking module', 'https://howards-council-media.s3.ap-south-1.amazonaws.com/gallery/sample-speaking.jpg', 'gallery/sample-speaking.jpg', 'speaking', 2),
(3, 'Award Ceremony', 'Students celebrating their band score achievements', 'https://howards-council-media.s3.ap-south-1.amazonaws.com/gallery/sample-award.jpg', 'gallery/sample-award.jpg', 'award', 3);

-- ─── Seed: Sample Testimonials ───────────────────────────────────────────────
INSERT IGNORE INTO testimonials (id, student_name, band_score, course_name, testimonial_text, rating, sort_order) VALUES
(1, 'Rahul Sharma', 'Band 7.5', 'IELTS Academic · Canada PR', 'The Howard\'s Council completely transformed my IELTS preparation. I felt confident on exam day for the first time.', 5, 1),
(2, 'Priya Verma', 'Band 8.0', 'IELTS Academic · UK University', 'I scored Band 8.0 in my first attempt! The writing task feedback and vocabulary modules were exceptional.', 5, 2),
(3, 'Aman Khan', 'Band 7.0', 'IELTS General · Australia Migration', 'Small batches meant my teacher actually knew my weak points. Cleared listening and reading at Band 7+ easily.', 5, 3);
