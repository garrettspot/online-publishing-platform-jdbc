package onlinePublishingPlatform;

// import java.sql.SQLException;
import java.util.Scanner;

public class OnlinePublishingPlatform implements AutoCloseable {
    Scanner sc;
    DBConnector database;
    public OnlinePublishingPlatform(Scanner sc) {
        this.sc = sc;
    }

    public void startProgram() {
        database = new DBConnector(sc);
        
        CLIMenu menu = new CLIMenu(database, sc);
        menu.startMenu();
    }

    @Override
    public void close() {
        sc.close();
        database.close();
    }
}
