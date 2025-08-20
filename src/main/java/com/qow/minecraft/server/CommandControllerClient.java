package com.qow.minecraft.server;

import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * {@link CommandControllerServer}への信号を送る
 *
 * @version 2025/08/20
 * @since 1.0.0
 */
public class CommandControllerClient {
    private final String ip;
    private final int port;
    private final int byteSize;

    /**
     * {@code qonFile}によって指定されたconfigファイルに従って{@link CommandControllerServer}のIPアドレス､ポート番号､バイトサイズを指定し初期化する
     *
     * @param qonFile qonで記述されたconfigファイルへのパス
     * @throws UntrustedQONException qonファイルに不備がある場合
     * @throws IOException           qonファイルに問題が生じた場合
     */
    public CommandControllerClient(File qonFile) throws UntrustedQONException, IOException {
        QONObject qonObject = new QONObject(qonFile);
        QONObject controlJs = qonObject.getQONObject("control");
        ip = controlJs.get("server-ip");
        port = Integer.parseInt(controlJs.get("port"));
        byteSize = Integer.parseInt(controlJs.get("byte-size"));
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
