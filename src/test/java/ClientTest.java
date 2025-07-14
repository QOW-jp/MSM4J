import com.qow.minecraft.server.CommandControllerClient;

import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        CommandControllerClient ccc = new CommandControllerClient(args[0]);

        Scanner sc = new Scanner(System.in);

//        while (true) {
//            System.out.print(">>");
//            String cmd = sc.nextLine();
//            if (cmd.equals("exit")) break;
//            System.out.println(cmd + " : " + ccc.command(cmd));
//        }

        System.out.print(">>");
        String cmd = sc.nextLine();
        System.out.println(cmd + " : " + ccc.command(cmd));

        sc.close();
    }
}