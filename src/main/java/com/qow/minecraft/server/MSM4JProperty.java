package com.qow.minecraft.server;

import com.qow.util.Property;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;

import java.util.Arrays;

/**
 * MSM4Jのプロパティ
 *
 * @version 2026/02/17
 * @since 1.5.3
 */
public class MSM4JProperty extends Property {
    /**
     * qonファイルに保存されているプロパティに従って設定する
     *
     * @param qon qonファイル
     * @throws NoSuchKeyException qonファイルに不備があった場合
     */
    public MSM4JProperty(QONObject qon) throws NoSuchKeyException {
        this();

        putMap("edition", qon.get("edition").toUpperCase());
        putMap("home-dir", qon.get("home-dir"));
        putMap("server-path", qon.get("server-path"));

        QONObject log = qon.getQONObject("log");
        putMap("log_enable", log.get("enable"));
        putMap("log_title", log.get("title"));
        putMap("log_time-format", log.get("time-format"));
        putMap("log_extension", log.get("extension"));
        putMap("log_directory", log.get("directory"));

        QONObject jvm = qon.getQONObject("jvm-args");
        putMap("jvm-args_before", Arrays.toString(jvm.getQONArray("before").list()));
        putMap("jvm-args_after", Arrays.toString(jvm.getQONArray("after").list()));

        QONObject notification = qon.getQONObject("notification");
        putMap("notification_webhook-url", notification.get("webhook-url"));
        putMap("notification_server-wave", notification.get("server-wave"));
        putMap("notification_server-status", notification.get("server-status"));
        putMap("notification_log-in-out", notification.get("log-in-out"));
        putMap("notification_log-in-out-index", notification.get("log-in-out-index"));
        putMap("notification_time-format", notification.get("time-format"));

        QONObject control = qon.getQONObject("control");
        putMap("control_enable", control.get("enable"));
        putMap("control_bind-ip", control.get("bind-ip"));
        putMap("control_server-ip", control.get("server-ip"));
        putMap("control_client-ip", control.get("client-ip"));
        putMap("control_port", control.get("port"));
        putMap("control_auto-porting", control.get("auto-porting"));
        putMap("control_port-temp", control.get("port-temp"));
        putMap("control_byte-size", control.get("byte-size"));
        putMap("control_protocol-id", control.get("protocol-id"));
    }

    /**
     * 規定のプロパティを定義する
     */
    public MSM4JProperty() {
        super();

        addTargetKey("edition");
        addTargetKey("home-dir");
        addTargetKey("server-path");
        addTargetKey("log_enable");
        addTargetKey("log_title");
        addTargetKey("log_time-format");
        addTargetKey("log_extension");
        addTargetKey("log_directory");
        addTargetKey("jvm-args_before");
        addTargetKey("jvm-args_after");
        addTargetKey("notification_webhook-url");
        addTargetKey("notification_server-wave");
        addTargetKey("notification_server-status");
        addTargetKey("notification_log-in-out");
        addTargetKey("notification_log-in-out-index");
        addTargetKey("notification_time-format");
        addTargetKey("control_enable");
        addTargetKey("control_bind-ip");
        addTargetKey("control_server-ip");
        addTargetKey("control_client-ip");
        addTargetKey("control_port");
        addTargetKey("control_auto-porting");
        addTargetKey("control_port-temp");
        addTargetKey("control_byte-size");
        addTargetKey("control_protocol-id");
    }

    /**
     * エディションの設定をする。
     *
     * @param edition エディション
     */
    public void setEdition(String edition) {
        putMap("edition", edition.toUpperCase());
    }

    /**
     * ホームディレクトリの設定をする。
     *
     * @param directory ホームディレクトリ
     */
    public void setHomeDirectory(String directory) {
        putMap("home-dir", directory);
    }

    /**
     * 実行ファイルのパスを設定する。
     *
     * @param path サーバー実行ファイルのパス
     */
    public void setServerPath(String path) {
        putMap("server-path", path);
    }

    /**
     * ログに関する設定をする。
     *
     * @param enable     有効化
     * @param title      ログファイル名
     * @param timeFormat ログファイルの時間形式
     * @param extension  ログファイル拡張子
     * @param directory  ログファイルを保存するディレクトリ
     */
    public void setLog(boolean enable, String title, String timeFormat, String extension, String directory) {
        putMap("log_enable", String.valueOf(enable));
        putMap("log_title", title);
        putMap("log_time-format", timeFormat);
        putMap("log_extension", extension);
        putMap("log_directory", directory);
    }

