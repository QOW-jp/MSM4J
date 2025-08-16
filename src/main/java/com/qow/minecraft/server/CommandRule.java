package com.qow.minecraft.server;

import com.qow.util.Webhook;
import org.json.JSONObject;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Minecraftのコマンドラインへ送るコマンドを制御する
 *
 * @version 2025/08/16
 * @since 1.0.0
 */
public class CommandRule {
    private BufferedWriter bufferedWriter;
    private ProcessManager processManager;
    private String webhookUrl;
    private String notificationTimeFormat;
    private boolean serverStatusNotification, logInOutNotification;

    /**
     * バッファーの初期化
     */
    public CommandRule() {
        bufferedWriter = null;
    }

    /**
     * {@link ProcessManager}で実行中のコマンドラインに入力する
     *
     * @param line 入力する文字列
     * @throws IOException {@link ProcessManager}のコマンドラインが無効化されていた場合またはバックアップに不備があった場合
     */
    public void command(String line) throws IOException {
        if (line.equals("BACKUP")) {
            try {
                processManager.backup(true, true);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } else if (line.equals("START")) {
            processManager.start();
        } else if (line.equals("STOP")) {
            if (processManager.getServerStatus()) processManager.requestStopServer(0, "");
        } else if (line.equals("RESTART")) {
            try {
                processManager.restart();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        } else {
            if (!processManager.getServerStatus()) return;
            processManager.log(line);
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }
    }

    /**
     * Minecraftのコマンドラインの文字列を引数として呼ばれる<br>
     * このメソッドをオーバーライドして特定の文字列に反応する処理を実行できる
     *
     * @param line Minecraftのコマンドラインの文字列
     */
    public void commandLine(String line) {

    }

    /**
     * Minecraftのコマンドラインの文字列を引数として呼ばれる<br>
     * 文字列がプレイヤーの発言だった場合を判定する
     *
     * @param line Minecraftのコマンドラインの文字列
     * @return プレイヤーの発言だった場合true
     */
    public boolean isPlayerComment(String line) {
        return line.matches(".*<.*>.*");
    }

    /**
     * これにより取得した{@link ProcessManager}を用いて{@link ProcessManager#backup(boolean, boolean)}などのネイティブな捜査を行える
     *
     * @return Minecraftを実行しているProcessManager
     */
    public ProcessManager getProcessManager() {
        return processManager;
    }

    protected void setProcessManager(ProcessManager processManager) {
        this.processManager = processManager;
    }

    protected void setCommandStream(BufferedWriter bw) {
        this.bufferedWriter = bw;
    }

    protected void setWebhook(JSONObject json) {
        webhookUrl = json.getString("webhook-url");
        serverStatusNotification = json.getBoolean("server-status");
        logInOutNotification = json.getBoolean("log-in-out");
        notificationTimeFormat = json.getString("time-format");
    }

    protected void commandsLine(String line) throws IOException, InterruptedException {
        commandLine(line);

        if (isPlayerComment(line)) return;
        if (serverStatusNotification) serverStatus(line);
        if (logInOutNotification) logInOut(line);
    }

    protected void closeCommandStream() throws IOException {
        if (bufferedWriter != null) {
            bufferedWriter.close();
        }
    }

    private void logInOut(String line) {
        if (line.contains("joined the game")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            //プレイヤー名抜き出し
            String name = line.split(" ")[3];
            new Webhook(webhookUrl, name + " LOG IN " + sdf.format(new Date()), Color.GREEN);
        } else if (line.contains("left the game")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            //プレイヤー名抜き出し
            String name = line.split(" ")[3];
            new Webhook(webhookUrl, name + " LOG OUT " + sdf.format(new Date()), Color.RED);
        }
    }

    private void serverStatus(String line) {
        if (line.contains("[Server thread/INFO]: Done")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            new Webhook(webhookUrl, "SERVER START " + sdf.format(new Date()), Color.WHITE);
        } else if (line.contains("[Server thread/INFO]: Stopping the server")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            new Webhook(webhookUrl, "SERVER STOP " + sdf.format(new Date()), Color.BLACK);
        }
    }

}
