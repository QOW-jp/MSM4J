# MSM4J (MinecraftServerManager4J)

### Requirements

Java 17 or later

## Getting started

| 項目      | 詳細               |
|---------|------------------|
| OS      | Ubuntu 22.04 LTS |
| Edition | Java             |

#### pom.xml

```xml

<dependencies>
    <dependency>
        <groupId>com.qow</groupId>
        <artifactId>qon4j</artifactId>
        <version>1.0.1</version>
    </dependency>
    <dependency>
        <groupId>net.lingala.zip4j</groupId>
        <artifactId>zip4j</artifactId>
        <version>2.11.5</version>
    </dependency>
</dependencies>
```

#### cmd

```
1. java ServerTest [qonPath]
2. java ClientTest [qonPath]

※[jsonPath] = msm4jconfig.qon
args[0] = qonPath
```

#### ServerTest.java

```java
import com.qow.minecraft.server.CommandControllerServer;
import com.qow.minecraft.server.CommandRule;
import com.qow.minecraft.server.MinecraftEditionException;
import com.qow.minecraft.server.MinecraftServerManager4J;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws InterruptedException, IOException, MinecraftEditionException, UntrustedQONException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        CommandRule commandRule = new CommandRule();
        MinecraftServerManager4J msManager = new MinecraftServerManager4J(new File(path), commandRule);
        Runtime.getRuntime().addShutdownHook(new Thread(msManager::killProcess));

        CommandControllerServer ccs = msManager.getCommandControllerServer();

        System.out.println("start MSM4J : " + msManager.start());
        System.out.println("start CommandControllerServer : " + ccs.start());

        int exitCode = msManager.waitFor();
        ccs.stop();
        System.out.println("server exit code " + exitCode);
    }
}
```

#### ClientTest.java

```java
import com.qow.minecraft.server.CommandControllerClient;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException, UntrustedQONException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        CommandControllerClient ccc = new CommandControllerClient(new File(path));

        Scanner sc = new Scanner(System.in);

//        while (true) {
//            System.out.print(">>");
//            String cmd = sc.nextLine();
//            if (cmd.equals("STOP")) break;
//            System.out.println(cmd + " : " + ccc.command(cmd));
//        }

        System.out.print(">>");
        String cmd = sc.nextLine();
        System.out.println(cmd + " : " + ccc.command(cmd));

        sc.close();
    }
}
```

#### msm4jconfig.qon

```qon
#Minecraft Edition [java,bedrock,cmd]
edition=java
#Home path
home-directory=/home/user/Desktop/minecraft/java/java-server
#Enable relative paths for server path
server-path-relative=true
#Server path
server-path=/server.jar
#Log config
log{
#Enable log
loggable=true
#log title
title=log
#Extension
extension=.log
#Time formats that conform to DateTimeFormatter
time-format=yyyy年MM月dd日HH時mm分ss秒
#Enable relative paths for log directory
log-directory-relative=true
#Log directory
log-directory=/run_log
}
#Backup config
backup{
#Enable backup
backupable=true
#stop server delay
delay=10
#Notification comment
comment=This server will stop for backup after %d seconds.
#Backup file title
title=MC_SERVER_BK
#Extension
extension=.zip
#Time formats that conform to DateTimeFormatter
time-format=yyyy年MM月dd日HH時mm分ss秒
#Enable relative paths for backup directory
backup-directory-relative=true
#Backup directory
backup-directory=/server-backups
#Enable relative paths for backup target files path
backup-files-path-relative=true
#Backup target files path
backup-files-path[
/world
/server.properties
/whitelist.json
/usercache.json
]
}
#JVM config
jvm-args{
#JVM argument
before[
-Xmx4G
-XX=+UnlockExperimentalVMOptions
-XX=+UseZGC
-XX=ZUncommitDelay=50
-XX=+AlwaysPreTouch
]
#Java program argument
after[
nogui
]
}
#Notification config
notification{
#Webhook url
webhook-url=https//discord.com/api/webhooks/
#Enable notifications when attempting to start up/quit
server-wave=true
#Enable notifications when startup/quit is successful
server-status=true
#Enable notification when log in/out
log-in-out=true
#player name index
log-in-out-index=3
#Time formats that conform to DateTimeFormatter
time-format=HH:mm:ss
}
#Control config
control{
#Enable control
controllable=true
#Enable bind ip
bind-ip=true
#server ip
server-ip=localhost
#client ip (Only when bind-ip=true)
client-ip=localhost
#server port
port=9999
#Communication protocol
byte-size=1024
}
```