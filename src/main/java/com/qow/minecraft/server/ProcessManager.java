package com.qow.minecraft.server;

import com.qow.util.Logger;
import com.qow.util.Property;
import com.qow.util.ThreadStopper;
import com.qow.util.Webhook;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Minecraftを実行する{@link ProcessBuilder}を管理する<br>
 * 取得するには{@link CommandRule#getProcessManager()}を使用する
 *
 * @version 2025/12/23
 * @since 1.0.0
 */
public class ProcessManager {
    private final ProcessBuilder pb;
    private final Property property;
    private final ThreadStopper stopper;
    private Process process;
    private CommandRule cr;
    private Logger log;
    private boolean startProcess;
    private boolean loggable;
    private boolean restart;

    protected ProcessManager(Property property, String[] exe) {
        this.property = property;
        process = null;
        cr = null;
        stopper = new ThreadStopper();

        String HOME_PATH = property.get("home-dir");
        pb = new ProcessBuilder(exe);
        pb.directory(new File(HOME_PATH));
        pb.redirectErrorStream(true);
    }

    /**
     * Minecraftを起動する
     *
     * @return 起動に成功した場合true
     */
    public synchronized boolean start() {
        if (startProcess) return false;
        startProcess = true;
        new Thread(() -> {
            stopper.setReady(false);
            try {
                loggable = Boolean.parseBoolean(property.get("log_enable"));
                if (loggable) {
                    String logPath = property.get("log_directory");

                    log = new Logger(logPath);
                    SimpleDateFormat sdf = new SimpleDateFormat(property.get("log_time-format"));
                    if (log.requestCreateLogFile(property.get("log_title") + "_" + sdf.format(new Date()) + property.get("log_extension"))) {
                        throw new RuntimeException();
                    }
                }

                if (Boolean.parseBoolean(property.get("notification_server-wave"))) {
                    try {
                        new Webhook(property.get("notification_webhook-url"), "TRY LAUNCH", Color.GRAY);
                    } catch (Exception ignored) {
                    }
                }
                cr.closeCommandStream();

                process = pb.start();

                InputStream is = process.getInputStream(); // プロセスの結果を変数に格納する
                BufferedReader br = new BufferedReader(new InputStreamReader(is)); // テキスト読み込みを行えるようにする

                cr.setCommandStream(new BufferedWriter(new OutputStreamWriter(process.getOutputStream())));

                restart = false;
                stopper.start();

                String line;
                while ((line = br.readLine()) != null) {
                    cr.commandsLine(line);
                    log(line);
                }
                startProcess = false;

                if (loggable) log.close();
                is.close();
                br.close();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
        return true;
    }

    /**
     * Minecraftを停止するリクエストをする
     *
     * @param seconds 停止までにかける時間 秒換算
     * @param comment 停止する前に表示する文字列 停止までにかける時間が0の場合表示されない
     * @throws IOException Minecraftの起動に失敗していた場合
     */
    public void requestStopServer(int seconds, String comment) throws IOException {
        if (0 < seconds) {
            cr.command("say " + comment);
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException ignored) {
            }
        }
        cr.command("stop");
    }

    /**
     * Minecraftを一度停止し再起動する<br>
     * Minecraftが起動していない場合でも起動する
     *
     * @throws IOException          サーバーの実行が不可能だった場合
     * @throws InterruptedException 予期せぬ割り込みが発生した場合
     */
    public void restart() throws IOException, InterruptedException {
        restart = true;
        boolean wasEnableServer = getServerStatus();
        if (wasEnableServer) {
            requestStopServer(0, "");
            process.waitFor();
        }
        start();
    }

    /**
     * Minecraftが実行中であるかを返す
     *
     * @return 実行中の場合true
     */
    public boolean getServerStatus() {
        return startProcess;
    }

    protected boolean isRestart() {
        return restart;
    }

    protected void connectCommandRule(CommandRule cr) {
        this.cr = cr;
        cr.setProcessManager(this);
    }

    protected void log(String line) throws IOException {
        if (loggable) log.writeLine(line);
    }

    protected void killProcess() {
        process.destroy();
    }

    protected synchronized void waitForProcessStart() {
        stopper.stop();
    }

    public final int waitForProcess() throws InterruptedException {
        waitForProcessStart();
        if (process == null) return -1;
        return process.waitFor();
    }

    protected void environment(String ldLibraryPath, String s) {
        pb.environment().put(ldLibraryPath, s);
    }
}
