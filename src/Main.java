import java.util.Scanner;
import onlinePublishingPlatform.*;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        OnlinePublishingPlatform opp = new OnlinePublishingPlatform(sc);
        opp.startProgram();
        
        opp.close();
    }
}
