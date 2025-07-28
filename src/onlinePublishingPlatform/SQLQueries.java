package onlinePublishingPlatform;

public class SQLQueries {
    public static final String Schema = """
    CREATE TABLE IF NOT EXISTS `users` (
    `user_id` integer PRIMARY KEY AUTO_INCREMENT,
    `username` varchar(255),
    `email` varchar(255),
    `password` varchar(255),
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
    """;

    public static final String DummyData = """
    INSERT INTO roles (role_name) VALUES 
    ('admin'), 
    ('editor'), 
    ('author'),  
    ('subscriber');

    INSERT INTO users (username, email, password, first_name, last_name, created_at, profile_picture_url) VALUES
    ('johndoe', 'john.doe@example.com', 'password_1', 'John', 'Doe', '2023-01-15 09:30:00', 'https://example.com/profiles/john.jpg'),
    ('janedoe', 'jane.doe@example.com', 'password_2', 'Jane', 'Doe', '2023-02-20 10:15:00', 'https://example.com/profiles/jane.jpg'),
    ('bobsmith', 'bob.smith@example.com', 'password_3', 'Bob', 'Smith', '2023-03-10 11:45:00', 'https://example.com/profiles/bob.jpg'),
    ('alicej', 'alice.johnson@example.com', 'password_4', 'Alice', 'Johnson', '2023-04-05 14:20:00', 'https://example.com/profiles/alice.jpg'),
    ('mikebrown', 'mike.brown@example.com', 'password_5', 'Mike', 'Brown', '2023-05-12 08:00:00', 'https://example.com/profiles/mike.jpg'),
    ('sarahlee', 'sarah.lee@example.com', 'password_6', 'Sarah', 'Lee', '2023-06-18 13:10:00', 'https://example.com/profiles/sarah.jpg'),
    ('davidwilson', 'david.wilson@example.com', 'password_7', 'David', 'Wilson', '2023-07-22 16:30:00', 'https://example.com/profiles/david.jpg'),
    ('emilydavis', 'emily.davis@example.com', 'password_8', 'Emily', 'Davis', '2023-08-30 10:45:00', 'https://example.com/profiles/emily.jpg');

    INSERT INTO user_roles (user_id, role_id) VALUES
    (1, 1), 
    (1, 3), 
    (2, 2), 
    (3, 3), 
    (4, 3), 
    (5, 4), 
    (6, 4), 
    (7, 4), 
    (8, 4); 

    INSERT INTO authors (user_id, bio) VALUES
    (1, 'John Doe is a seasoned writer with 10 years of experience in technology journalism.'),
    (3, 'Bob Smith specializes in business analysis and market trends.'),
    (4, 'Alice Johnson covers health and wellness topics with a scientific approach.');

    INSERT INTO categories (name, slug, description) VALUES
    ('Technology', 'technology', 'Articles about the latest in tech and innovation'),
    ('Business', 'business', 'Insights into business strategies and market trends'),
    ('Health', 'health', 'Health tips, medical news, and wellness advice'),
    ('Science', 'science', 'Discoveries and research across scientific disciplines'),
    ('Lifestyle', 'lifestyle', 'Daily life tips and cultural trends');

    INSERT INTO tags (name, slug) VALUES
    ('AI', 'artificial-intelligence'),
    ('Blockchain', 'blockchain'),
    ('Finance', 'finance'),
    ('Nutrition', 'nutrition'),
    ('Fitness', 'fitness'),
    ('Space', 'space'),
    ('Productivity', 'productivity'),
    ('Remote Work', 'remote-work');

    INSERT INTO series (name, description, author_id) VALUES
    ('Tech Trends 2023', 'A comprehensive look at emerging technologies in 2023', 1),
    ('Startup Success', 'Strategies for building and growing successful startups', 2),
    ('Mindful Living', 'A guide to mental health and balanced lifestyle', 3);

    INSERT INTO articles (title, slug, content_draft, content_published, status, created_at, published_at, primary_category_id, author_id, series_id, is_premium, view_count) VALUES
    ('The Future of AI', 'future-of-ai', 'Draft content about AI...', 'Published content about AI...', 'published', '2023-01-20', '2023-01-25', 1, 1, 1, false, 1500),
    ('Blockchain in Finance', 'blockchain-finance', 'Draft content about blockchain...', 'Published content about blockchain...', 'published', '2023-02-15', '2023-02-20', 2, 2, 2, true, 1200),
    ('Mindfulness Techniques', 'mindfulness-techniques', 'Draft content about mindfulness...', NULL, 'in_review', '2023-03-10', NULL, 3, 3, 3, false, 0),
    ('Quantum Computing Explained', 'quantum-computing', 'Draft content about quantum...', NULL, 'draft', '2023-04-05', NULL, 1, 1, NULL, true, 0),
    ('Healthy Eating Habits', 'healthy-eating', 'Draft content about nutrition...', 'Published content about nutrition...', 'published', '2023-05-12', '2023-05-18', 3, 3, NULL, false, 800),
    ('Startup Funding Strategies', 'startup-funding', 'Draft content about funding...', 'Published content about funding...', 'published', '2023-06-20', '2023-06-25', 2, 2, 2, true, 950),
    ('The Science of Sleep', 'science-of-sleep', 'Draft content about sleep...', NULL, 'in_review', '2023-07-15', NULL, 4, 3, NULL, false, 0),
    ('Remote Work Productivity', 'remote-productivity', 'Draft content about remote work...', 'Published content about remote work...', 'published', '2023-08-10', '2023-08-15', 5, 1, NULL, false, 1100),
    ('Tech Gadgets 2023', 'tech-gadgets-2023', 'Draft content about gadgets...', NULL, 'archived', '2023-01-05', '2023-01-10', 1, 1, 1, false, 300),
    ('Financial Planning Basics', 'financial-planning', 'Draft content about finance...', 'Published content about finance...', 'published', '2023-09-01', '2023-09-10', 2, 2, NULL, true, 700);

    INSERT INTO article_tags (article_id, tag_id) VALUES
    (1, 1), (1, 7),
    (2, 2), (2, 3),
    (3, 4), (3, 5),
    (4, 1), (4, 6),
    (5, 4), 
    (6, 3), (6, 7),
    (7, 4), (7, 5),
    (8, 7), (8, 8),
    (9, 1),
    (10, 3), (10, 7);

    INSERT INTO article_reviews (article_id, reviewer_id, status, feedback_notes, created_at) VALUES
    (3, 2, 'needs_changes', 'Please expand the section on meditation techniques and add references.', '2023-03-12'),
    (7, 2, 'approved', 'Well-researched and clearly written. Ready for publication.', '2023-07-17');

    INSERT INTO article_purchases (user_id, article_id, primary_category_id, amount_paid, currency, purchase_date) VALUES
    (5, 1, 1, 1.99, 'USD', '2023-01-26 10:35:00'),  -- Future of AI
    (6, 1, 1, 1.99, 'USD', '2023-01-27 11:40:00'),
    (7, 1, 1, 1.99, 'USD', '2023-01-28 09:15:00'),
    (5, 4, 1, 2.99, 'USD', '2023-04-10 14:20:00'),  -- Quantum Computing
    (6, 4, 1, 2.99, 'USD', '2023-04-11 16:30:00'),
    (8, 9, 1, 1.49, 'USD', '2023-01-15 13:45:00'),  -- Tech Gadgets
    (5, 2, 2, 3.99, 'USD', '2023-02-21 11:25:00'),  -- Blockchain in Finance
    (6, 2, 2, 3.99, 'USD', '2023-02-22 10:15:00'),
    (7, 2, 2, 3.99, 'USD', '2023-02-23 14:30:00'),
    (8, 2, 2, 3.99, 'USD', '2023-02-24 16:45:00'),
    (5, 6, 2, 2.99, 'USD', '2023-06-25 09:20:00'),  -- Startup Funding
    (6, 6, 2, 2.99, 'USD', '2023-06-26 11:35:00'),
    (7, 10, 2, 1.99, 'USD', '2023-09-12 13:50:00'), -- Financial Planning
    (8, 10, 2, 1.99, 'USD', '2023-09-13 15:05:00'),
    (5, 3, 3, 1.49, 'USD', '2023-03-15 10:10:00'),  -- Mindfulness
    (6, 5, 3, 0.99, 'USD', '2023-05-21 12:25:00'),  -- Healthy Eating
    (7, 5, 3, 0.99, 'USD', '2023-05-22 14:40:00'),
    (8, 5, 3, 0.99, 'USD', '2023-05-23 16:55:00'),
    (5, 8, 5, 1.99, 'USD', '2023-08-17 09:30:00'),  -- Remote Work
    (6, 8, 5, 1.99, 'USD', '2023-08-18 11:45:00'),
    (7, 8, 5, 1.99, 'USD', '2023-08-19 14:00:00');

    INSERT INTO comments (article_id, user_id, content, created_at, parent_comment_id) VALUES
    (1, 5, 'Great article! Very insightful.', '2023-01-26 10:30:00', NULL),
    (1, 6, 'I disagree with some points about AI ethics.', '2023-01-26 14:45:00', 1),
    (1, 7, 'Can you recommend more resources on this topic?', '2023-01-27 09:15:00', NULL),
    (2, 8, 'Blockchain will revolutionize finance!', '2023-02-21 11:20:00', NULL),
    (5, 5, 'These tips helped me improve my diet.', '2023-05-20 16:30:00', NULL),
    (5, 6, 'What about protein intake for athletes?', '2023-05-21 10:10:00', 5),
    (8, 7, 'Remote work has changed my productivity completely.', '2023-08-16 13:25:00', NULL);

    INSERT INTO article_views (article_id, user_id, view_timestamp, ip_address) VALUES
    (1, 5, '2023-01-26 10:25:00', '192.168.1.1'),
    (1, 6, '2023-01-26 14:40:00', '192.168.1.2'),
    (1, 7, '2023-01-27 09:10:00', '192.168.1.3'),
    (1, NULL, '2023-01-28 11:30:00', '203.0.113.45'),
    (1, NULL, '2023-01-29 15:20:00', '198.51.100.22'),
    (2, 8, '2023-02-21 11:15:00', '192.168.1.4'),
    (2, NULL, '2023-02-22 09:45:00', '203.0.113.78'),
    (5, 5, '2023-05-20 16:25:00', '192.168.1.1'),
    (5, 6, '2023-05-21 10:05:00', '192.168.1.2'),
    (8, 7, '2023-08-16 13:20:00', '192.168.1.3'),
    (8, NULL, '2023-08-17 14:30:00', '198.51.100.33');

    INSERT INTO article_votes (user_id, article_id, vote_type, voted_at) VALUES
    (5, 1, 'upvote', '2023-01-26 10:35:00'),
    (6, 1, 'downvote', '2023-01-26 14:50:00'),
    (7, 1, 'upvote', '2023-01-27 09:20:00'),
    (8, 2, 'upvote', '2023-02-21 11:25:00'),
    (5, 5, 'upvote', '2023-05-20 16:35:00'),
    (7, 8, 'upvote', '2023-08-16 13:30:00');

    
    INSERT INTO subscription_plans (name, price_monthly, price_annually, description, is_active) VALUES
    ('Basic', 9.99, 99.99, 'Access to standard articles', true),
    ('Premium', 19.99, 199.99, 'Access to premium content and early releases', true),
    ('Pro', 29.99, 299.99, 'All features plus exclusive content and community access', true);

    INSERT INTO subscriptions (user_id, plan_id, start_date, end_date, status) VALUES
    (1, 1, '2023-01-01', '2024-01-01', 'active'),
    (2, 1, '2023-02-01', '2023-05-01', 'expired'),
    (3, 1, '2023-03-01', NULL, 'active'),
    (4, 1, '2023-04-01', '2023-07-01', 'canceled'),
    (5, 2, '2023-01-15', '2024-01-15', 'active'),
    (6, 2, '2023-02-15', '2023-05-15', 'expired'),
    (7, 2, '2023-03-15', NULL, 'canceled'),
    (8, 3, '2023-01-10', '2024-01-10', 'active');
    """;

