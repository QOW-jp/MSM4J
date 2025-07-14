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
