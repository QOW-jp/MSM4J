import com.qow.qtcp.ClosedServerException;
import com.qow.qtcp.TCPClient;
import com.qow.qtcp.UntrustedConnectException;

import java.nio.charset.StandardCharsets;

public class CCCTest {
    public static void main(String[] args) throws ClosedServerException, UntrustedConnectException {
        TCPClient client = new TCPClient("localhost",9999,CCSTest.PROTOCOL_ID);
        String msg = "stop";
        byte[] receive = client.request(msg.getBytes(StandardCharsets.UTF_8));
        System.out.println(new String(receive,StandardCharsets.UTF_8));
    }
}
