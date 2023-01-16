package org.penguinencounter.screenscraper;

import com.mojang.bridge.launcher.SessionEventListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class ScreenScraperClient implements ClientModInitializer {
    public static final Logger LOG_MAIN = LoggerFactory.getLogger("ScreenScraper");
    public static ScreenScraperClient INSTANCE;

    // Hi admins!

    @Override
    public void onInitializeClient() {
        LOG_MAIN.info("ok");
        INSTANCE = this;
    }

    public static ScreenScraperClient getInstance() {
        return INSTANCE;
    }

    /**
     * "Do you really need this much null safety?"
     * Saves a file with the data package.
     * Name is determined by current game conditions.
     * @param pos position of the monitor
     * @param dataPackage contents of the monitor
     * @return was the operation successful?
     */
    public boolean saveFile(BlockPos pos, TextOnlyPackage dataPackage) {
        Path file = FabricLoader.getInstance().getGameDir();
        file = file.resolve("monitors");
        //noinspection ResultOfMethodCallIgnored
        file.toFile().mkdirs();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return false;
        if (client.player == null) return false;
        String dim = client.player.world.getRegistryKey().getValue().toString();
        String wrld;
        if (client.getGame().getCurrentSession() == null) return false;
        if (client.getGame().getCurrentSession().isRemoteServer()){
            if (client.getCurrentServerEntry() == null) return false;
            wrld = client.getCurrentServerEntry().address;
        } else {
            if (client.getServer() == null) return false;
            // Thank you, reddit!
            // https://www.reddit.com/r/fabricmc/comments/rj2r0z/comment/hrrr8ld/
            wrld = client.getServer().getSavePath(WorldSavePath.ROOT).getParent().getFileName().toString();
        }
        // sanitize
        dim = dim.replaceAll("[^a-zA-Z0-9]", "_");
        wrld = wrld.replaceAll("[^a-zA-Z0-9]", "_");

        file = file.resolve(wrld + "_in_" + dim + "_at_" + pos.getX() + "_" + pos.getY() + "_" + pos.getZ() + ".txt");
        File f = file.toFile();
        // Write the file
        try (FileWriter fw = new FileWriter(f)) {
            fw.write(dataPackage.getData());
        } catch (IOException e) {
            LOG_MAIN.error("Failed to write file", e);
            return false;
        }
        return true;
    }
}
