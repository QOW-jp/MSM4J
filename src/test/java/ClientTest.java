import com.qow.minecraft.server.CommandControllerClient;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException, UntrustedQONException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        CommandControllerClient ccc = new CommandControllerClient(new File(path));

        Scanner sc = new Scanner(System.in);

//        while (true) {
//            System.out.print(">>");
//            String cmd = sc.nextLine();
//            if (cmd.equals("STOP")) break;
//            System.out.println(cmd + " : " + ccc.command(cmd));
//        }

        System.out.print(">>");
        String cmd = sc.nextLine();
        System.out.println(cmd + " : " + ccc.command(cmd));

        sc.close();
    }
}