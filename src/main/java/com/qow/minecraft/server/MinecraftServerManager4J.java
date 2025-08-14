package com.qow.minecraft.server;

import com.qow.util.JsonReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Minecraftサーバーを管理する<br>
 * サーバーの起動から停止、再起動、バックアップ、ログが可能で{@link CommandRule#commandLine(String)}から実行結果を取得でき{@link CommandRule#command(String)}でコマンドラインからのコマンド実行も可能<br>
 * 手動でバックアップなどを取る場合は{@link ProcessManager}を使用する<br>
 * 基本的にjsonファイル形式でconfigを管理しており、パスや通知の有無はjsonファイルで設定する
 *
 * @version 2025/08/14
 * @since 1.0.0
 */
public class MinecraftServerManager4J {
    private final static String EXE_BE = "LD_LIBRARY_PATH= ";
    private final static String EXE_JE = "java";

    private final CommandControllerServer ccs;
    private final ProcessManager processManager;
    private final CommandRule commandRule;

    /**
     * configに従いパス、エディションを設定する<br>
     * {@link CommandControllerServer}を初期化する
     *
     * @param jsonPath    configファイルのパス
     * @param commandRule 設定する{@link CommandRule}
     * @throws IOException               ファイルが存在しないまたはアクセス権がない場合
     * @throws MinecraftEditionException 非対応のエディションを選択した場合
     */
    public MinecraftServerManager4J(String jsonPath, CommandRule commandRule) throws IOException, MinecraftEditionException {
        ccs = new CommandControllerServer(jsonPath);
        ccs.setCommandRule(commandRule);

        JsonReader jsonReader = new JsonReader(jsonPath);
        String HOME_PATH = jsonReader.getJSONObject().getString("home-directory");

        JSONObject json = jsonReader.getJSONObject();

        String serverPath = JsonReader.getAbsolutePath(HOME_PATH, json, "server-path");
        String edition = json.getString("edition");

        this.commandRule = commandRule;
        commandRule.setWebhook(jsonReader.getJSONObject("notification"));

        if (edition.equals("java")) {
            JSONObject jvmArgs = jsonReader.getJSONObject("jvm-args");
            JSONArray beforeArgs = jvmArgs.getJSONArray("before");
            JSONArray afterArgs = jvmArgs.getJSONArray("after");

            String[] exe = new String[beforeArgs.length() + afterArgs.length() + 2];

            exe[0] = EXE_JE;
            for (int i = 0; i < beforeArgs.length(); i++) {
                exe[i + 1] = beforeArgs.getString(i);
            }
            exe[beforeArgs.length()] = "-jar";
            exe[beforeArgs.length() + 1] = serverPath;
            for (int i = 0; i < afterArgs.length(); i++) {
                exe[beforeArgs.length() + i + 2] = afterArgs.getString(i);
            }
            processManager = new ProcessManager(jsonReader, exe);
            processManager.connectCommandRule(commandRule);
        } else if (edition.equals("bedrock")) {
            processManager = new ProcessManager(jsonReader, new String[]{EXE_BE + serverPath});
            processManager.connectCommandRule(commandRule);
        } else if (edition.equals("cmd")) {
            processManager = new ProcessManager(jsonReader, new String[]{serverPath});
            processManager.connectCommandRule(commandRule);
        } else {
            throw new MinecraftEditionException("unsupported edition was picked.");
        }
    }

    /**
     * {@link ProcessManager#start()}を呼び出す
     *
     * @return 起動に成功した場合true
     */
    public boolean start() throws IOException {
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
            exitCode = processManager.waitFor();
        } while (processManager.isRestart());
        return exitCode;
    }

    /**
     * {@link CommandRule}に対応した{@link CommandControllerServer}を返す
     *
     * @return 対応したコマンド受信サーバー
     */
    public CommandControllerServer getCommandControllerServer() {
        return ccs;
    }
}
