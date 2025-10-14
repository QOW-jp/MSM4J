package com.qow.minecraft.server;

import com.qow.util.Webhook;
import com.qow.util.qon.NoSuchKeyException;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Minecraftのコマンドラインへ送るコマンドを制御する
 *
 * @version 2025/10/14
 * @since 1.0.0
 */
public class CommandRule {
    private final List<String> playerList;
    private BufferedWriter bufferedWriter;
    private ProcessManager processManager;
    private String webhookUrl;
    private String notificationTimeFormat;
    private boolean serverStatusNotification, logInOutNotification;
    private int logInOutIndex;

    /**
     * コマンド出力バッファーとプレイヤーネームリストの初期化
     */
    public CommandRule() {
        playerList = new ArrayList<>();
        bufferedWriter = null;
    }

    /**
     * {@link ProcessManager}で実行中のコマンドラインに入力する
     *
     * @param line 入力する文字列
     * @throws IOException {@link ProcessManager}のコマンドラインが無効化されていた場合またはバックアップに不備があった場合
     */
    public void command(String line) throws IOException {
        switch (line) {
            case "BACKUP" -> {
                try {
                    processManager.backup(true, true);
                } catch (InterruptedException | NoSuchKeyException e) {
                    System.out.println(e.getMessage());
                }
            }
            case "START" -> processManager.start();
            case "STOP" -> {
                if (processManager.getServerStatus()) processManager.requestStopServer(0, "");
            }
            case "RESTART" -> {
                try {
                    processManager.restart();
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
            default -> {
                if (!processManager.getServerStatus()) return;
                processManager.log(line);
                bufferedWriter.write(line);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
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
        for (String name : playerList) {
            if (line.matches(".*<" + name + ">.*")) {
                return true;
            }
        }
        return false;
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

    protected void setWebhook(MSM4JProperty property) {
        webhookUrl = property.get("notification_webhook-url");
        serverStatusNotification = Boolean.parseBoolean(property.get("notification_server-status"));
        logInOutNotification = Boolean.parseBoolean(property.get("notification_log-in-out"));
        if (logInOutNotification) logInOutIndex = Integer.parseInt(property.get("notification_log-in-out-index"));
        notificationTimeFormat = property.get("notification_time-format");
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
            String name = line.split(" ")[logInOutIndex];
            playerList.add(name);
            new Webhook(webhookUrl, name + " LOG IN " + sdf.format(new Date()), Color.GREEN);
        } else if (line.contains("left the game")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            //プレイヤー名抜き出し
            String name = line.split(" ")[logInOutIndex];
            playerList.remove(name);
            new Webhook(webhookUrl, name + " LOG OUT " + sdf.format(new Date()), Color.RED);
        } else if (line.contains("Player connected")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            //プレイヤー名抜き出し
            String name = line.split(" ")[logInOutIndex].replace(",", "");
            playerList.add(name);
            new Webhook(webhookUrl, name + " LOG IN " + sdf.format(new Date()), Color.GREEN);
        } else if (line.contains("Player disconnected")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            //プレイヤー名抜き出し
            String name = line.split(" ")[logInOutIndex].replace(",", "");
            playerList.remove(name);
            new Webhook(webhookUrl, name + " LOG OUT " + sdf.format(new Date()), Color.RED);
        }
    }

    private void serverStatus(String line) {
        if (line.contains("]: Done") || line.endsWith("Server started.")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            new Webhook(webhookUrl, "SERVER START " + sdf.format(new Date()), Color.WHITE);
        } else if (line.contains("]: Stopping the server") || line.endsWith("Stopping server...")) {
            SimpleDateFormat sdf = new SimpleDateFormat(notificationTimeFormat);
            new Webhook(webhookUrl, "SERVER STOP " + sdf.format(new Date()), Color.BLACK);
        }
    }

}
