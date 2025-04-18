package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class UseCase12 {
    public static void run(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Use Case 12: Determine the effectiveness of past events.");
        
        String event_type = null;
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("Would you like to filter by event type (y/n)?");
            cmd = scanner.nextLine().trim().toLowerCase();
        }
        if (cmd.equals("y")) {
            display_event_types(conn);
            System.out.println("Enter the event type to filter by:");
            event_type = scanner.nextLine();
        }

        Integer campaign_id = null;
        cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("Would you like to filter by campaign (y/n)?");
            cmd = scanner.nextLine().trim().toLowerCase();
        }
        if (cmd.equals("y")) {
            display_campaigns(conn);
            System.out.println("Enter the campaign_id to filter by:");
            campaign_id = scanner.nextInt();
            scanner.nextLine();
        }

        var callable = conn.prepareCall("{call analyze_event_effectiveness(?, ?)}");
        if (event_type != null && !event_type.isBlank()) {
            callable.setString(1, event_type);
        } else {
            callable.setNull(1, java.sql.Types.VARCHAR);
        }

        if (campaign_id != null) {
            callable.setInt(2, campaign_id);
        } else {
            callable.setNull(2, java.sql.Types.INTEGER);
        }

        var result = callable.executeQuery();

        System.out.println("\nTop 5 Events Based on Attendance and Attendee Issues:");
        System.out.println("Event ID | Event Name\t\t| Attendance | Top Issue");
        while (result.next()) {
            int event_id = result.getInt("event_id");
            String name = result.getString("name");
            int attendance = result.getInt("total_attendance");
            String top_issue = result.getString("top_issue");

            System.out.printf("%-9d| %-20s| %-11d| %s\n", event_id, name, attendance, top_issue);
        }
    }

    private static void display_event_types(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement(
            "SELECT DISTINCT type FROM event WHERE type IS NOT NULL"
        );
        var rs = stmt.executeQuery();
    
        System.out.println("\nAvailable Event Types:");
        while (rs.next()) {
            System.out.println("- " + rs.getString("type"));
        }
        System.out.println();
    }

    private static void display_campaigns(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement(
            "SELECT c.campaign_id, p.first, p.last, e.date " +
            "FROM campaign c " +
            "JOIN person p ON c.candidate_id = p.person_id " +
            "JOIN election e ON c.election_id = e.election_id"
        );
        var rs = stmt.executeQuery();
        System.out.println("\nAvailable Campaigns:");
        while (rs.next()) {
            System.out.printf("campaign_id: %d | candidate: %s %s | election date: %s\n",
                rs.getInt("campaign_id"),
                rs.getString("first"),
                rs.getString("last"),
                rs.getDate("date"));
        }
        System.out.println();
    }
}