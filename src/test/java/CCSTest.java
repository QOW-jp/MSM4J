import com.qow.qtcp.TCPServer;
import com.qow.qtcp.UntrustedConnectException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CCSTest extends TCPServer {
    public final static byte[] PROTOCOL_ID = "msm4j-s1.2.0".getBytes(StandardCharsets.UTF_8);
    private boolean run;

    public CCSTest(int port, byte[] protocolID, String clientIP) throws IOException, UntrustedConnectException {
        super(port, protocolID, clientIP);

        run = true;
        while (run) {
            listeningRequest();
        }
    }

    public static void main(String[] args) throws IOException, UntrustedConnectException {
        new CCSTest(9999, PROTOCOL_ID, "localhost");
    }

    @Override
    public byte[] read(byte[] data) {
        String line = new String(data, StandardCharsets.UTF_8);
        System.out.println("receive:" + line);
        if (line.equals("stop")) {
            run = false;
        }
        return data;
    }
}
