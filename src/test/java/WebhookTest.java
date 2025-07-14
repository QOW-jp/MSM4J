import com.qow.util.Webhook;

import java.awt.*;

public class WebhookTest {
    public static void main(String[] args) {
        String url = "https://discord.com/api/webhooks/1390936104894599259/Lvx7NJSEAuPQ-UNMPx1YsTMjx9vnC1Uln5qcMF22u1MYXz-kSppXXJdu79qY3V9rJrKl";
        String text = "WebhookTest";
        Color color = new Color(63, 244, 222);
        new Webhook(url, text, color);
    }
}
