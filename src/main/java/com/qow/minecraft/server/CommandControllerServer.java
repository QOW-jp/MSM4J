package com.qow.minecraft.server;

import com.qow.util.JsonReader;
import org.json.JSONObject;

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
 * @version 2025/07/14
 * @since 1.0.0
 */
public class CommandControllerServer implements Runnable {
    private final Thread thread;
    private final ServerSocket serverSocket;
    private final int byteSize;
    private final boolean controllable;
    private CommandRule commandRule;
    private boolean run;    //Thread実行中かどうか

    protected CommandControllerServer(String path) throws IOException {
        run = false;

        JsonReader jsonReader = new JsonReader(path);
        JSONObject controlJs = jsonReader.getJSONObject("control");

        controllable = controlJs.getBoolean("controllable");

        byteSize = controlJs.getInt("byte-size");
        boolean bind = controlJs.getBoolean("bind-ip");
        int port = controlJs.getInt("port");

        if (bind) {
            String clientIp = controlJs.getString("client-ip");
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(clientIp));
        } else {
            serverSocket = new ServerSocket(port);
        }

        thread = new Thread(this);
    }

    /**
     * サーバーを開くためのリクエストをする
     *
     * @return 既にサーバーが開いていたり実行可能ではない場合にfalseが返される
     */
    public synchronized boolean start() {
        if (!controllable) return false;
        if (run) return false;
        if (commandRule == null) return false;
        if (thread == null) return false;
        thread.start();
        return true;
    }

    /**
     * サーバーを止めるリクエストをする<br>
     * 次回の{@link CommandControllerClient}から受信後再度の受信をしない
     *
     * @deprecated
     */
    public void stop() {
        run = false;
    }

    protected void setCommandRule(CommandRule commandRule) {
        this.commandRule = commandRule;
    }

    @Override
    public void run() {
        run = true;
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        run = false;
    }
}
