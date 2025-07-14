import com.qow.minecraft.server.CommandControllerServer;
import com.qow.minecraft.server.CommandRule;
import com.qow.minecraft.server.MinecraftEditionException;
import com.qow.minecraft.server.MinecraftServerManager4J;

import java.io.IOException;

public class ServerTest {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("args.length is not 1");
            System.exit(2);
        }
        CommandRule commandRule = new CommandRule();
        MinecraftServerManager4J msManager;
        try {
            msManager = new MinecraftServerManager4J(args[0], commandRule);
        } catch (IOException | MinecraftEditionException e) {
            throw new RuntimeException(e);
        }

        CommandControllerServer ccs = msManager.getCommandControllerServer();

        Runtime.getRuntime().addShutdownHook(new Thread(msManager::killProcess));

        try {
            Thread.sleep(1000);
            System.out.println("start MSM4J : " + msManager.start());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("start CommandControllerServer : " + ccs.start());

        try {
            int exitCode = msManager.waitFor();
            ccs.stop();
            System.out.println("server exit code " + exitCode);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