    /**
     * バックアップに関する設定をする。
     *
     * @param enable     有効化
     * @param title      バックアップファイル名
     * @param timeFormat バックアップファイルの時間形式
     * @param extension  バックアップファイル拡張子
     * @param directory  バックアップファイルを保存するディレクトリ
     * @param delay      バックアップを実行するまでの猶予時間
     * @param comment    バックアップ予告メッセージ
     * @param filesPath  バックアップ対象のパス
     */
    public void setBackup(boolean enable, String title, String timeFormat, String extension, String directory, int delay, String comment, String[] filesPath) {
        putMap("backup_enable", String.valueOf(enable));
        putMap("backup_title", title);
        putMap("backup_time-format", timeFormat);
        putMap("backup_extension", extension);
        putMap("backup_directory", directory);
        putMap("backup_delay", String.valueOf(delay));
        putMap("backup_comment", comment);
        putMap("backup_backup-files-path", Arrays.toString(filesPath));
    }

    /**
     * サーバーのJVM引数とサーバーの引数を設定する。
     *
     * @param before JVM引数
     * @param after  サーバー引数
     */
    public void setJVMArgs(String[] before, String[] after) {
        putMap("jvm-args_before", Arrays.toString(before));
        putMap("jvm-args_after", Arrays.toString(after));
    }

    /**
     * 通知に関する設定をする。
     *
     * @param webhookURL ウェブフックURL
     * @param wave       サーバー起動宣言の有効化
     * @param status     サーバー起動時の有効化
     * @param logInOut   ログインアウト時の有効化
     * @param timeFormat 時間形式
     * @param index      プレイヤー名のインデックス
     */
    public void setNotification(String webhookURL, boolean wave, boolean status, boolean logInOut, String timeFormat, int index) {
        putMap("notification_webhook-url", webhookURL);
        putMap("notification_server-wave", String.valueOf(wave));
        putMap("notification_server-status", String.valueOf(status));
        putMap("notification_log-in-out", String.valueOf(logInOut));
        putMap("notification_log-in-out-index", String.valueOf(index));
        putMap("notification_time-format", timeFormat);
    }

    /**
     * MSM4Jの通信によるサーバー操作の設定をする。
     * ポート番号固定。
     *
     * @param enable     有効化
     * @param bind       アクセス可能なIPアドレスを制限する
     * @param serverIP   サーバーIPアドレス
     * @param clientIP   クライアントIPアドレス
     * @param port       ポート番号
     * @param byteSize   通信サイズ
     * @param protocolID 識別ID
     */
    public void setControl(boolean enable, boolean bind, String serverIP, String clientIP, int port, int byteSize, String protocolID) {
        putMap("control_enable", String.valueOf(enable));
        putMap("control_bind-ip", String.valueOf(bind));
        putMap("control_server-ip", serverIP);
        putMap("control_client-ip", clientIP);
        putMap("control_port", String.valueOf(port));
        putMap("control_auto-porting", "false");
        putMap("control_port-temp", null);
        putMap("control_byte-size", String.valueOf(byteSize));
        putMap("control_protocol-id", protocolID);
    }

    /**
     * MSM4Jの通信によるサーバー操作の設定をする。
     * ポート番号は自動で割り当てられる。
     *
     * @param enable     有効化
     * @param bind       アクセス可能なIPアドレスを制限する
     * @param serverIP   サーバーIPアドレス
     * @param clientIP   クライアントIPアドレス
     * @param tempPath   ポート番号を一時的に保管するファイルパス
     * @param byteSize   通信サイズ
     * @param protocolID 識別ID
     */
    public void setControl(boolean enable, boolean bind, String serverIP, String clientIP, String tempPath, int byteSize, String protocolID) {
        putMap("control_enable", String.valueOf(enable));
        putMap("control_bind-ip", String.valueOf(bind));
        putMap("control_server-ip", serverIP);
        putMap("control_client-ip", clientIP);
        putMap("control_port", "0");
        putMap("control_auto", "true");
        putMap("control_port-temp", tempPath);
        putMap("control_byte-size", String.valueOf(byteSize));
        putMap("control_protocol-id", protocolID);
    }
}