    public static final String articlesPerAuthorPerMonthQuery = """
    SELECT 
        a.author_id,
        u.username AS author_name,
        DATE_FORMAT(ar.published_at, '%Y-%m') AS month,
        COUNT(ar.article_id) AS articles_published
    FROM 
        articles ar
    JOIN 
        authors a ON ar.author_id = a.author_id
    JOIN 
        users u ON a.user_id = u.user_id
    WHERE 
        ar.status = 'published'
    GROUP BY 
        a.author_id, u.username, DATE_FORMAT(ar.published_at, '%Y-%m')
    ORDER BY 
        month, articles_published DESC
    """;

    public static final String pendingArticlesInReviewQuery = """
    SELECT 
        a.article_id,
        a.title,
        a.created_at,
        u.username AS author_name,
        DATEDIFF(NOW(), a.created_at) AS days_in_review,
        ar.status AS review_status,
        ar.feedback_notes
    FROM 
        articles a
    JOIN 
        authors au ON a.author_id = au.author_id
    JOIN 
        users u ON au.user_id = u.user_id
    LEFT JOIN
        article_reviews ar ON a.article_id = ar.article_id
    WHERE 
        a.status = 'in_review'
    ORDER BY 
        a.created_at ASC
    """;

    public static final String revenuePerCategoryQuery = """
    SELECT 
        c.category_id,
        c.name AS category_name,
        COUNT(ap.purchase_id) AS total_purchases,
        SUM(ap.amount_paid) AS total_revenue,
        AVG(ap.amount_paid) AS avg_purchase_amount
    FROM 
        article_purchases ap
    JOIN 
        categories c ON ap.primary_category_id = c.category_id
    GROUP BY 
        c.category_id, c.name
    ORDER BY 
        total_revenue DESC
    """;

