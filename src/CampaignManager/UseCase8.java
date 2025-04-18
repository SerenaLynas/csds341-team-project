package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;

public class UseCase8 {
    public static void run(Connection conn, Scanner scanner) {
        System.out.println("would you like to insert a new person-issue tuple or find people who care about an issue?");

        String cmd = "";
        while (!cmd.equals("insert") && !cmd.equals("find")) {
            System.out.println(
                    "enter `insert` to add a new person-issue tuple or `find` to identify people who care about an issue:");
            cmd = scanner.next();
        }

        if (cmd.equals("insert")) {
            run_insert(conn, scanner);
        } else {
            run_find(conn, scanner);
        }
    }

    private static void run_insert(Connection conn, Scanner scanner) {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the person_id of the person you want to edit (y/n/Y/N)?");
            cmd = scanner.next().toLowerCase();
        }

        if (cmd.equals("n")) {
        }
    }

    private static void run_find(Connection conn, Scanner scanner) {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the issue id you wish to find (y/n/Y/N)?");
            cmd = scanner.next().toLowerCase();
        }

        if (cmd.equals("n")) {
            Utility.display_issues(conn);
        }

        System.out.println("enter the issue id you are interested in (q to quit):");
        var id_or_quit = scanner.next();

        if (id_or_quit.toLowerCase().equals("q")) {
            scanner.close();
            return;
        }

        var issue_id = Integer.parseInt(id_or_quit);

        String where = "";
        while (!where.equals("people") && !where.equals("election")) {
            System.out.println("to find all people who are known to care about this issue, enter `people`.");
            System.out.println(
                    "to find all people who have voted in an election where this issue was at stake, enter `election`.");
            where = scanner.next();
        }

        if (where.equals("people")) {
            find_known_people(conn, issue_id);
        } else {
            find_by_vote(conn, issue_id);
        }
    }

    private static void find_known_people(Connection conn, int issue_id) {
        try (var callable = conn.prepareCall("{call find_people_for_issue(?)}")) {
            callable.setInt(1, issue_id);
            var result = callable.executeQuery();

            while (result.next()) {
                System.out.println(result.getString("first") + " " + result.getString("last") + ","
                        + result.getString("phone") + "," + result.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void find_by_vote(Connection conn, int issue_id) {
        try (var callable = conn.prepareCall("{call find_elections_for_issue(?)}");
                var voted_in = conn.prepareCall("{call voted_in(?)}")) {
            callable.setInt(1, issue_id);
            var result = callable.executeQuery();
            var lines = new HashSet<String>();
            while (result.next()) {
                var election_id = result.getInt("election_id");
                voted_in.setInt(1, election_id);
                var people = voted_in.executeQuery();

                while (people.next()) {
                    lines.add(people.getString("first") + " " + people.getString("last") + ","
                            + people.getString("phone") + "," + people.getString("email"));
                }
            }
            for (var line : lines) {
                System.out.println(line);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
