package onlinePublishingPlatform;

import java.util.Scanner;

public class CLIMenu {
    Scanner sc;
    DBConnector database;

    CLIMenu(DBConnector database, Scanner sc) {
        this.sc = sc;
        this.database = database;
    }

    public void startMenu() {
        while (true) {
            System.out.println("""

            ---------------------------------------------------
            Adam's Online Publishing Platform Management System
            ---------------------------------------------------
            Admin Panel
            1. Frequently accessed views
            2. Register a new User
            3. Manage Article Workflow
            4. Change Plan Prices
            5. Exit
            """);
            System.out.print("Select option: ");
            String choice = sc.next().trim();
            
            switch (choice) {
                case "1":
                    viewData(); break;
                case "2":
                    register(); break;
                case "3":
                    edit(); break;
                case "4":
                    changePrices(); break;
                case "5":
                    System.out.println("Closing Program"); return;
                default:
                    System.out.println("!!! --- Please enter a valid option! --- !!!"); break;
            }
        }
    }

    public void viewData() {
        while (true) {
            System.out.println("Frequently used views");
            System.out.println("""
            1. Articles published per Author per month
            2. Subscriber churn rate per Plan
            3. Top-viewed Articles by Category
            4. Pending articles in Review
            5. Revenue per content Category
            0. Back
            """);
            System.out.print("Select option: ");
            
            String choice = sc.next().trim();
            sc.nextLine();
            switch (choice) {
                case "1":
                    database.printArticlesPerAuthorPerMonth(); break;
                case "2":
                    database.printSubscriberChurnRate(); break;
                case "3":
                    database.printTopViewedArticlesByCategory(); break;
                case "4":
                    database.printPendingArticlesInReview(); break;
                case "5":
                    database.printRevenuePerCategory(); break;
                case "0":
                    return;
                default:
                    System.out.println("Please enter a valid option!"); break;
            }
            System.out.println("Press enter to go back");
            sc.nextLine();
        }
    }

    public void register() {
        boolean usernameInvalid = true, emailInvalid = true, passwordInvalid = true;
        String username = "", email = "", password = "";

        while (usernameInvalid) {
            System.out.print("Enter username: ");
            username = sc.next().trim();
            if (username.isBlank()) {
                System.out.println("Please enter a username!");
                continue;
            }
            
            if (database.usernameExists(username)) {
                System.out.println("username already exists! Please try a unique username");
                while (true) {
                    System.out.print("Continue registration? (y/n) ");
                    String choice = sc.next().toLowerCase().trim();
                    
                    if (choice.equals("y")) {
                        break;
                    } else if (choice.equals("n")) {
                        return;
                    }
                }
            } else {
                usernameInvalid = false;
            }
        }
        while (emailInvalid) {
            System.out.print("Enter email: ");
            email = sc.next().trim();
            if (email.isBlank()) {
                System.out.println("Please enter an email!");
                continue;
            }
            
            if (database.usernameExists(email)) {
                System.out.println("email already in use! Please login to your existing account or use a new email!");
                while (true) {
                    System.out.print("Continue registration? (y/n) ");
                    String choice = sc.next().toLowerCase().trim();
                    if (choice.equals("y")) {
                        break;
                    } else if (choice.equals("n")) {
                        return;
                    }
                    
                }              
            } else {
                emailInvalid = false;
            }
        }
        while (passwordInvalid) {
            System.out.print("Enter password: ");
            String pass1 = sc.next().trim();
            System.out.print("Confirm password: ");
            String pass2 = sc.next().trim();
            
            if (pass1.isBlank()) {
                System.out.println("Please enter a password!");
                continue;
            }
            if (pass1.equals(pass2)) {
                passwordInvalid = false;
            } else {
                System.out.println("Passwords do not match! Try again.");
                while (true) {
                    System.out.print("Continue registration? (y/n) ");
                    String choice = sc.next().toLowerCase().trim();
                    if (choice.equals("y")) {
                        break;
                    } else if (choice.equals("n")) {
                        return;
                    }
                }
            }
        }
        int userRole;
        while (true) {
            System.out.println("""
            Roles:
            1. Admin
            2. Editor
            3. Author   
            """);
            System.out.print("Select a role for the user: ");
            String input = sc.next();
            
            int choice = Integer.parseInt(input);
            if (choice > 0 && choice < 4) {
                userRole = choice;
                break;
            }
        }
        String firstName, lastName;
        while (true) {
            System.out.print("Enter first name: ");
            firstName = sc.next();
            if (firstName.isBlank()) {
                System.out.println("Please enter a first name!");
            } else {
                break;
            }
        }
        while (true) {
            System.out.print("Enter last name: ");
            lastName = sc.next();
            if (lastName.isBlank()) {
                System.out.println("Please enter a last name!");
            } else {
                break;
            }
        }
        if (database.createUser(username, firstName, lastName, password, email, userRole)) {
            System.out.println("User " + username + " successfully created!");
        }
    }

