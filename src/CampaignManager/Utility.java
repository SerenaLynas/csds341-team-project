package CampaignManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class Utility {
    public static int login(Connection conn, Cli cli) throws Exception {
        var voter_id = cli.askInt("What is your user ID?");
        var call = conn.prepareCall("{call get_person(?)}");
        call.setInt(1, voter_id);
        var result = call.executeQuery();

        if (!result.next()) {
            throw new Exception("Person with id " + voter_id + " does not exist.");
        }

        System.out.println(
            "You are logged in as "
            + result.getString("first") + " "
            + result.getString("last") + " residing in "
            + result.getString("district")
        );

        return voter_id;
    }

    public static void display_issues(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement("select * from issue");
        var result = stmt.executeQuery();
        while (result.next()) {
            int id = result.getInt("issue_id");
            String desc = result.getString("description");
            System.out.println("issue_id: " + id + ", description: " + desc);
        }
    }

    public static int fetch_or_insert_person(Connection conn, Scanner scanner) throws SQLException {
        var fetch = conn
                .prepareStatement("select person_id from person where first = ? and last = ? and dob = ?");
        var insert = conn.prepareCall("{call insert_person(?, ?, ?, ?, ?, ?, ?)}");

        System.out.println("first name:");
        var first = scanner.nextLine();

        System.out.println("last name:");
        var last = scanner.nextLine();

        System.out.println("date of birth (yyyy-[m]m-[d]d):");
        var dob = Date.valueOf(scanner.nextLine());

        fetch.setString(1, first);
        fetch.setString(2, last);
        fetch.setDate(3, dob);
        var fetch_result = fetch.executeQuery();

        while (fetch_result.next()) {
            var person_id = fetch_result.getInt("person_id");
            System.out.println("found someone with person_id = " + person_id);
            return fetch_result.getInt("person_id");
        }

        System.out.println("phone (empty for null):");
        var phone = scanner.nextLine();

        System.out.println("email (empty for null):");
        var email = scanner.nextLine();

        System.out.println("address (empty for null):");
        var address = scanner.nextLine();

        System.out.println("district (empty for null):");
        var district = scanner.nextLine();

        insert.setString(1, first);
        insert.setString(2, last);
        insert.setDate(3, dob);
        insert.setString(4, phone.length() > 0 ? phone : null);
        insert.setString(5, email.length() > 0 ? email : null);
        insert.setString(6, address.length() > 0 ? address : null);
        insert.setString(7, district.length() > 0 ? district : null);

        var insert_result = insert.executeQuery();
        insert_result.next();
        var person_id = insert_result.getInt("person_id");
        System.out.println("inserted someone with person_id = " + person_id);
        return person_id;
    }

    public static int fetch_or_insert_campaign(Connection conn, Scanner scanner) throws SQLException {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the person_id of the campaign's candidate (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int candidate_id;
        if (cmd.equals("n")) {
            candidate_id = fetch_or_insert_person(conn, scanner);
        } else {
            System.out.println("enter the person_id:");
            candidate_id = scanner.nextInt();
            scanner.nextLine();
        }

        cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the person_id of the campaign's manager (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int manager_id;
        if (cmd.equals("n")) {
            manager_id = fetch_or_insert_person(conn, scanner);
        } else {
            System.out.println("enter the person_id:");
            manager_id = scanner.nextInt();
            scanner.nextLine();
        }

        cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the election_id of the campaign (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int election_id;
        if (cmd.equals("n")) {
            election_id = fetch_or_insert_election(conn, scanner);
        } else {
            System.out.println("enter the election_id:");
            election_id = scanner.nextInt();
            scanner.nextLine();
        }

        var select = conn.prepareStatement(
                "select campaign_id from campaign where candidate_id = ? and manager_id = ? and election_id = ?");
        select.setInt(1, candidate_id);
        select.setInt(2, manager_id);
        select.setInt(3, election_id);
        var result = select.executeQuery();

        while (result.next()) {
            var campaign_id = result.getInt("campaign_id");
            System.out.println("found campaign with campaign_id = " + campaign_id);
        }

        var insert = conn.prepareCall("{call insert_campaign(?, ?, ?)}");
        insert.setInt(1, candidate_id);
        insert.setInt(2, manager_id);
        insert.setInt(3, election_id);

        var insert_result = insert.executeQuery();
        insert_result.next();
        var campaign_id = insert_result.getInt("campaign_id");
        System.out.println("inserted a new campaign with campaign_id = " + campaign_id);
        return campaign_id;
    }

    public static int fetch_or_insert_election(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("what is the date (yyyy-mm-dd) of the election?");
        var date = Date.valueOf(scanner.nextLine());

        System.out.println("what is the registration deadline (yyyy-mm-dd) of the election?");
        var deadline = Date.valueOf(scanner.nextLine());

        var select = conn
                .prepareStatement("select election_id from election where date = ? and registration_deadline = ?");
        select.setDate(1, date);
        select.setDate(2, deadline);

        var result = select.executeQuery();
        while (result.next()) {
            int election_id = result.getInt("election_id");
            System.out.println("found existing election with election_id = " + election_id);
            return election_id;
        }

        var insert = conn.prepareCall("{call insert_election(?, ?)}");
        insert.setDate(1, date);
        insert.setDate(2, deadline);

        result = insert.executeQuery();
        result.next();

        int election_id = result.getInt("election_id");
        System.out.println("inserted new election with election_id = " + election_id);
        return election_id;
    }

    public static void display_donations(Connection conn) throws SQLException {
        var stmt = conn.prepareStatement("select * from donation");
        var result = stmt.executeQuery();

        while (result.next()) {
            System.out
                    .println("donation_id=" + result.getInt("donation_id") + ",person_id=" + result.getInt("person_id")
                            + ",campaign_id=" + result.getInt("campaign_id") + ",amount=" + result.getInt("amount"));
        }
    }

    public static int insert_donation(Connection conn, Scanner scanner) throws SQLException {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the person_id of the donation's donor (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int donor_id;
        if (cmd.equals("n")) {
            donor_id = fetch_or_insert_person(conn, scanner);
        } else {
            System.out.println("enter the person_id of the donation's donor:");
            donor_id = scanner.nextInt();
            scanner.nextLine();
        }

        cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the campaign_id of the campaign the donation was made too (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int campaign_id;
        if (cmd.equals("n")) {
            campaign_id = fetch_or_insert_campaign(conn, scanner);
        } else {
            System.out.println("enter the campaign_id:");
            campaign_id = scanner.nextInt();
            scanner.nextLine();
        }

        int donation_amount;
        System.out.println("enter the donation amount in cents (i.e. 1 dollar becomes 100 cents):");
        donation_amount = scanner.nextInt();
        scanner.nextLine();

        var insert = conn.prepareCall("{call insert_donation(?, ?, ?)}");
        insert.setInt(1, donor_id);
        insert.setInt(2, campaign_id);
        insert.setInt(3, donation_amount);

        insert.execute();
        var result = insert.getGeneratedKeys();
        result.next();

        var donation_id = result.getInt("donation_id");
        System.out.println("added a new donation with donation_id = " + donation_id);
        return donation_id;
    }

    public static void delete_donation(Connection conn, Scanner scanner) throws SQLException {
        String cmd = "";
        while (!cmd.equals("n") && !cmd.equals("y")) {
            System.out.println("do you know the donation_id of the donation you want to delete (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        if (cmd.equals("n")) {
            display_donations(conn);
        }

        System.out.println("enter the donation_id you want to delete:");
        var donation_id = scanner.nextInt();
        scanner.nextLine();

        var delete = conn.prepareCall("{call delete_donation(?)}");
        delete.setInt(1, donation_id);
        delete.executeUpdate();
    }
}
