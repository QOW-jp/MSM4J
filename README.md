# MSM4J (MinecraftServerManager4Java)

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
        <version>1.3.1</version>
    </dependency>
    <dependency>
        <groupId>com.qow</groupId>
        <artifactId>qtcp</artifactId>
        <version>1.2.0</version>
    </dependency>
    <dependency>
        <groupId>com.qow</groupId>
        <artifactId>util</artifactId>
        <version>1.0.3</version>
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
import com.qow.minecraft.server.*;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) throws InterruptedException, IOException, MinecraftEditionException, UntrustedQONException, NoSuchKeyException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        String path = args[0];

        MSM4JProperty property = new MSM4JProperty(new QONObject(new File(path)));
        CommandRule commandRule = new CommandRule();
        MinecraftSM4J msManager = new MinecraftSM4J(property, commandRule);
        Runtime.getRuntime().addShutdownHook(new Thread(msManager::killProcess));

        System.out.println("start MSM4J : " + msManager.start());

        try {
            CommandControllerServer ccs = msManager.getCommandControllerServer();

            System.out.println("start CommandControllerServer : " + ccs.start());

            int exitCode = msManager.waitFor();
            ccs.stop();
            System.out.println("server exit code " + exitCode);
        } catch (DisabledException e) {
            int exitCode = msManager.waitFor();
            System.out.println("server exit code " + exitCode);
        }
    }
}
```

#### ClientTest.java
```java
import com.qow.minecraft.server.CommandControllerClient;
import com.qow.net.ClosedServerException;
import com.qow.net.UntrustedConnectException;
import com.qow.util.qon.NoSuchKeyException;
import com.qow.util.qon.QONObject;
import com.qow.util.qon.UntrustedQONException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class ClientTest {
    public static void main(String[] args) throws IOException, UntrustedQONException, UntrustedConnectException, ClosedServerException, NoSuchKeyException {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }

        String path = args[0];
        QONObject qonObject = new QONObject(new File(path));
        QONObject control = qonObject.getQONObject("control");
        String hostname = control.get("server-ip");
        byte[] protocolID = control.get("protocol-id").getBytes(StandardCharsets.UTF_8);
        int byteSize = Integer.parseInt(control.get("byte-size"));

        int port;
        boolean autoPorting = Boolean.parseBoolean(control.get("auto"));
        if (autoPorting) {
            Path portTempPath = Paths.get(control.get("port-temp"));
            List<String> lines = Files.readAllLines(portTempPath, StandardCharsets.UTF_8);
            port = Integer.parseInt(lines.get(0));
        } else {
            port = Integer.parseInt(control.get("port"));
        }

        CommandControllerClient ccc = new CommandControllerClient(hostname, port, protocolID, byteSize);

        Scanner sc = new Scanner(System.in);

        System.out.print(">>");
        String cmd = sc.nextLine();
        System.out.println(cmd + " : " + ccc.command(cmd));

//        while (true) {
//            System.out.print(">>");
//            String cmd = sc.nextLine();
//            if (cmd.equals("STOP")) break;
//            System.out.println(cmd + " : " + ccc.command(cmd));
//        }

        sc.close();
    }
}
```

#### msm4jconfig.qon

```qon
#qon file 4 msm4j config v1.7.0
#Requirements qon4j v1.1.0 or later
#Minecraft Edition [java,bedrock,cmd]
edition=java
#Home path
home-dir=/home/user/Desktop/minecraft/java/default
#Server path
server-path=$(home-dir)/server.jar
#Log config
log{
    #Enable log
    enable=true
    #log title
    title=log
    #Time formats that conform to DateTimeFormatter
    time-format=yyyy年MM月dd日HH時mm分ss秒
    #Extension
    extension=.log
    #Log directory
    directory=$(home-dir)/run_log
}
#JVM config
jvm-args{
    #JVM argument
    before[
        -Xmx8G
        -XX:+UnlockExperimentalVMOptions
        -XX:+UseZGC
        -XX:ZUncommitDelay=50
        -XX:+AlwaysPreTouch
    ]
    #Java program argument
    after[
        nogui
    ]
}
#Notification config
notification{
    #Webhook url
    webhook-url=https//discord.com/api/webhooks/???
    #Enable notifications when attempting to start up/quit
    server-wave=false
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
    enable=true
    #Enable bind IP
    bind-ip=true
    #Server IP
    server-ip=localhost
    #Client IP (Only when bind-ip=true)
    client-ip=localhost
    #Server port
    port=9999
    #Enable auto port
    auto=true
    #Server port temp file path
    port-temp=$(home-dir)/msm4j_port.temp
    #Communication protocol
    byte-size=1024
    #Protocol ID
    protocol-id=msm4j@$(home-dir)
}
```