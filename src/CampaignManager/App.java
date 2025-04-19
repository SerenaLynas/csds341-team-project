package CampaignManager;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws SQLException {
        String connectionUrl = "jdbc:sqlserver://cxp-sql-03\\sad123;"
                + "database=CampaignManager;"
                + "user=sa;"
                + "password=ioIbCq5vew483a;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=15;";

        var conn = DriverManager.getConnection(connectionUrl);
        conn.setAutoCommit(false);

        var scanner = new Scanner(System.in);
        var cli = new Cli(scanner);
        while (true) {
            var choice = cli.choices(
                "What would you like to do?",
                "Find local events [use case 1]",
                "Make a donation [use case 2]",
                "[use case 7]",
                "[use case 8]",
                "[use case ]",
                "[use case ]",
                "Quit"
            );

            try {
                switch (choice) {
                    case 0: UseCase1.run(conn, cli); break;
                    case 2: UseCase7.run(conn, scanner); break;
                    case 3: UseCase8.run(conn, scanner); break;
                    case 6: scanner.close(); return;
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }
        }
    }
}