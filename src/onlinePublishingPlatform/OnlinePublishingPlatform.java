package onlinePublishingPlatform;

// import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class OnlinePublishingPlatform {
    Scanner sc;
    SQLConnector database;
    public OnlinePublishingPlatform(Scanner sc) {
        this.sc = sc;
    }

    public void startProgram() {
        database = new SQLConnector(sc);
        
        while (true) {
            System.out.println("""

            ---------------------------------------------------
            Adam's Online Publishing Platform Management System
            ---------------------------------------------------
            1. View data
            
            2. Exit
            """);
            try {
                System.out.print("Enter action: ");
                if (sc.hasNextInt()) {
                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            viewData(); break;
                        // case 2:
                        //     register(); break;
                        // case 3:
                        //     login(); break;
                        case 2:
                            System.out.println("Closing Program"); return;
                        default:
                            System.out.println("!!! --- Please enter a valid option! --- !!!"); break;
                    }
                } else {
                    System.out.println("!!! --- Please enter a valid option! --- !!!");
                    sc.nextLine();
                }
            } catch (Exception e) {
                System.out.println("""
                -!-!-!-!-!-!-!-!-!-!-!-
                Please enter a number!
                -!-!-!-!-!-!-!-!-!-!-!-
                """);
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
            4. Pending Articles in review queue
            5. Revenue per content Category
            0. Back
            """);
            try {
                System.out.print("Select view: ");
                if (sc.hasNextInt()) {
                    int choice = sc.nextInt();
                    switch (choice) {
                        case 1:
                            database.printArticlesPerAuthorPerMonth(); break;
                        case 2:
                            database.printSubscriberChurnRate(); break;
                        case 3:
                            database.printTopViewedArticlesByCategory(); break;
                        case 4:
                            database.printPendingArticlesInReview(); break;
                        case 5:
                            database.printRevenuePerCategory(); break;
                        case 0:
                            return;
                        default:
                            System.out.println("Please enter a valid option!"); break;
                    }
                    System.out.println("Input any key to go back");
                    sc.next();
                } else {
                    System.out.println("! -- Please Enter a number -- !");
                    sc.nextLine();
                }
            } catch (InputMismatchException e) {
                System.out.println("""
                -!-!-!-!-!-!-!-!-!-!-!
                Please enter a number!
                -!-!-!-!-!-!-!-!-!-!-!
                """);
            }
            
        }
    }

    public void register() {

    }

    public void login() {

    }
}
