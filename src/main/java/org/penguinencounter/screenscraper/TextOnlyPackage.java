package org.penguinencounter.screenscraper;

import io.netty.buffer.ByteBuf;

public record TextOnlyPackage(String data) {

    public static TextOnlyPackage import_(int w, int h, ByteBuf data) {
        ScreenScraperClient.LOG_MAIN.info("Import " + data.readableBytes() + " mon bytes");
        //noinspection GrazieInspection
        data.skipBytes(10); // int int bool 2*nybble
        StringBuilder builder = new StringBuilder();
        try {
            for (int line = 0; line < h; line++) {
                for (int column = 0; column < w; column++) {
                    builder.append((char) data.readByte());
                }
                data.skipBytes(w); // ignore coloring
                builder.append("\n");
            }
        } catch (IndexOutOfBoundsException e) {
            ScreenScraperClient.LOG_MAIN.error("Crash while importing monitor data");
            ScreenScraperClient.LOG_MAIN.error("Progress so far: " + builder);
            throw e;
        }
        return new TextOnlyPackage(builder.toString());
    }
}
