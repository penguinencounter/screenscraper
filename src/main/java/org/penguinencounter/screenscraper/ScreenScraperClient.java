package org.penguinencounter.screenscraper;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(net.fabricmc.api.EnvType.CLIENT)
public class ScreenScraperClient implements ClientModInitializer {
    public static final Logger LOG_TESTING = LoggerFactory.getLogger("ScreenScraper Testing");
    public static final Logger LOG_MAIN = LoggerFactory.getLogger("ScreenScraper");

    @Override
    public void onInitializeClient() {
        LOG_TESTING.info("testing logger OK");
        LOG_MAIN.info("main logger OK");
    }
}
