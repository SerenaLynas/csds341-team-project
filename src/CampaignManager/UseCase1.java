package CampaignManager;

import java.sql.Connection;
import java.sql.SQLException;

public class UseCase1 {
    public static void run(Connection conn, Cli cli) throws SQLException {
        var voter_id = cli.askInt("What is your user ID?");
        var call = conn.prepareCall("{call get_person(?)}");
        call.setInt(1, voter_id);
        var result = call.executeQuery();

        while (result.next()) {
            
        }
    }
}
