# MSM4J (MinecraftServerManager4J)

### Requirements

Java 21 or later

## Getting started

| 項目      | 詳細               |
|---------|------------------|
| OS      | Ubuntu 22.04 LTS |
| Edition | Java             |

```
1. java ServerTest [jsonPath]
2. java ClientTest [jsonPath]

※[jsonPath] = msm4jconfig.json
args[0] = jsonPath
```

#### ServerTest.java

```java
import com.qow.minecraft.server.CommandControllerServer;
import com.qow.minecraft.server.CommandRule;
import com.qow.minecraft.server.MinecraftEditionException;
import com.qow.minecraft.server.MinecraftServerManager4J;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws InterruptedException, IOException, MinecraftEditionException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        CommandRule commandRule = new CommandRule();
        MinecraftServerManager4J msManager = new MinecraftServerManager4J(path, commandRule);
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

import java.io.IOException;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        CommandControllerClient ccc = new CommandControllerClient(path);

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

#### msm4jconfig.json

```json
{
  "edition": "java",
  "home-directory": "/home/user/Desktop/minecraft/java/java-server",
  "server-path-relative": true,
  "server-path": "/server.jar",
  "log": {
    "loggable": true,
    "title": "log",
    "extension": ".log",
    "time-format": "yyyy年MM月dd日HH時mm分ss秒",
    "log-directory-relative": true,
    "log-directory": "/run_log"
  },
  "backup": {
    "backupable": true,
    "delay": 10,
    "comment": "This server will stop for backup after %d seconds.",
    "title": "MC_SERVER_BK",
    "extension": ".zip",
    "time-format": "yyyy年MM月dd日HH時mm分ss秒",
    "backup-directory-relative": true,
    "backup-directory": "/server-backups",
    "backup-files-path-relative": true,
    "backup-files-path": [
      "/world",
      "/server.properties",
      "/whitelist.json",
      "/usercache.json"
    ]
  },
  "jvm-args": {
    "before": [
      "-Xmx4G",
      "-XX:+UnlockExperimentalVMOptions",
      "-XX:+UseZGC",
      "-XX:ZUncommitDelay=50",
      "-XX:+AlwaysPreTouch"
    ],
    "after": [
      "nogui"
    ]
  },
  "notification": {
    "webhook-url": "https://discord.com/api/webhooks/",
    "server-wave": true,
    "server-status": true,
    "log-in-out": true,
    "time-format": "HH:mm:ss"
  },
  "control": {
    "controllable": true,
    "bind-ip": true,
    "server-ip": "localhost",
    "client-ip": "localhost",
    "port": 9999,
    "byte-size": 1024
  }
}
```
