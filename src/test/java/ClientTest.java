import com.qow.minecraft.server.CommandControllerClient;
import com.qow.qtcp.ClosedServerException;
import com.qow.qtcp.UntrustedConnectException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException, UntrustedQONException, UntrustedConnectException, ClosedServerException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        QONObject qonObject = new QONObject(new File(path));
        QONObject control = qonObject.getQONObject("control");
        String hostname = control.get("server-ip");
        int port = Integer.parseInt(control.get("port"));
        int byteSize = Integer.parseInt(control.get("byte-size"));

        CommandControllerClient ccc = new CommandControllerClient(hostname, port, byteSize);

        Scanner sc = new Scanner(System.in);

        System.out.print(">>");
        String cmd = sc.nextLine();
        System.out.println(cmd + " : " + ccc.command(cmd));

//        while (true) {
//            System.out.print(">>");
//            String cmd = sc.nextLine();
//            if (cmd.equals("STOP")) break;
//            System.out.println(cmd + " : " + ccc.command(cmd));
//        }

        sc.close();
    }
}