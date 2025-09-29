package com.qow.minecraft.server;

import com.qow.qtcp.TCPServer;
import com.qow.util.qon.UntrustedQONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * {@link CommandControllerClient}から受信した文字列を{@link CommandRule}を通して{@link ProcessManager}に送信､または特定のメソッドを呼び出す
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class CommandControllerServer extends TCPServer implements Runnable {
    public final static byte[] PROTOCOL_ID = "msm4j-s1.2.0".getBytes();
    private final Thread thread;
    private final ServerSocket serverSocket;
    private CommandRule commandRule;
    private boolean run;
    private boolean enable;

    protected CommandControllerServer(int port, int byteSize, String clientIP) throws IOException, UntrustedQONException {
        super(port, PROTOCOL_ID, byteSize);

        serverSocket = new ServerSocket(port, 50, InetAddress.getByName(clientIP));

        run = false;
        enable = false;
        thread = new Thread(this);
    }

    protected CommandControllerServer(int port, int byteSize) throws IOException, UntrustedQONException {
        super(port, PROTOCOL_ID, byteSize);

        serverSocket = new ServerSocket(port);

        run = false;
        thread = new Thread(this);
    }
    public void setEnable(boolean enable){
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
            if (serverSocket != null) serverSocket.close();
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
            try (Socket sock = serverSocket.accept()) {
                try (InputStream in = sock.getInputStream(); OutputStream out = sock.getOutputStream()) {
                    //受信データバッファ
                    byte[] data = new byte[byteSize];

                    int readSize = in.read(data);
                    if (readSize == -1) continue;

                    //受信データを読み込んだサイズまで切り詰め
                    byte[] receiveData = Arrays.copyOf(data, readSize);

                    //バイト配列を文字列に変換
                    String line = new String(receiveData, StandardCharsets.UTF_8);

                    commandRule.command(line);

                    //送られてきた文字列をUTF-8形式のバイト配列に変換して返信
                    out.write(line.getBytes(StandardCharsets.UTF_8));

                    if (line.equals("STOP")) {
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                err = 0;
            } catch (IOException e) {
                if (1 < err++) e.printStackTrace();
            }
        }
        run = false;
    }

    @Override
    public byte[] read(byte[] data){
        String line = new String(data, StandardCharsets.UTF_8);
        try {
            commandRule.command(line);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (line.equals("STOP")) {
            run = false;
        }
        return line.getBytes(StandardCharsets.UTF_8);
    }
}
