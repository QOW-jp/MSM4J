package com.qow.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 入力された文字列をファイルに保存するロガー
 *
 * @version 2025/07/14
 * @since 1.0.0
 */
public class Logger {
    private final String logHomePath;
    private BufferedWriter bufferedWriter;

    /**
     * @param logHomePath ログを保存するディレクトリ
     */
    public Logger(String logHomePath) {
        this.logHomePath = logHomePath;
    }

    /**
     * ログファイルを作成する<br>
     * ログファイルまでのディレクトリが存在しない場合は自動的に作成される
     *
     * @param name ログファイルの名前
     * @return ログファイルの作成が成功した場合true
     * @throws IOException 何らかの要因でファイルの作成ができなかった場合
     */
    public boolean requestCreateLogFile(String name) throws IOException {
        Path p = Paths.get(logHomePath);
        Files.createDirectories(p);

        File file = new File(logHomePath + "/" + name);
        bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        return file.createNewFile();
    }

    /**
     * ログファイルに文字列を追加する
     *
     * @param line 追加する文字列
     * @throws IOException ファイルに書き込みができなかった場合
     */
    public void writeLine(String line) throws IOException {
        bufferedWriter.write(line);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    /**
     * ログファイルの書き込みを終了する
     *
     * @throws IOException 正常に終了できなかった場合
     */
    public void close() throws IOException {
        bufferedWriter.close();
    }
}
