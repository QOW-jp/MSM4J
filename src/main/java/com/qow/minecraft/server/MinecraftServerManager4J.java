package com.qow.minecraft.server;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Minecraftサーバーを管理する<br>
 * サーバーの起動から停止、再起動、バックアップ、ログが可能で{@link CommandRule#listeningCommandLine(String)}から実行結果を取得でき{@link CommandRule#command(String)}でコマンドラインからのコマンド実行も可能<br>
 * 手動でバックアップなどを取る場合は{@link ProcessManager}を使用する<br>
 * 基本的にqonファイル形式でconfigを管理しており、パスや通知の有無はqonファイルで設定する
 *
 * @version 2025/12/23
 * @since 1.0.0
 */
public class MinecraftServerManager4J {
    private final static String EXE_BE = "LD_LIBRARY_PATH";
    private final static String EXE_JE = "java";

    private final CommandControllerServer ccs;
    private final ProcessManager processManager;
    private final CommandRule commandRule;

    /**
     * プロパティに従いパス、エディションを設定する<br>
     * {@link CommandControllerServer}を初期化する
     *
     * @param property    プロパティ
     * @param commandRule 設定する{@link CommandRule}
     * @throws IOException               ファイルが存在しないまたはアクセス権がない場合
     * @throws MinecraftEditionException 非対応のエディションを選択した場合
     */
    public MinecraftServerManager4J(MSM4JProperty property, CommandRule commandRule) throws IOException, MinecraftEditionException {
        if (Boolean.parseBoolean(property.get("control_enable"))) {
            boolean autoPorting = Boolean.parseBoolean(property.get("control_auto-porting"));
            int port;
            if (autoPorting) {
                port = 0;
            } else {
                port = Integer.parseInt(property.get("control_port"));
            }
            int byteSize = Integer.parseInt(property.get("control_byte-size"));
            byte[] protocolID = property.get("control_protocol-id").getBytes(StandardCharsets.UTF_8);
            if (Boolean.parseBoolean(property.get("control_bind-ip"))) {
                String clientIP = property.get("control_client-ip");
                ccs = new CommandControllerServer(port, protocolID, byteSize, clientIP);
            } else {
                ccs = new CommandControllerServer(port, protocolID, byteSize);
            }
            ccs.setCommandRule(commandRule);

            if (autoPorting) {
                File temp = new File(property.get("control_port-temp"));
                Path parent = Path.of(temp.getParent());
                Files.createDirectories(parent);
                try (FileWriter fw = new FileWriter(temp)) {
                    try (PrintWriter pw = new PrintWriter(new BufferedWriter(fw))) {
                        pw.println(ccs.getLocalPort());
                    }
                }
            }
        } else {
            ccs = null;
        }

        String serverPath = property.get("server-path");
        Edition edition;
        try {
            edition = Edition.valueOf(property.get("edition"));
        } catch (IllegalArgumentException e) {
            throw new MinecraftEditionException(e.getMessage());
        }

        this.commandRule = commandRule;
        commandRule.setEdition(edition);
        commandRule.setWebhook(property);

        switch (edition) {
            case JAVA -> {
                String beforeArg = property.get("jvm-args_before");
                String[] beforeArgs = beforeArg.substring(1, beforeArg.length() - 1).split(",\\s*");
                String afterArg = property.get("jvm-args_after");
                String[] afterArgs = afterArg.substring(1, afterArg.length() - 1).split(",\\s*");

                String[] exe = new String[beforeArgs.length + afterArgs.length + 2];

                exe[0] = EXE_JE;
                System.arraycopy(beforeArgs, 0, exe, 1, beforeArgs.length);
                exe[beforeArgs.length] = "-jar";
                exe[beforeArgs.length + 1] = serverPath;
                for (int i = 0; i < afterArgs.length; i++) {
                    exe[beforeArgs.length + i + 2] = afterArgs[i];
                }
                processManager = new ProcessManager(property, exe);
                processManager.connectCommandRule(commandRule);
            }
            case BEDROCK -> {
                processManager = new ProcessManager(property, new String[]{serverPath});
                processManager.environment(EXE_BE, ".");
                processManager.connectCommandRule(commandRule);
            }
            case CMD -> {
                processManager = new ProcessManager(property, new String[]{serverPath});
                processManager.connectCommandRule(commandRule);
            }
            default -> throw new MinecraftEditionException("unsupported edition was picked.");
        }
    }

    /**
     * {@link ProcessManager#start()}を呼び出す
     *
     * @return 起動に成功した場合true
     */
    public boolean start() {
        return processManager.start();
    }

    /**
     * {@link ProcessManager#requestStopServer(int, String)}を呼び出す
     *
     * @param seconds 停止までにかける時間 秒換算
     * @param comment 停止する前に表示する文字列 停止までにかける時間が0の場合表示されない
     * @throws IOException Minecraftの起動に失敗していた場合
     */
    public void requestStopServer(int seconds, String comment) throws IOException {
        processManager.requestStopServer(seconds, comment);
    }

    /**
     * {@link CommandRule#command(String)}を呼び出す
     *
     * @param line 入力する文字列
     * @throws IOException {@link ProcessManager}のコマンドラインが無効化されていた場合またはバックアップに不備があった場合
     */
    public void command(String line) throws IOException {
        commandRule.command(line);
    }

    /**
     * {@link ProcessManager}の{@link Process}を強制的に終了する<br>
     */
    public void killProcess() {
        processManager.killProcess();
    }

    /**
     * {@link ProcessManager}のプログラムが終了するまで待機する
     *
     * @return {@link ProcessManager}の終了コード
     * @throws InterruptedException 予期せぬ割り込みが発生した場合
     */
    public synchronized int waitFor() throws InterruptedException {
        int exitCode;
        do {
            exitCode = processManager.waitForProcess();
        } while (processManager.isRestart());
        return exitCode;
    }

    /**
     * {@link CommandRule}に対応した{@link CommandControllerServer}を返す
     *
     * @return 対応したコマンド受信サーバー
     */
    public CommandControllerServer getCommandControllerServer() throws DisabledException {
        if (ccs == null) throw new DisabledException("config:controllable is false");
        return ccs;
    }
}
