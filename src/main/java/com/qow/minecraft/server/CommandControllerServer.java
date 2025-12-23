package com.qow.minecraft.server;

import com.qow.net.qtcp.TCPServer;
import com.qow.net.UntrustedConnectException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * {@link CommandControllerClient}から受信した文字列を{@link CommandRule}を通して{@link ProcessManager}に送信､または特定のメソッドを呼び出す
 *
 * @version 2025/12/19
 * @since 1.0.0
 */
public class CommandControllerServer extends TCPServer implements Runnable {
    private Thread thread;
    private CommandRule commandRule;
    private boolean run;

    protected CommandControllerServer(int port, byte[] protocolID, int byteSize, String clientIP) throws IOException {
        super(port, protocolID, clientIP);

        setByteSize(byteSize);
        init();
    }

    protected CommandControllerServer(int port, byte[] protocolID, int byteSize) throws IOException {
        super(port, protocolID);

        setByteSize(byteSize);
        init();
    }

    private void init() {
        run = false;
        thread = new Thread(this);
    }


    /**
     * サーバーを開くためのリクエストをする
     *
     * @return 既にサーバーが開いていたり実行可能ではない場合にfalseが返される
     */
    public synchronized boolean start() {
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
                err = 0;
            } catch (IOException | UntrustedConnectException e) {
                if (1 < err++) System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public byte[] read(byte[] data) {
        String line = new String(data, StandardCharsets.UTF_8);
        if (line.equals("STOP")) {
            run = false;
        }
        try {
            commandRule.commandServer(line);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return line.getBytes(StandardCharsets.UTF_8);
    }
}
