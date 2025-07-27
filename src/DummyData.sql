INSERT INTO roles (role_name) VALUES 
('admin'), 
('editor'), 
('author'),  
('subscriber');

INSERT INTO users (username, email, password_hash, first_name, last_name, created_at, profile_picture_url) VALUES
('johndoe', 'john.doe@example.com', 'hashed_password_1', 'John', 'Doe', '2023-01-15 09:30:00', 'https://example.com/profiles/john.jpg'),
('janedoe', 'jane.doe@example.com', 'hashed_password_2', 'Jane', 'Doe', '2023-02-20 10:15:00', 'https://example.com/profiles/jane.jpg'),
('bobsmith', 'bob.smith@example.com', 'hashed_password_3', 'Bob', 'Smith', '2023-03-10 11:45:00', 'https://example.com/profiles/bob.jpg'),
('alicej', 'alice.johnson@example.com', 'hashed_password_4', 'Alice', 'Johnson', '2023-04-05 14:20:00', 'https://example.com/profiles/alice.jpg'),
('mikebrown', 'mike.brown@example.com', 'hashed_password_5', 'Mike', 'Brown', '2023-05-12 08:00:00', 'https://example.com/profiles/mike.jpg'),
('sarahlee', 'sarah.lee@example.com', 'hashed_password_6', 'Sarah', 'Lee', '2023-06-18 13:10:00', 'https://example.com/profiles/sarah.jpg'),
('davidwilson', 'david.wilson@example.com', 'hashed_password_7', 'David', 'Wilson', '2023-07-22 16:30:00', 'https://example.com/profiles/david.jpg'),
('emilydavis', 'emily.davis@example.com', 'hashed_password_8', 'Emily', 'Davis', '2023-08-30 10:45:00', 'https://example.com/profiles/emily.jpg');

INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- John is admin
(1, 3), -- John is also author
(2, 2), -- Jane is editor
(3, 3), -- Bob is author
(4, 3), -- Alice is author
(5, 4), -- Mike is subscriber
(6, 4), -- Sarah is subscriber
(7, 4), -- David is subscriber
(8, 4); -- Emily is subscriber

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

-- Insert tags
INSERT INTO tags (name, slug) VALUES
('AI', 'artificial-intelligence'),
('Blockchain', 'blockchain'),
('Finance', 'finance'),
('Nutrition', 'nutrition'),
('Fitness', 'fitness'),
('Space', 'space'),
('Productivity', 'productivity'),
('Remote Work', 'remote-work');

-- Insert series (using proper author_ids from the authors table)
INSERT INTO series (name, description, author_id) VALUES
('Tech Trends 2023', 'A comprehensive look at emerging technologies in 2023', 1),
('Startup Success', 'Strategies for building and growing successful startups', 2),
('Mindful Living', 'A guide to mental health and balanced lifestyle', 3);

-- Insert articles with proper author_id and series_id references
INSERT INTO articles (title, slug, content_draft, content_published, status, created_at, published_at, primary_category_id, author_id, series_id, is_premium, view_count) VALUES
('The Future of AI', 'future-of-ai', 'Draft content about AI...', 'Published content about AI...', 'published', '2023-01-20', '2023-01-25', 1, 1, 1, false, 1500),
('Blockchain in Finance', 'blockchain-finance', 'Draft content about blockchain...', 'Published content about blockchain...', 'published', '2023-02-15', '2023-02-20', 2, 2, 2, true, 1200),
('Mindfulness Techniques', 'mindfulness-techniques', 'Draft content about mindfulness...', NULL, 'in_review', '2023-03-10', NULL, 3, 3, 3, false, 0),
('Quantum Computing Explained', 'quantum-computing', 'Draft content about quantum...', NULL, 'draft', '2023-04-05', NULL, 1, 1, NULL, true, 0),
('Healthy Eating Habits', 'healthy-eating', 'Draft content about nutrition...', 'Published content about nutrition...', 'published', '2023-05-12', '2023-05-18', 3, 3, NULL, false, 800),
('Startup Funding Strategies', 'startup-funding', 'Draft content about funding...', 'Published content about funding...', 'published', '2023-06-20', '2023-06-25', 2, 2, 2, true, 950),
('The Science of Sleep', 'science-of-sleep', 'Draft content about sleep...', NULL, 'in_review', '2023-07-15', NULL, 4, 3, NULL, false, 0),
('Remote Work Productivity', 'remote-productivity', 'Draft content about remote work...', 'Published content about remote work...', 'published', '2023-01-10', '2023-01-15', 5, 1, NULL, false, 1100),
('Tech Gadgets 2023', 'tech-gadgets-2023', 'Draft content about gadgets...', NULL, 'archived', '2023-01-05', '2023-01-10', 1, 1, 1, false, 300),
('Financial Planning Basics', 'financial-planning', 'Draft content about finance...', 'Published content about finance...', 'published', '2023-09-01', '2023-09-10', 2, 2, NULL, true, 700);

-- Insert article tags
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

-- Insert article reviews
INSERT INTO article_reviews (article_id, reviewer_id, status, feedback_notes, created_at) VALUES
(3, 2, 'needs_changes', 'Please expand the section on meditation techniques and add references.', '2023-03-12'),
(7, 2, 'approved', 'Well-researched and clearly written. Ready for publication.', '2023-07-17');

-- Insert article purchases
INSERT INTO article_purchases (user_id, article_id, primary_category_id, amount_paid, currency, purchase_date) VALUES
(5, 2, 2, 1.99, 'USD', '2023-03-01'),
(6, 2, 2, 1.99, 'USD', '2023-03-05'),
(7, 6, 2, 1.99, 'USD', '2023-07-01'),
(8, 10, 2, 1.99, 'USD', '2023-09-15'),
(5, 6, 2, 1.99, 'USD', '2023-07-10');

-- Insert comments
INSERT INTO comments (article_id, user_id, content, created_at, parent_comment_id) VALUES
(1, 5, 'Great article! Very insightful.', '2023-01-26 10:30:00', NULL),
(1, 6, 'I disagree with some points about AI ethics.', '2023-01-26 14:45:00', 1),
(1, 7, 'Can you recommend more resources on this topic?', '2023-01-27 09:15:00', NULL),
(2, 8, 'Blockchain will revolutionize finance!', '2023-02-21 11:20:00', NULL),
(5, 5, 'These tips helped me improve my diet.', '2023-05-20 16:30:00', NULL),
(5, 6, 'What about protein intake for athletes?', '2023-05-21 10:10:00', 5),
(8, 7, 'Remote work has changed my productivity completely.', '2023-08-16 13:25:00', NULL);

-- Insert article views
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

-- Insert article votes
INSERT INTO article_votes (user_id, article_id, vote_type, voted_at) VALUES
(5, 1, 'upvote', '2023-01-26 10:35:00'),
(6, 1, 'downvote', '2023-01-26 14:50:00'),
(7, 1, 'upvote', '2023-01-27 09:20:00'),
(8, 2, 'upvote', '2023-02-21 11:25:00'),
(5, 5, 'upvote', '2023-05-20 16:35:00'),
(7, 8, 'upvote', '2023-08-16 13:30:00');