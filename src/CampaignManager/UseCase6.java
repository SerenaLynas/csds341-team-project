package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class UseCase6 {
    public static void run(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Use Case 6: Identify and add a new candidate to an upcoming election.");


        display_districts(conn);
        System.out.println("Enter the district you would like to find a candidate for:");
        String district = scanner.nextLine();

        
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("Do you want to see a list of issues before selecting (y/n)?");
            cmd = scanner.nextLine().toLowerCase();
        }
        if (cmd.equals("y")) {
            Utility.display_issues(conn);
        }

        System.out.println("Enter the issue_id that you want your candidate to care about:");
        int issue_id = scanner.nextInt();
        scanner.nextLine();

        
        display_elections(conn);
        System.out.println("Enter the election_id to add a candidate for:");
        int election_id = scanner.nextInt();
        scanner.nextLine();

        
        display_campaign_managers(conn);
        System.out.println("Enter your person_id (as manager) from the above list:");
        int manager_id = scanner.nextInt();
        scanner.nextLine();

        
        var callable = conn.prepareCall("{call find_new_candidates(?, ?, ?)}");
        callable.setString(1, district);
        callable.setInt(2, issue_id);
        callable.setInt(3, election_id);
        var result = callable.executeQuery();

        int index = 1;
        int[] person_ids = new int[5];

        System.out.println("\nTop recommended candidates:");
        System.out.println("Index\tName\t\tEmail\t\tPhone\t\t#Elections\tMatching Issue");
        while (result.next() && index <= 5) {
            int person_id = result.getInt("person_id");
            String first = result.getString("first");
            String last = result.getString("last");
            String email = result.getString("email");
            String phone = result.getString("phone");
            int elections = result.getInt("elections_participated");
            int matches = result.getInt("matching_issue");

            System.out.printf("%d\t%s %s\t%s\t%s\t%d\t\t%d\n", index, first, last, email, phone, elections, matches);
            person_ids[index - 1] = person_id;
            index++;
        }

        if (index == 1) {
            System.out.println("no suitable candidates found for the district and issue.");
            return;
        }

        
        System.out.println("Enter the index (1-5) of the person you'd like to nominate as a candidate (or 0 to cancel):");
        int chosen_index = scanner.nextInt();
        scanner.nextLine();

        if (chosen_index < 1 || chosen_index > 5) {
            System.out.println("No candidate was added.");
            return;
        }

        int chosen_person_id = person_ids[chosen_index - 1];
        var insert_candidate = conn.prepareCall("{call add_candidate_to_campaign(?, ?, ?)}");
        insert_candidate.setInt(1, chosen_person_id);
        insert_candidate.setInt(2, election_id);
        insert_candidate.setInt(3, manager_id);

        try {
            insert_candidate.executeUpdate();
            System.out.println("Candidate successfully added to the campaign.");
        } catch (SQLException e) {
            System.out.println("Error while adding candidate: " + e.getMessage());
        }
    }

    private static void display_districts(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement("SELECT DISTINCT district FROM person WHERE district IS NOT NULL");
        var rs = stmt.executeQuery();
        System.out.println("\nAvailable Districts:");
        while (rs.next()) {
            System.out.println("- " + rs.getString("district"));
        }
        System.out.println();
    }

    private static void display_elections(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement(
            "SELECT election_id, date, registration_deadline " +
            "FROM election WHERE date >= CAST(GETDATE() AS DATE)"
        );
        var rs = stmt.executeQuery();
        System.out.println("\nUpcoming Elections:");
        while (rs.next()) {
            System.out.printf("election_id: %d | date: %s | registration deadline: %s\n",
                rs.getInt("election_id"),
                rs.getDate("date"),
                rs.getDate("registration_deadline"));
        }
        System.out.println();
    }

    private static void display_campaign_managers(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement(
            "SELECT DISTINCT p.person_id, p.first, p.last, p.email " +
            "FROM person p " +
            "INNER JOIN campaign c ON p.person_id = c.manager_id"
        );
        var rs = stmt.executeQuery();
        System.out.println("\nExisting Campaign Managers:");
        while (rs.next()) {
            System.out.printf("person_id: %d | name: %s %s | email: %s\n",
                rs.getInt("person_id"),
                rs.getString("first"),
                rs.getString("last"),
                rs.getString("email"));
        }
        System.out.println();
    }
}