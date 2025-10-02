package com.qow.minecraft.server;

import com.qow.qtcp.ClosedServerException;
import com.qow.qtcp.TCPClient;
import com.qow.qtcp.UntrustedConnectException;

import java.nio.charset.StandardCharsets;

/**
 * {@link CommandControllerServer}への信号を送る
 *
 * @version 2025/09/29
 * @since 1.0.0
 */
public class CommandControllerClient extends TCPClient {
    /**
     * {@link CommandControllerServer}のホスト名､ポート番号､バイトサイズを指定し初期化する
     *
     * @param hostname ホスト名
     * @param port     ポート番号
     */
    public CommandControllerClient(String hostname, int port, int byteSize) {
        super(hostname, port, CommandControllerServer.PROTOCOL_ID);
        setByteSize(byteSize);
    }


    /**
     * {@link CommandControllerServer}へ通信する<br>
     * 文字コードはUTF_8を用いる
     *
     * @param line 送信する文字列
     * @return 送信先からの返信が送信した文字列と同じだった場合trueを返す
     * @throws UntrustedConnectException プロトコルIDが違った場合
     * @throws ClosedServerException     サーバーが存在しない場合
     */
    public boolean command(String line) throws UntrustedConnectException, ClosedServerException {
        //文字列をUTF-8形式のバイト配列に変換して送受信
        byte[] receiveData = request(line.getBytes(StandardCharsets.UTF_8));
        String receiveLine = new String(receiveData, StandardCharsets.UTF_8);

        return line.equals(receiveLine);
    }
}
