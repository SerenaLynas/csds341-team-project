package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class UseCase2 {
    public static int STATUTORY_LIMIT = 5500000;

    public static void run(Connection conn, Cli cli) throws Exception {
        var uid = Utility.login(conn, cli);

        var issuesResult = conn.prepareCall("{ call get_issues() }").executeQuery();

        int campaign_id;
        if (cli.askBool("Do you know the campaigns's ID?")) {
            campaign_id = cli.askInt("Campaign's ID");
        } else {
            var issueIds = new ArrayList<Integer>();
            var issueDescs = new ArrayList<>();
            while (issuesResult.next()) {
                issueIds.add(issuesResult.getInt("issue_id"));
                issueDescs.add(issuesResult.getString("description"));
            }
    
            var issueIndex = cli.choice("What issue would you like to focus on?", issueDescs.toArray(String[]::new));
            var issueId = issueIds.get(issueIndex);

            var campaignIds = new ArrayList<Integer>();
            var candidateDesc = new ArrayList<>();
            var findCandidatesCall = conn.prepareCall("{ call find_candidates_by_issue(?) }");
            findCandidatesCall.setInt(0, issueId);
            var result = findCandidatesCall.executeQuery();

            while (result.next()) {
                campaignIds.add(result.getInt("campaign_id"));
                candidateDesc.add(
                    result.getString("first") + " " +
                    result.getString("last") + " " +
                    result.getString("district")
                );
            }

            campaign_id = campaignIds.get(
                cli.choice("Which campaign would you like to donate to?", candidateDesc.toArray(String[]::new))
            );
        }

        System.out.println("How much would you like to donate? (enter a currency amount, e.g. 100.00)");
        var str = cli.getScanner().nextLine().trim().split(".");
        int amnt = Integer.parseInt(str[0]) * 100;
        if (str.length > 1) {
            amnt += Integer.parseInt(str[1]);
        }

        var call = conn.prepareCall("{ call make_donation(?, ?, ?, ?) }");
        call.setInt(1, uid);
        call.setInt(2, campaign_id);
        call.setInt(3, amnt);
        call.setInt(4, STATUTORY_LIMIT);

        call.execute();

        System.out.println("Made a donation of " + str + ".");
    }
}
