import com.qow.minecraft.server.CommandControllerClient;
import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException, UntrustedQONException, UntrustedConnectException, ClosedServerException, NoSuchKeyException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        QONObject qonObject = new QONObject(new File(path));
        QONObject control = qonObject.getQONObject("control");
        String hostname = control.get("server-ip");
        byte[] protocolID = control.get("protocol-id").getBytes(StandardCharsets.UTF_8);
        int byteSize = Integer.parseInt(control.get("byte-size"));

        int port;
        boolean autoPorting = Boolean.parseBoolean(control.get("auto"));
        if (autoPorting) {
            Path portTempPath = Paths.get(control.get("port-temp"));
            List<String> lines = Files.readAllLines(portTempPath, StandardCharsets.UTF_8);
            port = Integer.parseInt(lines.get(0));
        } else {
            port = Integer.parseInt(control.get("port"));
        }

        CommandControllerClient ccc = new CommandControllerClient(hostname, port, protocolID, byteSize);

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