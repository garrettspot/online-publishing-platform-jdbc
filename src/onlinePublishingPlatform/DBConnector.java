package onlinePublishingPlatform;

// import java.nio.file.Files;
// import java.nio.file.Path;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class DBConnector implements AutoCloseable {
    private Connection conn;
    private String url, user, password;
    private boolean isLoggedIn;
    
    public DBConnector(Scanner sc) {
        isLoggedIn = false;
        while (!isLoggedIn) {
            System.out.print("Enter your MySQL password: ");
            password = sc.next();
            try {
                user = "root";
                url = "jdbc:mysql://localhost:3306/";
                conn = DriverManager.getConnection(url, user, password);
                isLoggedIn = true;
            } catch (SQLException e) {
                System.out.println("Wrong password! Try again!");
            }
        }
        try {
            if (databaseNotExists()) {
                createDatabase(password);
            } else {
                conn.close();
                url = "jdbc:mysql://localhost:3306/AdamsOPP";
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            System.err.println("Error connecting to MySQL");
            e.printStackTrace();
        }
    }
    
    private void createDatabase(String password) {
        try {
            Statement stmt = conn.createStatement();
            String sql = "CREATE DATABASE IF NOT EXISTS AdamsOPP";
            stmt.executeUpdate(sql);

            System.out.println("Database initialized successfully!");
            conn.close();
        } catch (Exception e) {
            System.err.println("Error initializing Database");
            e.printStackTrace();
        }
        
        try {
            url = "jdbc:mysql://localhost:3306/AdamsOPP";
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("Error connecting to database");
            e.printStackTrace();
        }

        try {
            Statement stmt = conn.createStatement();
            // System.out.println("Working dir: " + System.getProperty("user.dir"));
            // String sql = Files.readString(Path.of("Schema.sql"));
            String sql = SQLQueries.Schema;

            Arrays.stream(sql.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(query -> {
                    try {
                        stmt.executeUpdate(query);
                    } catch (Exception e) {
                        System.out.println("Failed query: " + query);
                        e.printStackTrace();
                    }
                });
        } catch (Exception e) {
            System.err.println("Error creating tables");
            try {
                Statement stmt = conn.createStatement();
                stmt.executeUpdate("DROP DATABASE AdamsOPP");
            } catch (Exception f) {
                System.err.println("Error dropping dp" + f.getMessage());
            }
            e.printStackTrace();
        }

        try {
            Statement stmt = conn.createStatement();
            // System.out.println("Working dir: " + System.getProperty("user.dir"));
            // String sql = Files.readString(Path.of("DummyData.sql"));
            String sql = SQLQueries.DummyData;

            Arrays.stream(sql.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .forEach(query -> {
                    try {
                        stmt.executeUpdate(query);
                    } catch (Exception e) {
                        System.out.println("Failed query: " + query);
                        e.printStackTrace();
                    }
                });
            
            System.out.println("Dummy Data inserted successfully!");
        } catch (Exception e) {
            System.err.println("Error inserting dummy data");
            e.printStackTrace();
        }

        }

    private boolean databaseNotExists() {
        String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "AdamsOPP");
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // false(true if DB exists)
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void printArticlesPerAuthorPerMonth() {
        String query = SQLQueries.articlesPerAuthorPerMonthQuery;

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("+------------+----------------------+---------+--------------------+");
            System.out.println("| Author ID  | Author Name          | Month   | Articles Published |");
            System.out.println("+------------+----------------------+---------+--------------------+");

            while (rs.next()) {
                int authorId = rs.getInt("author_id");
                String authorName = rs.getString("author_name");
                String month = rs.getString("month");
                int count = rs.getInt("articles_published");

                System.out.printf("| %-10d | %-20s | %-7s | %18d |%n", 
                    authorId,
                    truncateString(authorName, 20),
                    month,
                    count);
            }
            System.out.println("+------------+----------------------+---------+--------------------+");
        } catch (SQLException e) {
            System.err.println("Error retrieving articles published per author per month: " + e.getMessage());
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "";
        }
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }

    public void printPendingArticlesInReview() {
        String query = SQLQueries.pendingArticlesInReviewQuery;

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nPENDING ARTICLES IN REVIEW QUEUE");
            System.out.println("+------------+--------------------------------+---------------------+----------------+--------------+----------------+");
            System.out.println("| Article ID | Title                          | Created On          | Author         | Days Waiting | Review Status  |");
            System.out.println("+------------+--------------------------------+---------------------+----------------+--------------+----------------+");

            while (rs.next()) {
                int articleId = rs.getInt("article_id");
                String title = truncateString(rs.getString("title"), 30);
                String createdDate = rs.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                String author = truncateString(rs.getString("author_name"), 14);
                int daysWaiting = rs.getInt("days_in_review");
                String reviewStatus = rs.getString("review_status") != null ? 
                                    rs.getString("review_status") : "Not Started";

                System.out.printf("| %-10d | %-30s | %-19s | %-14s | %-12d | %-14s |%n",
                    articleId, title, createdDate, author, daysWaiting, reviewStatus);
            }

            System.out.println("+------------+--------------------------------+---------------------+----------------+--------------+----------------+");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving pending articles: " + e.getMessage());
        }
    }

    public void printRevenuePerCategory() {
        String query = SQLQueries.revenuePerCategoryQuery;

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nREVENUE BY CONTENT CATEGORY");
            System.out.println("+------------+----------------------+------------------+---------------+---------------+");
            System.out.println("| Category ID| Category Name        | Total Purchases  | Total Revenue | Avg Purchase  |");
            System.out.println("+------------+----------------------+------------------+---------------+---------------+");

            while (rs.next()) {
                int categoryId = rs.getInt("category_id");
                String categoryName = truncateString(rs.getString("category_name"), 20);
                int totalPurchases = rs.getInt("total_purchases");
                double totalRevenue = rs.getDouble("total_revenue");
                double avgPurchase = rs.getDouble("avg_purchase_amount");

                System.out.printf("| %-10d | %-20s | %,16d | $%,12.2f | $%,12.2f |%n",
                    categoryId,
                    categoryName,
                    totalPurchases,
                    totalRevenue,
                    avgPurchase);
            }

            System.out.println("+------------+----------------------+------------------+---------------+---------------+");
            
        } catch (SQLException e) {
            System.err.println("Error retrieving revenue data: " + e.getMessage());
        }
    }

    public void printSubscriberChurnRate() {
        String query = SQLQueries.subscriberChurnRateQuery;

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nSUBSCRIBER CHURN RATE BY PLAN");
            System.out.println("+---------+----------------------+------------------+---------------------+---------------------+-------------------+----------------+");
            System.out.println("| Plan ID | Plan Name            | Active Subs      | Canceled Subs       | Expired Subs        | Total Subs        | Churn Rate %   |");
            System.out.println("+---------+----------------------+------------------+---------------------+---------------------+-------------------+----------------+");

            while (rs.next()) {
                int planId = rs.getInt("plan_id");
                String planName = truncateString(rs.getString("plan_name"), 20);
                int active = rs.getInt("active_subscribers");
                int canceled = rs.getInt("canceled_subscribers");
                int expired = rs.getInt("expired_subscribers");
                int total = rs.getInt("total_subscribers");
                double churnRate = rs.getDouble("churn_rate_percentage");

                System.out.printf("| %-7d | %-20s | %,16d | %,19d | %,19d | %,17d | %13.2f%% |%n",
                    planId,
                    planName,
                    active,
                    canceled,
                    expired,
                    total,
                    churnRate);
            }

            System.out.println("+---------+----------------------+------------------+---------------------+---------------------+-------------------+----------------+");
           
        } catch (SQLException e) {
            System.err.println("Error retrieving churn rate data: " + e.getMessage());
        }
    }


    public void printTopViewedArticlesByCategory() {
        String query = SQLQueries.topViewedArticlesByCategoryQuery;

        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nTOP VIEWED ARTICLES BY CATEGORY");
            System.out.println("+------------+----------------------+------------+--------------------------------+------------------+----------------+");
            System.out.println("| Category ID| Category Name        | Article ID | Article Title                  | View Count       | Author         |");
            System.out.println("+------------+----------------------+------------+--------------------------------+------------------+----------------+");

            String currentCategory = "";
            boolean isFirstInCategory = true;
            
            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                int categoryId = rs.getInt("category_id");
                
                if (!categoryName.equals(currentCategory)) {
                    currentCategory = categoryName;
                    isFirstInCategory = true;
                }

                // Print row - include category info only for first article in category
                System.out.printf("| %-10s | %-20s | %-10d | %-30s | %,16d | %-14s |%n",
                    isFirstInCategory ? categoryId : "",
                    isFirstInCategory ? categoryName : "",
                    rs.getInt("article_id"),
                    truncateString(rs.getString("title"), 30),
                    rs.getLong("view_count"),
                    truncateString(rs.getString("author_name"), 14));
                
                isFirstInCategory = false;
            }

            // Print table footer
            System.out.println("+------------+----------------------+------------+--------------------------------+------------------+----------------+");
        } catch (SQLException e) {
            System.err.println("Error retrieving top viewed articles: " + e.getMessage());
        }
    }

    public boolean createUser(String username, String first_name, String last_name, String password, String email, int userRole) {
        String query1 = "INSERT INTO users (username, password, email, first_name, last_name, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
        String query2 = "SELECT user_id FROM users WHERE username = ?";
        String query3 = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query1);
             PreparedStatement stmt2 = conn.prepareStatement(query2);
             PreparedStatement stmt3 = conn.prepareStatement(query3);) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.setString(4, first_name);
            stmt.setString(5, last_name);

            if (stmt.executeUpdate() == 0) {
                return false;
            }

            stmt2.setString(1, username);
            ResultSet res = stmt2.executeQuery();

            if (!res.next()) return false;

            int userId = res.getInt("user_id");
            stmt3.setInt(1, userId);
            stmt3.setInt(2, userRole);
            stmt3.executeUpdate();

            return true;
        } catch (SQLException e) {
            System.err.println("User creation failed: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching ");
        }
        return false;
    }

    public void listArticlesByStatus(String status) {
        String query = "SELECT a.article_id, a.title, u.username AS author, a.created_at " +
                    "FROM articles a JOIN authors au ON a.author_id = au.author_id " +
                    "JOIN users u ON au.user_id = u.user_id WHERE a.status = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            System.out.println("+------------+------------------------------------------+----------------------+----------------------+");
            System.out.printf("| %-10s | %-40s | %-20s | %-20s |\n", "ID", "Title", "Author", "Created At");
            System.out.println("+------------+------------------------------------------+----------------------+----------------------+");
            while (rs.next()) {
                System.out.printf("| %-10d | %-40s | %-20s | %-20s |\n",
                    rs.getInt("article_id"),
                    truncateString(rs.getString("title"), 40),
                    rs.getString("author"),
                    rs.getTimestamp("created_at").toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
            }
            System.out.println("+------------+------------------------------------------+----------------------+----------------------+");
        } catch (Exception e) {
            System.err.println("Error in database.listArticlesByStatus: " + e.getMessage());
        }
    }
            
    public void submitForReview(int articleId) {        
        String query = "UPDATE articles SET status = 'in_review' WHERE article_id = ? AND status = 'draft'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, articleId);
            int updated = stmt.executeUpdate();

            System.out.println(updated > 0 ? "Article submitted for review" : "No draft article found with that ID");
        } catch (Exception e) {
            System.err.println("Error in database.submitForReview: " + e.getMessage());
        }
    }

    public void approvePublication(int articleId) {
        String query = "UPDATE articles SET status = 'published', published_at = NOW() " +
                    "WHERE article_id = ? AND status = 'in_review'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, articleId);
            int updated = stmt.executeUpdate();
            System.out.println(updated > 0 ? "Article published successfully" : "No review-ready article found with that ID");
        } catch (Exception e) {
            System.err.println("Error in database.approvePublication: " + e.getMessage());
        }
    }

    public void returnForRevisions(int articleId, String feedback) {
        String reviewQuery = "INSERT INTO article_reviews (article_id, reviewer_id, status, feedback_notes, created_at) " +
                            "VALUES (?, ?, 'needs_changes', ?, NOW())";
        
        String statusQuery = "UPDATE articles SET status = 'draft' WHERE article_id = ?";
        
        try {
            conn.setAutoCommit(false);
            
            try (PreparedStatement reviewStmt = conn.prepareStatement(reviewQuery);
                PreparedStatement statusStmt = conn.prepareStatement(statusQuery)) {
                
                reviewStmt.setInt(1, articleId);
                reviewStmt.setInt(2, 0);
                reviewStmt.setString(3, feedback);
                reviewStmt.executeUpdate();
                
                statusStmt.setInt(1, articleId);
                int updated = statusStmt.executeUpdate();
                
                if (updated > 0) {
                    conn.commit();
                    System.out.println("Article returned for revisions with feedback");
                } else {
                    conn.rollback();
                    System.out.println("No review-ready article found with that ID");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error in returnForRevisions: " + e.getMessage());
        }
    }

    public void displayPlans() {
        String query = "SELECT * FROM subscription_plans";
        try (Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);) {
            System.out.println("\nSUBSCRIPTION PLANS");
            System.out.println("+---------+----------------+----------------+---------------+---------------------------------------+-----------+");
            System.out.printf("| %-7s | %-14s | %-14s | %-13s | %-37s | %-9s |\n", 
                "Plan ID", "Plan Name", "Price Monthly", "Price Annually", "Description", "Is Active");
            System.out.println("+---------+----------------+----------------+---------------+---------------------------------------+-----------+");
            
            while (rs.next()) {
                System.out.printf("| %-7d | %-14s | $%,13.2f | $%,14.2f | %-37s | %-9s |\n",
                    rs.getInt("plan_id"),
                    truncateString(rs.getString("name"), 14),
                    rs.getDouble("price_monthly"),
                    rs.getDouble("price_annually"),
                    truncateString(rs.getString("description"), 37),
                    rs.getBoolean("is_active") ? "ACTIVE" : "INACTIVE");
            }
            
            System.out.println("+---------+----------------+----------------+---------------+---------------------------------------+-----------+");
            
        } catch (SQLException e) {
            System.err.println("Error displaying plans: " + e.getMessage());
        }
    }

    public void changePlanPrice(int plan_id, double price, String type) {
        if (price < 0) {
            System.out.println("Price cannot be negative");
            return;
        }

        String query = "UPDATE subscription_plans SET price_" + type + " = ? WHERE plan_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, price);
            stmt.setInt(2, plan_id);
            
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                System.out.println("No plan found with ID: " + plan_id);
            } else {
                System.out.printf("Successfully updated plan %d to new price: $%.2f%n", plan_id, price);
            }
        } catch (Exception e) {
            System.out.println("Error in database.changePlanPrice: " + e.getMessage());
        }
    }

    public boolean togglePlanStatus(int plan_id) {
        String toggleQuery = "UPDATE subscription_plans " +
                            "SET is_active = NOT is_active " +
                            "WHERE plan_id = ?";
        
        String statusQuery = "SELECT name, is_active FROM subscription_plans WHERE plan_id = ?";
        
        try (PreparedStatement toggleStmt = conn.prepareStatement(toggleQuery);
            PreparedStatement statusStmt = conn.prepareStatement(statusQuery)) {
            
            toggleStmt.setInt(1, plan_id);
            int rowsUpdated = toggleStmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                System.out.println("No plan found with ID: " + plan_id);
                return false;
            }
            
            statusStmt.setInt(1, plan_id);
            try (ResultSet rs = statusStmt.executeQuery()) {
                if (rs.next()) {
                    String planName = rs.getString("name");
                    boolean isActive = rs.getBoolean("is_active");
                    
                    System.out.printf("Plan '%s' (ID: %d) has been %s%n",
                                    planName, plan_id,
                                    isActive ? "ACTIVATED" : "DEACTIVATED");
                }
            }
        } catch (Exception e) {
            System.out.println("Error at databse.togglePlanStats: " + e.getMessage());
        }
        return true;
    }

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection");
        }
    }
}
