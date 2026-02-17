package com.qow.minecraft.server;

import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.net.qtcp.TCPClient;

import java.nio.charset.StandardCharsets;

/**
 * {@link CommandControllerServer}への信号を送る
 *
 * @version 2025/10/08
 * @since 1.0.0
 */
public class CommandControllerClient extends TCPClient {
    /**
     * {@link CommandControllerServer}のホスト名､ポート番号､バイトサイズを指定し初期化する
     *
     * @param host       ホスト名
     * @param port       ポート番号
     * @param protocolID 識別子
     * @param byteSize   バイトサイズ
     */
    public CommandControllerClient(String host, int port, byte[] protocolID, int byteSize) {
        super(host, port, protocolID);
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
