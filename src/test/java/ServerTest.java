import com.qow.minecraft.server.*;
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
