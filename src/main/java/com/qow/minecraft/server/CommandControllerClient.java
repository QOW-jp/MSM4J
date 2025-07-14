package com.qow.minecraft.server;

import com.qow.util.JsonReader;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * {@link CommandControllerServer}への信号を送る
 *
 * @version 2025/07/14
 * @since 1.0.0
 */
public class CommandControllerClient {
    private final String ip;
    private final int port;
    private final int byteSize;

    /**
     * {@code jsonPath}によって指定されたconfigファイルに従って{@link CommandControllerServer}のIPアドレス､ポート番号､バイトサイズを指定し初期化する
     *
     * @param jsonPath jsonで記述されたconfigファイルへのパス
     */
    public CommandControllerClient(String jsonPath) {
        JsonReader jsonReader = new JsonReader(jsonPath);
        JSONObject controlJs = jsonReader.getJSONObject("control");
        ip = controlJs.getString("client-ip");
        port = controlJs.getInt("port");
        byteSize = controlJs.getInt("byte-size");
    }

    /**
     * {@link CommandControllerServer}へ通信する<br>
     * 文字コードはUTF_8を用いる
     *
     * @param line 送信する文字列
     * @return 送信先からの返信が送信した文字列と同じだった場合trueを返す
     * @throws IOException 送受信に失敗した場合
     */
    public boolean command(String line) throws IOException {
        try (Socket socket = new Socket(ip, port)) {
            try (OutputStream out = socket.getOutputStream(); InputStream in = socket.getInputStream()) {
                //受信データバッファ
                byte[] data = new byte[byteSize];

                //文字列をUTF-8形式のバイト配列に変換して送信
                out.write(line.getBytes(StandardCharsets.UTF_8));

                //データを受信
                int readSize = in.read(data);
                //受信データを読み込んだサイズまで切り詰め
                byte[] receiveData = Arrays.copyOf(data, readSize);
                String receiveLine = new String(receiveData, StandardCharsets.UTF_8);

                return line.equals(receiveLine);
            }
        }
    }
}
