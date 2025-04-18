package CampaignManager;

import java.util.Arrays;
import java.util.Scanner;

public class Cli {
    private Scanner scanner;
    public Cli(Scanner scanner) {
        this.scanner = scanner;
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public boolean askBool(String msg) {
        String cmd = "";
        while (!cmd.equals("y") && !cmd.equals("n")) {
            System.out.println(msg + " (y/n)");
            cmd = scanner.nextLine().toLowerCase();
        }

        return cmd.equals("y");
    }

    public int askInt(String msg) {
        System.out.println(msg + " (enter an integer)");
        return scanner.nextInt();
    }

    public int choices(String question, String... choices) {
        System.out.println(question);

        int i = 0;
        for (String choice: choices) {
            System.out.println("  " + choice + "(" + (1 + i++) + ")");
        }

        while (true) {
            System.out.println("(enter an integer 1 to " + i + ")");
            var ans = scanner.nextInt();
            if (0 < ans && ans < i) {
                return ans;
            }
        }
    }

    public int[] selectMultiple(String question, String... choices) {
        System.out.println(question);

        int i = 0;
        for (String choice: choices) {
            System.out.println("  " + choice + "(" + (1 + i++) + ")");
        }

        while (true) {
            System.out.println("(enter a list of integers 1 to " + i + ", e.g. 1,3,4)");
            var ans = scanner.nextLine();
            var arr = Arrays.stream(ans.split(",")).mapToInt(s -> Integer.parseInt(s)).toArray();
            if (Arrays.stream(arr).allMatch(k -> 0 < k && k < choices.length)) {
                return arr;
            }
        }
    }
}