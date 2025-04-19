package CampaignManager;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UseCase1 {
    public static void run(Connection conn, Cli cli) throws Exception {
        var uid = Utility.login(conn, cli);

        var findLocalCall = conn.prepareCall("{ call find_local_candidates(?) }");
        findLocalCall.setInt(1, uid);
        var findLocalResult = findLocalCall.executeQuery();

        var candidateIds = new ArrayList<>();
        var candidateChoices = new ArrayList<>();
        while (findLocalResult.next()) {
            candidateIds.add(findLocalResult.getInt("person_id"));

            candidateChoices.add(
                findLocalResult.getString("first") + " " +
                findLocalResult.getString("last") + " for the election on " +
                findLocalResult.getString("election_date")
            );
        }

        System.out.println("Your local candidates are:");
        var id = cli.choice(
            "Which candidate would you like to see events for?",
            candidateChoices.toArray(String[]::new)
        );

        String[] types = {
            "town hall",
            "gotv",
            "rally",
            "phone bank",
            "other"
        };

        String filter = null;
        if (cli.askBool("Would you like to filter by event type?")) {
            filter = Arrays.stream(cli.selectMultiple("Select the events you would like to include", types))
                .mapToObj(k -> types[k])
                .collect(Collectors.joining(","));
        }

        var queryEventsCall = conn.prepareCall("{ call query_events(?, ?) }");
        queryEventsCall.setInt(1, id);
        queryEventsCall.setString(2, filter);

        var queryEventsResult = queryEventsCall.executeQuery();
        var eventIds = new ArrayList<Integer>();
        var eventChoices = new ArrayList<>();
        var eventNames = new ArrayList<>();
        while (queryEventsResult.next()) {
            eventIds.add(queryEventsResult.getInt("event_id"));

            eventChoices.add(
                queryEventsResult.getString("name") + ": " +
                queryEventsResult.getString("description") + ", from " +
                queryEventsResult.getString("time_start") + " to " +
                queryEventsResult.getString("time_end")
            );

            eventNames.add(queryEventsResult.getString("name"));
        }
        
        var events = cli.selectMultiple("Which events would you like to register for?", eventChoices.toArray(String[]::new));
        for (int i: events) {
            var call = conn.prepareCall("{ call register_event(?, ?, ?) }");
            call.setInt(1, eventIds.get(i));
            call.setInt(2, uid);
            call.setString(3, "utm_source=cli");

            call.execute();
            System.out.println("Registered for " + eventNames.get(i) + ".");
        }
    }
}