    public static final String subscriberChurnRateQuery = """
    SELECT 
        sp.plan_id,
        sp.name AS plan_name,
        COUNT(CASE WHEN s.status = 'active' THEN 1 END) AS active_subscribers,
        COUNT(CASE WHEN s.status = 'canceled' THEN 1 END) AS canceled_subscribers,
        COUNT(CASE WHEN s.status = 'expired' THEN 1 END) AS expired_subscribers,
        COUNT(*) AS total_subscribers,
        ROUND(
            (COUNT(CASE WHEN s.status IN ('canceled', 'expired') THEN 1 END) * 100.0 / 
            GREATEST(COUNT(*), 1)), 
            2
        ) AS churn_rate_percentage
    FROM 
        subscriptions s
    JOIN 
        subscription_plans sp ON s.plan_id = sp.plan_id
    GROUP BY 
        sp.plan_id, sp.name
    ORDER BY 
        churn_rate_percentage DESC
    """;

    public static final String topViewedArticlesByCategoryQuery = """
    WITH ranked_articles AS (
        SELECT 
            c.category_id,
            c.name AS category_name,
            a.article_id,
            a.title,
            a.view_count,
            u.username AS author_name,
            RANK() OVER (PARTITION BY c.category_id ORDER BY a.view_count DESC) AS rank_in_category
        FROM 
            articles a
        JOIN 
            categories c ON a.primary_category_id = c.category_id
        JOIN 
            authors au ON a.author_id = au.author_id
        JOIN 
            users u ON au.user_id = u.user_id
        WHERE 
            a.status = 'published'
    )
    SELECT * FROM ranked_articles
    WHERE rank_in_category <= 3
    ORDER BY category_name, view_count DESC;
    """;

}
