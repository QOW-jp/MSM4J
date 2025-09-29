package com.qow.minecraft.server;

import com.qow.qtcp.TCPServer;
import com.qow.qtcp.UntrustedConnectException;
import com.qow.util.qon.UntrustedQONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {@link CommandControllerClient}から受信した文字列を{@link CommandRule}を通して{@link ProcessManager}に送信､または特定のメソッドを呼び出す
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class CommandControllerServer extends TCPServer implements Runnable {
    public final static byte[] PROTOCOL_ID = "msm4j-s1.2.0".getBytes();
    private Thread thread;
    private CommandRule commandRule;
    private boolean run;
    private boolean enable;

    protected CommandControllerServer(int port, int byteSize, String clientIP) throws IOException, UntrustedQONException {
        super(port, PROTOCOL_ID, clientIP);

        setByteSize(byteSize);
        init();
    }

    protected CommandControllerServer(int port, int byteSize) throws IOException, UntrustedQONException {
        super(port, PROTOCOL_ID);

        setByteSize(byteSize);
        init();
    }

    private void init() {
        run = false;
        enable = false;
        thread = new Thread(this);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    /**
     * サーバーを開くためのリクエストをする
     *
     * @return 既にサーバーが開いていたり実行可能ではない場合にfalseが返される
     */
    public synchronized boolean start() {
        if (!enable) return false;
        if (run) return false;
        if (commandRule == null) return false;
        if (thread == null) return false;
        thread.start();
        return true;
    }

    /**
     * 瞬時にサーバーを強制的に止める
     */
    public void stop() {
        run = false;
        try {
            close();
        } catch (IOException ignored) {
        }
    }

    protected void setCommandRule(CommandRule commandRule) {
        this.commandRule = commandRule;
    }

    @Override
    public void run() {
        run = true;
        int err = 0;
        while (run) {
            try {
                listeningRequest();
            } catch (IOException | UntrustedConnectException e) {
                if (1 < err++) e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] read(byte[] data) {
        String line = new String(data, StandardCharsets.UTF_8);
        try {
            commandRule.command(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        if (line.equals("STOP")) {
            run = false;
        }
        return line.getBytes(StandardCharsets.UTF_8);
    }
}
