package com.qow.minecraft.server;

import com.qow.util.Property;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;

import java.util.Arrays;

/**
 * MSM4Jのプロパティ
 *
 * @version 2025/10/15
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

        QONObject backup = qon.getQONObject("backup");
        putMap("backup_enable", backup.get("enable"));
        putMap("backup_title", backup.get("title"));
        putMap("backup_time-format", backup.get("time-format"));
        putMap("backup_extension", backup.get("extension"));
        putMap("backup_directory", backup.get("directory"));
        putMap("backup_delay", backup.get("delay"));
        putMap("backup_comment", backup.get("comment"));
        putMap("backup_backup-files-path", Arrays.toString(backup.getQONArray("backup-files-path").list()));

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
        addTargetKey("backup_enable");
        addTargetKey("backup_title");
        addTargetKey("backup_time-format");
        addTargetKey("backup_extension");
        addTargetKey("backup_directory");
        addTargetKey("backup_delay");
        addTargetKey("backup_comment");
        addTargetKey("backup_backup-files-path");
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
        addTargetKey("control_byte-size");
        addTargetKey("control_protocol-id");
    }

    public void setEdition(String edition) {
        putMap("edition", edition.toUpperCase());
    }

    public void setHomeDirectory(String directory) {
        putMap("home-dir", directory);
    }

    public void setServerPath(String path) {
        putMap("server-path", path);
    }

    public void setLog(boolean enable, String title, String timeFormat, String extension, String directory) {
        putMap("log_enable", String.valueOf(enable));
        putMap("log_title", title);
        putMap("log_time-format", timeFormat);
        putMap("log_extension", extension);
        putMap("log_directory", directory);
    }

    public void setBackup(boolean enable, String title, String timeFormat, String extension, String directory, int delay, String comment, String[] filesPath) {
        putMap("backup_enable", String.valueOf(enable));
        putMap("backup_title", title);
        putMap("backup_time-format", timeFormat);
        putMap("backup_extension", extension);
        putMap("backup_directory", directory);
        putMap("backup_delay", String.valueOf(delay));
        putMap("backup_comment", comment);
    }

    public void setJVMArgs(String[] before, String[] after) {
        putMap("jvm-args_before", Arrays.toString(before));
        putMap("jvm-args_after", Arrays.toString(after));
    }

    public void setNotification(String webhookURL, boolean wave, boolean status, boolean logInOut, String timeFormat, int index) {
        putMap("notification_webhook-url", webhookURL);
        putMap("notification_server-wave", String.valueOf(wave));
        putMap("notification_server-status", String.valueOf(status));
        putMap("notification_log-in-out", String.valueOf(logInOut));
        putMap("notification_log-in-out-index", String.valueOf(index));
        putMap("notification_time-format", timeFormat);
    }

    public void setControl(boolean enable, boolean bind, String serverIP, String clientIP, int port, int byteSize, String protocolID) {
        putMap("control_enable", String.valueOf(enable));
        putMap("control_bind-ip", String.valueOf(bind));
        putMap("control_server-ip", serverIP);
        putMap("control_client-ip", clientIP);
        putMap("control_port", String.valueOf(port));
        putMap("control_byte-size", String.valueOf(byteSize));
        putMap("control_protocol-id", protocolID);
    }
}
