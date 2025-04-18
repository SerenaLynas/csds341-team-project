package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class UseCase7 {
    public static void run(Connection conn, Scanner scanner) {
        System.out.println("would you like to insert a new donation or find information about existing donations?");

        String cmd = "";
        while (!cmd.equals("insert") && !cmd.equals("find")) {
            System.out.println(
                    "enter `insert` to add a new donation or `find` find information about existing donations:");
            cmd = scanner.nextLine();
        }

        if (cmd.equals("insert")) {

        } else {

        }
    }

    private static void insert_donation(Connection conn, Scanner scanner) throws SQLException {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the person_id of the person who made the donation (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int person_id;
        if (cmd.equals("n")) {
            person_id = Utility.fetch_or_insert_person(conn, scanner);
        } else {
            System.out.println("enter the person_id:");
            person_id = scanner.nextInt();
            scanner.nextLine();
        }
    }
}
