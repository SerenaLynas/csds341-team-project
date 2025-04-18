package CampaignManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Scanner;

public class Utility {
    public static void display_issues(Connection conn) {
        try (var stmt = conn.prepareStatement("select * from issues")) {
            var result = stmt.executeQuery();
            while (result.next()) {
                int id = result.getInt("issue_id");
                String desc = result.getString("description");
                System.out.println("issue_id: " + id + ", description: " + desc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return insert_result.getInt("person_id");
    }
}
