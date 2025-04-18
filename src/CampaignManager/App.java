package CampaignManager;

import java.sql.DriverManager;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        String connectionUrl = "jdbc:sqlserver://cxp-sql-03\\sjl132;"
                + "database=university;"
                + "user=dbuser;"
                + "password=csds341143sdsc;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=15;";

        var conn = DriverManager.getConnection(connectionUrl);
        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("enter ucN to select a specific use case, or q/Q to quit:");
            String cmd = scanner.next();

            if (cmd.toLowerCase().equals("q")) {
                break;
            }

            if (cmd.equals("uc7")) {
                UseCase7.run(conn, scanner);
            } else if (cmd.equals("uc8")) {
                UseCase8.run(conn, scanner);
            }
        }

        scanner.close();
    }
}
