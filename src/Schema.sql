CREATE TABLE IF NOT EXISTS `users` (
  `user_id` integer PRIMARY KEY AUTO_INCREMENT,
  `username` varchar(255),
  `email` varchar(255),
  `password_hash` varchar(255),
  `first_name` varchar(255),
  `last_name` varchar(255),
  `created_at` timestamp,
  `profile_picture_url` varchar(255)
);

CREATE TABLE IF NOT EXISTS `roles` (
  `role_id` integer PRIMARY KEY AUTO_INCREMENT,
  `role_name` varchar(255)
);

CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` integer,
  `role_id` integer
);

CREATE TABLE IF NOT EXISTS `authors` (
  `author_id` integer PRIMARY KEY AUTO_INCREMENT,
  `user_id` integer,
  `bio` text
);

CREATE TABLE `categories` (
  `category_id` integer PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `slug` varchar(255),
  `description` text
);

CREATE TABLE IF NOT EXISTS `series` (
  `series_id` integer PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `description` text,
  `author_id` integer
);

CREATE TABLE IF NOT EXISTS `articles` (
  `article_id` integer PRIMARY KEY AUTO_INCREMENT,
  `title` varchar(255),
  `slug` varchar(255),
  `content_draft` text,
  `content_published` text,
  `status` ENUM ('draft', 'in_review', 'published', 'archived'),
  `created_at` timestamp,
  `published_at` timestamp,
  `primary_category_id` integer,
  `author_id` integer,
  `series_id` integer,
  `is_premium` boolean,
  `view_count` bigint
);

CREATE TABLE IF NOT EXISTS `tags` (
  `tag_id` integer PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `slug` varchar(255)
);

CREATE TABLE IF NOT EXISTS `article_tags` (
  `article_id` integer,
  `tag_id` integer
);

CREATE TABLE IF NOT EXISTS `article_reviews` (
  `review_id` integer PRIMARY KEY AUTO_INCREMENT,
  `article_id` integer,
  `reviewer_id` integer,
  `status` ENUM ('approved', 'rejected', 'needs_changes'),
  `feedback_notes` text,
  `created_at` timestamp
);

CREATE TABLE IF NOT EXISTS `subscription_plans` (
  `plan_id` integer PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(255),
  `price_monthly` decimal,
  `price_annually` decimal,
  `description` text,
  `is_active` boolean
);

CREATE TABLE IF NOT EXISTS `subscriptions` (
  `subscription_id` integer PRIMARY KEY AUTO_INCREMENT,
  `user_id` integer,
  `plan_id` integer,
  `start_date` timestamp,
  `end_date` timestamp,
  `status` ENUM ('active', 'canceled', 'expired')
);

CREATE TABLE IF NOT EXISTS `article_purchases` (
  `purchase_id` integer PRIMARY KEY AUTO_INCREMENT,
  `user_id` integer,
  `article_id` integer,
  `primary_category_id` integer COMMENT 'Denormalized for faster revenue reporting per category.',
  `amount_paid` decimal,
  `currency` varchar(255),
  `purchase_date` timestamp
);

CREATE TABLE IF NOT EXISTS `comments` (
  `comment_id` integer PRIMARY KEY AUTO_INCREMENT,
  `article_id` integer,
  `user_id` integer,
  `content` text,
  `created_at` timestamp,
  `parent_comment_id` integer
);

CREATE TABLE IF NOT EXISTS `article_views` (
  `view_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `article_id` integer,
  `user_id` integer,
  `view_timestamp` timestamp,
  `ip_address` varchar(255)
);

CREATE TABLE IF NOT EXISTS `article_votes` (
  `user_id` integer,
  `article_id` integer,
  `vote_type` ENUM ('upvote', 'downvote'),
  `voted_at` timestamp
);

CREATE INDEX `idx_articles_author_status_published_at` ON `articles` (`author_id`, `status`, `published_at`);

CREATE INDEX `idx_articles_category_views` ON `articles` (`primary_category_id`, `view_count`);

CREATE INDEX `idx_articles_status_created_at` ON `articles` (`status`, `created_at`);

CREATE INDEX `idx_article_tags_tag_id_article_id` ON `article_tags` (`tag_id`, `article_id`);

CREATE INDEX `idx_subscriptions_plan_status_end_date` ON `subscriptions` (`plan_id`, `status`, `end_date`);

CREATE INDEX `idx_purchases_category_amount` ON `article_purchases` (`primary_category_id`, `amount_paid`);

ALTER TABLE `authors` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `user_roles` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `user_roles` ADD FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`);

ALTER TABLE `series` ADD FOREIGN KEY (`author_id`) REFERENCES `authors` (`author_id`);

ALTER TABLE `articles` ADD FOREIGN KEY (`author_id`) REFERENCES `authors` (`author_id`);

ALTER TABLE `articles` ADD FOREIGN KEY (`primary_category_id`) REFERENCES `categories` (`category_id`);

ALTER TABLE `articles` ADD FOREIGN KEY (`series_id`) REFERENCES `series` (`series_id`);

ALTER TABLE `article_tags` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);

ALTER TABLE `article_tags` ADD FOREIGN KEY (`tag_id`) REFERENCES `tags` (`tag_id`);

ALTER TABLE `article_reviews` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);

ALTER TABLE `article_reviews` ADD FOREIGN KEY (`reviewer_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `subscriptions` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `subscriptions` ADD FOREIGN KEY (`plan_id`) REFERENCES `subscription_plans` (`plan_id`);

ALTER TABLE `article_purchases` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `article_purchases` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);

ALTER TABLE `article_purchases` ADD FOREIGN KEY (`primary_category_id`) REFERENCES `categories` (`category_id`);

ALTER TABLE `comments` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `comments` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);

ALTER TABLE `comments` ADD FOREIGN KEY (`parent_comment_id`) REFERENCES `comments` (`comment_id`);

ALTER TABLE `article_views` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);

ALTER TABLE `article_views` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `article_votes` ADD FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

ALTER TABLE `article_votes` ADD FOREIGN KEY (`article_id`) REFERENCES `articles` (`article_id`);
