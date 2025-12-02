package com.qow.minecraft.server;

import com.qow.util.Logger;
import com.qow.util.Property;
import com.qow.util.ThreadStopper;
import com.qow.util.Webhook;
import net.lingala.zip4j.ZipFile;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Minecraftを実行する{@link ProcessBuilder}を管理する<br>
 * 取得するには{@link CommandRule#getProcessManager()}を使用する
 *
 * @version 2025/12/02
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
            cr.command("say " + String.format(comment, seconds));
            try {
                Thread.sleep(seconds * 1000L);
            } catch (InterruptedException e) {
                cr.command("say <Error> failed");
            }
        }
        cr.command("stop");
    }

    /**
     * サーバーのバックアップを保存する<br>
     * 保存するファイル､保存先のディレクトリは{@link MinecraftServerManager4J#MinecraftServerManager4J(MSM4JProperty, CommandRule)}で指定されたconfigファイルが参照される
     *
     * @param delay   サーバー停止までの猶予を与える場合true
     * @param restart バックアップ前にサーバーが起動していてバックアップ後に起動する場合true
     * @throws IOException          Minecraftの起動に失敗していた場合
     * @throws InterruptedException 予期せぬ割り込みが発生した場合
     */
    public synchronized void backup(boolean delay, boolean restart) throws IOException, InterruptedException {
        if (!Boolean.parseBoolean(property.get("backup_enable"))) {
            System.err.println("backupable is false");
            return;
        }

        String backupPath = property.get("backup_directory");

        boolean wasEnableServer = getServerStatus();
        if (wasEnableServer) {
            requestStopServer(delay ? Integer.parseInt(property.get("backup_delay")) : 0, property.get("backup_comment"));
            process.waitFor();
        }

        String backupFilesPath = property.get("backup_backup-files-path");
        String[] backupFilePaths = backupFilesPath.substring(1, backupFilesPath.length() - 1).split(",\\s*");
        SimpleDateFormat sdf = new SimpleDateFormat(property.get("backup_time-format"));
        String archivedFileName = backupPath + "/" + property.get("backup_title") + "_" + sdf.format(new Date()) + property.get("backup_extension");

        //親ディレクトリ作成
        Path p = Paths.get(archivedFileName).getParent();
        Files.createDirectories(p);

        //Zipファイルを作成
        try (ZipFile zipFile = new ZipFile(archivedFileName)) {
            for (String backupFilePath : backupFilePaths) {
                File targetFile = new File(backupFilePath);
                if (targetFile.isDirectory()) {
                    zipFile.addFolder(targetFile);
                } else {
                    zipFile.addFile(targetFile);
                }
            }
        } catch (Exception e) {
            System.err.println("failed to archive.");
            System.out.println(e.getMessage());
        }

        if (restart && wasEnableServer) start();
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

    protected int waitFor() throws InterruptedException {
        waitForProcessStart();
        if (process == null) return -1;
        return process.waitFor();
    }

    protected void environment(String ldLibraryPath, String s) {
        pb.environment().put(ldLibraryPath, s);
    }
}
