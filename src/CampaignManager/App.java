package CampaignManager;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws SQLException {
        String connectionUrl = "jdbc:sqlserver://cxp-sql-03\\sjl132;"
                + "database=university;"
                + "user=dbuser;"
                + "password=csds341143sdsc;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=15;";

        var conn = DriverManager.getConnection(connectionUrl);
        conn.setAutoCommit(false);

        var scanner = new Scanner(System.in);

        while (true) {
            System.out.println("enter ucN to select a specific use case, or q/Q to quit:");
            String cmd = scanner.nextLine();

            if (cmd.toLowerCase().equals("q")) {
                break;
            }

            try {
                if (cmd.equals("uc7")) {
                    UseCase7.run(conn, scanner);
                } else if (cmd.equals("uc8")) {
                    UseCase8.run(conn, scanner);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        }

        scanner.close();
    }
}