    public void edit() {
        while (true) {
            System.out.println("""
            ARTICLE WORKFLOW MANAGEMENT
            1. List draft articles
            2. List articles in review
            3. Submit article for review
            4. Approve article for publication
            5. Return article for revisions
            0. Back
            """);
            System.out.print("Select option: ");
            
            String choice = sc.next().trim();
            
            switch (choice) {
                case "1":
                    System.out.println("Articles in Draft");
                    database.listArticlesByStatus("draft");
                    break;
                case "2":
                    System.out.println("Articles in Review");
                    database.listArticlesByStatus("in_review");
                    break;
                case "3":
                    System.out.print("Enter articleID to submit to review: ");
                    while (true) {
                        String article_id = sc.next().trim();
                        
                        if (isDigit(article_id)) {
                            database.submitForReview(Integer.parseInt(article_id));
                            break;
                        } else {
                            System.out.println("Please enter a valid article ID!");
                        }
                        break;
                    }
                    break;
                case "4":
                    System.out.print("Enter articleID to publish: ");
                    while (true) {
                        String article_id = sc.next().trim();
                        
                        if (isDigit(article_id)) {
                            database.approvePublication(Integer.parseInt(article_id));
                            break;
                        } else {
                            System.out.println("Please enter a valid article ID!");
                        }
                        break;
                    }
                    break;
                case "5":
                    System.out.print("Enter article ID to return for revisions: ");
                    String articleId = sc.next().trim();
                    
                    System.out.print("Enter feedback for author: ");
                    String feedback = sc.next().trim();
                    database.returnForRevisions(Integer.parseInt(articleId), feedback);
                    
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option"); break;
            }
        }
    }

    private boolean isDigit(String st) {
        int n = st.length();
        for (int i = 0; i < n; i++) {
            if (!Character.isDigit(st.charAt(i))) return false;
        }
        return true;
    }
    
    public void changePrices() {
        while (true) {
            System.out.println("""
            PRICE MANAGEMENT
            1. View All Plans
            2. Change a plan's price
            3. Activate/Deactivate a plan
            0. Back
            """);
            System.out.print("Select option: ");
            String choice = sc.next();
            sc.nextLine();
            if (Character.isDigit(choice.charAt(0))) {
                switch (choice) {
                    case "1":
                        database.displayPlans();
                        System.out.println("Press enter to go back");
                        sc.nextLine();
                        break;
                    case "2":
                        boolean priceInvalid = true, typeInvalid = true;
                        while (priceInvalid) {
                            System.out.println("Enter plan_id to change its price: ");
                            String planId = sc.next();
                            if (!isDigit(planId)) {
                                System.out.println("Please enter a number!");
                                while (true) {
                                    System.out.println("Continue changing price? (y/n): ");
                                    String input = sc.next().toLowerCase().trim();
                                    if (input.equals("y")) {
                                        break;
                                    } else if (input.equals("n")) {
                                        return;
                                    }
                                }
                            } else {
                                while (typeInvalid) {
                                    System.out.println("Monthly or Annually? (m or a): ");
                                    String type = sc.next();
                                    if (type.equals("m")) {
                                        System.out.print("Enter new price: ");
                                        double price = sc.nextDouble();
                                        database.changePlanPrice(Integer.parseInt(planId), price, "monthly");
                                        typeInvalid = false;
                                        priceInvalid = false;
                                    } else if (type.equals("a")) {
                                        System.out.print("Enter new price: ");
                                        double price = sc.nextDouble();
                                        database.changePlanPrice(Integer.parseInt(planId), price, "annually");
                                        typeInvalid = false;
                                        priceInvalid = false;
                                    } else {
                                        System.out.println("Please enter a valid option!");
                                        while (true) {
                                            System.out.print("Continue changing price? (y/n): ");
                                            String input = sc.next().toLowerCase().trim();
                                            if (input.equals("y")) {
                                                break;
                                            } else if (input.equals("n")) {
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case "3":
                        while (true) {
                            System.out.print("Enter plan_id to toggle: ");
                            String planId = sc.next();
                            if (isDigit(planId)) {
                                database.togglePlanStatus(Integer.parseInt(planId));
                                break;
                            } else {
                                System.out.println("Enter a number!");
                            }
                        }
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Please enter a valid number!");
                }
            } else {
                System.out.println("Please enter a number!");
            }
        }
    }
}
