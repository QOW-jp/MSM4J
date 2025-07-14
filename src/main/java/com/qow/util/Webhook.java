package com.qow.util;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * webhookを送信する
 *
 * @version 2025/07/14
 * @since 1.0.0
 */
public class Webhook {
    private int responseCode;

    /**
     * webhookを送信する
     *
     * @param webhookUrl webhookのURL
     * @param text       送信する文字列
     * @param color      送信する色
     */
    public Webhook(String webhookUrl, String text, Color color) {
        try {
            //WebhookのURL
            URL url = URI.create(webhookUrl).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //HTTP設定
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            //JSONデータ
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            int colorCode = (red << 16) + (green << 8) + blue;
            String json = "{\"embeds\": [{\"title\":\"" + text + "\",\"color\": " + colorCode + "}]}";

            //ボディに書き込み
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            //レスポンス取得
            responseCode = conn.getResponseCode();
        } catch (IOException e) {
            System.err.println("failed to webhook.");
            System.out.println(e.getMessage());
            responseCode = -1;
        }
    }

    /**
     * コンストラクタで送信したwebhookのレスポンスコードを取得する
     *
     * @return レスポンスコード
     */
    public int getResponseCode() {
        return responseCode;
    }
}
