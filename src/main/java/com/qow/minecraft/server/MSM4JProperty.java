package com.qow.minecraft.server;

import com.qow.util.Property;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class MSM4JProperty extends Property {
    public MSM4JProperty(File qonFile) throws UntrustedQONException, IOException {
        this();
        QONObject qonObject = new QONObject(qonFile);

    }

    public MSM4JProperty() {
        super();

        addTargetKey("edition");
        addTargetKey("home-dir");
        addTargetKey("server-path");
        addTargetKey("log_loggable");
        addTargetKey("log_title");
        addTargetKey("log_extension");
        addTargetKey("log_time-format");
        addTargetKey("log_dir");
        addTargetKey("backup_backupable");
        addTargetKey("backup_delay");
        addTargetKey("backup_comment");
        addTargetKey("backup_title");
        addTargetKey("backup_extension");
        addTargetKey("backup_time-format");
        addTargetKey("backup_backup-dir");
        addTargetKey("backup_backup-files-path");
        addTargetKey("jvm-args_before");
        addTargetKey("jvm-args_after");
        addTargetKey("notification_webhook-url");
        addTargetKey("notification_server-wave");
        addTargetKey("notification_server-status");
        addTargetKey("notification_log-in-out");
        addTargetKey("notification_log-in-out-index");
        addTargetKey("notification_time-format");
        addTargetKey("control_controllable");
        addTargetKey("control_bind-ip");
        addTargetKey("control_server-ip");
        addTargetKey("control_client-ip");
        addTargetKey("control_port");
        addTargetKey("control_byte-size");
        addTargetKey("control_protocol-id");
    }

    public void setEdition(String edition) {
        putMap("edition", edition);
    }
}
