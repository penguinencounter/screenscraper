package org.penguinencounter.screenscraper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class ScreenScraperClient implements ClientModInitializer {
    public static final Logger LOG_MAIN = LoggerFactory.getLogger("ScreenScraper");
    public static ScreenScraperClient INSTANCE;

    public static Map<Byte, String> charRemap = new HashMap<>();

    /**
     * matches SemVer (MAJOR.MINOR.PATCH+extra?)
     * but isn't a development version (contains dev in the 'extra' part)
     *
     * @param version the version string to check
     * @return true if the version is a release version
     */
    private boolean versionIsOk(String version) {
        return version.matches("^[0-9]+\\.[0-9]+\\.[0-9]+(?!.*dev.*)(?:\\+.*)?$");
    }

    // Hi admins!

    @Override
    public void onInitializeClient() {
        LOG_MAIN.info("ScreenScraper loaded");
        INSTANCE = this;
        FabricLoader.getInstance().getModContainer("screenscraper").ifPresent(modContainer -> {
            String ver = modContainer.getMetadata().getVersion().getFriendlyString();
            boolean okForPublicUse = versionIsOk(ver);
            boolean isDev = FabricLoader.getInstance().isDevelopmentEnvironment();
            if (!okForPublicUse) {
                LOG_MAIN.error("This version of ScreenScraper is not intended (or approved) for public use!");
                LOG_MAIN.error("Please use a release version instead to avoid bans/warnings.");
                LOG_MAIN.error("Stable releases at https://github.com/penguinencounter/screenscraper/releases");
                LOG_MAIN.error("This mod's version is: " + ver);
            }
            if (!(okForPublicUse || isDev)) {
                throw new RuntimeException("Bad ScreenScraper version. Check logs for more details.");
            }
            if (isDev) {
                LOG_MAIN.warn("Looks like a development environment. I guess you can continue.");
            }
        });

        // Load up the mapping file from assets/screenscraper/charmap

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(
                new SimpleSynchronousResourceReloadListener() {
                    @Override
                    public Identifier getFabricId() {
                        return new Identifier("screenscraper", "charmaploader");
                    }

                    @Override
                    public void reload(ResourceManager manager) {
                        charRemap.clear();
                        manager.findResources("screenscraper", path -> path.getPath().endsWith("charmap"))
                                .forEach((identifier, resource) -> {
                                    charRemap.clear();
                                    String errorExtra = " (" + identifier + " in " + resource.getResourcePackName() + ")";
                                    try (InputStream stream = resource.getInputStream()) {
                                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                                        String line;
                                        int i = 0;
                                        HashMap<Byte, String> newMap = new HashMap<>();
                                        while ((line = reader.readLine()) != null) {
                                            if (line.startsWith("##")) {
                                                continue;
                                            }
                                            line = line.strip();
                                            if (line.equals("$space")) {
                                                line = " ";
                                            } else if (line.startsWith("\\u")) {
                                                line = new String(Character.toChars(Integer.parseInt(line.substring(2), 16)));
                                            }
                                            newMap.put((byte) i++, line);
                                        }
                                        if (i != 256) {
                                            throw new RuntimeException("Invalid charmap file: " + i + " lines (want 256)" + errorExtra);
                                        }
                                        LOG_MAIN.info("Loaded charmap" + errorExtra + " with " + newMap.size() + " entries");
                                        newMap.putIfAbsent((byte) '\n', "\n");
                                        charRemap = newMap;
                                    } catch (IOException e) {
                                        LOG_MAIN.error("Failed to load charmap!!" + errorExtra, e);
                                    }
                                });
                    }
                }
        );
    }

    /**
     * "Do you really need this much null safety?"
     * Saves a file with the data package.
     * Name is determined by current game conditions.
     *
     * @param pos         position of the monitor
     * @param dataPackage contents of the monitor
     * @return was the operation successful?
     */
    public boolean saveFile(BlockPos pos, int monW, int monH,
                            LineBytePack dataPackage, LineBytePack backgroundPackage, LineBytePack foregroundPackage,
                            ColorPallete pallete) {
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
        if (client.getGame().getCurrentSession().isRemoteServer()) {
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
        try (OutputStreamWriter fw =
                     new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
            fw.write("ss v2\n");
            fw.write("monitor size " + monW + " " + monH + "\n");
            for (byte[] bs : dataPackage.data()) {
                for (byte b : bs) {
                    fw.write(charRemap.getOrDefault(b, "�"));  // � is intentional
                }
                fw.write("\n");
            }
            for (byte[] bs : backgroundPackage.data()) {
                for (byte b : bs) {
                    fw.write(charRemap.getOrDefault(b, "�"));  // � is intentional
                }
                fw.write("\n");
            }
            for (byte[] bs : foregroundPackage.data()) {
                for (byte b : bs) {
                    fw.write(charRemap.getOrDefault(b, "�"));  // � is intentional
                }
                fw.write("\n");
            }
            fw.write("pallete:\n");
            int i = 0;
            for (int color : pallete.colors()) {
                fw.write(String.format("%s %06x\n", ByteTools.nybToHex.get(i), color));
                i++;
            }
        } catch (IOException e) {
            LOG_MAIN.error("Failed to write file", e);
            return false;
        }
        return true;
    }
}
