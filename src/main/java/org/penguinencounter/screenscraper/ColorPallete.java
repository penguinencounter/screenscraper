package org.penguinencounter.screenscraper;

import io.netty.buffer.ByteBuf;

public record ColorPallete(int[] colors) {
    public static ColorPallete import_pallete(int w, int h, ByteBuf data) {
        ScreenScraperClient.LOG_MAIN.info("Import " + data.readableBytes() + " pallete bytes");
        //noinspection GrazieInspection
        data.skipBytes(10); // int int bool 2*nybble
        data.skipBytes(w * h * 2); // ignore characters & colors
        int[] builder = new int[16];
        for (int i = 0; i < 16; i++) {
            int r = data.readByte() & 0xff;
            int g = data.readByte() & 0xff;
            int b = data.readByte() & 0xff;
            // Yes it's reversed. No I don't know why.
            builder[15 - i] = r << 16 | g << 8 | b;
        }
        return new ColorPallete(builder);
    }
}
