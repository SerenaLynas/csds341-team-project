package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class UseCase7 {
    public static void run(Connection conn, Scanner scanner) throws SQLException {
        System.out.println(
                "would you like to insert a new donation, delete an existing donation, or find information about existing donations?");

        String cmd = "";
        while (!cmd.equals("insert") && !cmd.equals("find") && !cmd.equals("delete")) {
            System.out.println(
                    "enter `insert` to add a new donation, `delete` to delete an existing donation, or `find` find information about existing donations:");
            cmd = scanner.nextLine();
        }

        if (cmd.equals("insert")) {
            Utility.insert_donation(conn, scanner);
        } else if (cmd.equals("delete")) {
            Utility.delete_donation(conn, scanner);
        } else {
            find_donations(conn, scanner);
        }
    }

    private static void find_donations(Connection conn, Scanner scanner) throws SQLException {

        String cmd = "";
        while (!cmd.equals("campaign") && !cmd.equals("similarity")) {
            System.out.println(
                    "enter `campaign` if you want to find historically large donors for a given campaign or `similarity` if you want to find large donors to campaigns that are similar to yours");
            cmd = scanner.nextLine();
        }

        if (cmd.equals("campaign")) {
            find_donors_within_campaign(conn, scanner);
        } else {
            find_donors_across_campaigns(conn, scanner);
        }
    }

    private static void find_donors_within_campaign(Connection conn, Scanner scanner) throws SQLException {
        var callable = conn.prepareCall("{call find_largest_donors(?)}");

        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know the campaign_id of the campaign you are interested in (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int campaign_id;
        if (cmd.equals("n")) {
            campaign_id = Utility.fetch_or_insert_campaign(conn, scanner);
        } else {
            System.out.println("enter the campaign_id:");
            campaign_id = scanner.nextInt();
            scanner.nextLine();
        }

        callable.setInt(1, campaign_id);
        var result = callable.executeQuery();

        System.out.println("name,phone,email,total donations");
        while (result.next()) {
            System.out.println(result.getString("first") + " " + result.getString("last") +
                    "," + result.getString("phone") + "," + result.getString("email") + "," +
                    + result.getInt("total_donations"));
        }
    }

    private static void find_donors_across_campaigns(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("this will find the top donors from the most similar campaigns to yours.\n" +
                "How many campaigns would you like to generate donors from?");
        int count = scanner.nextInt();
        scanner.nextLine();

        var pq = new PriorityQueue<SimilarityResult>(count);

        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println("do you know your campaign_id (y/n/Y/N)?");
            cmd = scanner.nextLine().toLowerCase();
        }

        int campaign_id;
        if (cmd.equals("n")) {
            campaign_id = Utility.fetch_or_insert_campaign(conn, scanner);
        } else {
            System.out.println("enter your campaign_id");
            campaign_id = scanner.nextInt();
            scanner.nextLine();
        }

        var fetch_all_campaign_ids = conn.prepareStatement("select campaign_id from campaign where campaign_id != ?");
        fetch_all_campaign_ids.setInt(1, campaign_id);
        var campaign_similarity = conn.prepareCall("{call check_campaign_similarity(?, ?)}");
        campaign_similarity.setInt(1, campaign_id);

        var other_campaign_ids = fetch_all_campaign_ids.executeQuery();

        // keep only the top count campaigns by number of similar issues
        while (other_campaign_ids.next()) {
            var similarity_result = new SimilarityResult();
            similarity_result.campaign_id = other_campaign_ids.getInt("campaign_id");
            campaign_similarity.setInt(2, similarity_result.campaign_id);
            var similarity = campaign_similarity.executeQuery();
            similarity.next();
            similarity_result.count = similarity.getInt(1);

            pq.add(similarity_result);
            if (pq.size() > count) {
                pq.poll();
            }
        }

        // for each campaign, collect the largest donors
        var find_largest_donors = conn.prepareCall("{call find_largest_donors(?)}");

        var top_donors = new HashSet<String>();
        while (!pq.isEmpty()) {
            var other_campaign_id = pq.poll().campaign_id;
            find_largest_donors.setInt(1, other_campaign_id);
            var result = find_largest_donors.executeQuery();

            while (result.next()) {
                top_donors.add(result.getString("first") + " " + result.getString("last") + ","
                        + result.getString("phone") + "," + result.getString("email"));
            }
        }

        System.out.println("name,phone,email");
        for (var donor : top_donors) {
            System.out.println(donor);
        }
    }

    static private class SimilarityResult implements Comparable<SimilarityResult> {
        int count;
        int campaign_id;

        @Override
        public int compareTo(SimilarityResult other) {
            return Integer.compare(count, other.count);
        }
    }
}
