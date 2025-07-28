package onlinePublishingPlatform;

import java.util.Scanner;

/* I just realised it's redundant because Scanner is only used in CLIs.
Might consider refactoring and implementing this class if GUI becomes a requirement */

public abstract class Menu {
    protected Scanner sc;
    protected DBConnector database;

    Menu(DBConnector database, Scanner sc) {
        this.sc = sc;
        this.database = database;
    }

    public abstract void startMenu();
}
