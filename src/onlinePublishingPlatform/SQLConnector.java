package onlinePublishingPlatform;

// import java.nio.file.Files;
// import java.nio.file.Path;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

public class SQLConnector {
    private Connection conn;
    private String url, user, password;
    private boolean isLoggedIn;
    
    public SQLConnector(Scanner sc) {
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
                url = "jdbc:mysql://localhost:3306/AdamsOnlinePublishingPlatform";
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
            String sql = "CREATE DATABASE IF NOT EXISTS AdamsOnlinePublishingPlatform";
            stmt.executeUpdate(sql);

            System.out.println("Database initialized successfully!");
            conn.close();
        } catch (Exception e) {
            System.err.println("Error initializing Database");
            e.printStackTrace();
        }
        
        try {
            url = "jdbc:mysql://localhost:3306/AdamsOnlinePublishingPlatform";
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
                stmt.executeUpdate("DROP DATABASE AdamsOnlinePublishingPlatform");
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
            } catch (Exception e) {
                System.err.println("Error inserting dummy data");
                e.printStackTrace();
            }

        }

    private boolean databaseNotExists() {
        String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = ?";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, "AdamsOnlinePublishingPlatform");
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

                System.out.printf("| %-10d | %-20s | %,16d | $%,12.2f | $%,11.2f |%n",
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

                System.out.printf("| %-7d | %-20s | %,16d | %,19d | %,19d | %,17d | %14.2f%% |%n",
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
}
