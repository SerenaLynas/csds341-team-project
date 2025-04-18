import java.sql.DriverManager;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        String connectionUrl = "jdbc:sqlserver://cxp-sql-03\\tak112;"
                + "database=university;"
                + "user=sa;"
                + "password=YVaTAtNDMgAEr3;"
                + "encrypt=true;"
                + "trustServerCertificate=true;"
                + "loginTimeout=15;";

        var conn = DriverManager.getConnection(connectionUrl);
        var scanner = new Scanner(System.in);

        String name;
        String dept_name;
        int tot_cred;

        System.out.print("name: ");
        name = scanner.nextLine();

        System.out.print("dept_name: ");
        dept_name = scanner.nextLine();

        System.out.print("tot_cred: ");
        tot_cred = scanner.nextInt();

        scanner.close();

        var callable = conn.prepareCall("{call insertStudent2(?, ?, ?)}");
        callable.setString(1, name);
        callable.setString(2, dept_name);
        callable.setInt(3, tot_cred);
        callable.execute();
        
        var ret = callable.getResultSet();
        while (ret.next()) {
            System.out.println("returned ID: " + ret.getInt("ID"));
        }
    }
}
