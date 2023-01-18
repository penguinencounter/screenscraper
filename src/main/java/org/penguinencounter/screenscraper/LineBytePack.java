package org.penguinencounter.screenscraper;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

public record LineBytePack(List<byte[]> data) {

    public static LineBytePack import_char(int w, int h, ByteBuf data) {
        //noinspection GrazieInspection
        data.skipBytes(10); // int int bool 2*nybble
        List<byte[]> builder1 = new ArrayList<>();
        List<Byte> builder2 = new ArrayList<>();
        try {
            for (int line = 0; line < h; line++) {
                builder2.clear();
                for (int column = 0; column < w; column++) {
                    builder2.add(data.readByte());
                }
                byte[] bytes = new byte[builder2.size()];
                for (int i = 0; i < builder2.size(); i++) {
                    bytes[i] = builder2.get(i);
                }
                builder1.add(bytes);
                data.skipBytes(w); // ignore coloring
            }
        } catch (IndexOutOfBoundsException e) {
            ScreenScraperClient.LOG_MAIN.error("Crash while importing monitor data");
            ScreenScraperClient.LOG_MAIN.error("Progress so far: " + builder1.size() + " lines " + builder2.size() + " col");
            throw e;
        }
        return new LineBytePack(builder1);
    }

    public static LineBytePack import_col_bg(int w, int h, ByteBuf data) {
        //noinspection GrazieInspection
        data.skipBytes(10); // int int bool 2*nybble
        List<byte[]> builder1 = new ArrayList<>();
        List<Byte> builder2 = new ArrayList<>();
        try {
            for (int line = 0; line < h; line++) {
                builder2.clear();
                data.skipBytes(w); // ignore characters
                for (int column = 0; column < w; column++) {
                    builder2.add((byte) ByteTools.nybToHex.get(ByteTools.nybbles(data.readByte())[0]).charValue());
                }
                byte[] bytes = new byte[builder2.size()];
                for (int i = 0; i < builder2.size(); i++) {
                    bytes[i] = builder2.get(i);
                }
                builder1.add(bytes);
            }
        } catch (IndexOutOfBoundsException e) {
            ScreenScraperClient.LOG_MAIN.error("Crash while importing monitor color bg data");
            ScreenScraperClient.LOG_MAIN.error("Progress so far: " + builder1.size() + " lines " + builder2.size() + " col");
            throw e;
        }
        return new LineBytePack(builder1);
    }

    public static LineBytePack import_col_fg(int w, int h, ByteBuf data) {
        //noinspection GrazieInspection
        data.skipBytes(10); // int int bool 2*nybble
        List<byte[]> builder1 = new ArrayList<>();
        List<Byte> builder2 = new ArrayList<>();
        try {
            for (int line = 0; line < h; line++) {
                builder2.clear();
                data.skipBytes(w); // ignore characters
                for (int column = 0; column < w; column++) {
                    builder2.add((byte) ByteTools.nybToHex.get(ByteTools.nybbles(data.readByte())[1]).charValue());
                }
                byte[] bytes = new byte[builder2.size()];
                for (int i = 0; i < builder2.size(); i++) {
                    bytes[i] = builder2.get(i);
                }
                builder1.add(bytes);
            }
        } catch (IndexOutOfBoundsException e) {
            ScreenScraperClient.LOG_MAIN.error("Crash while importing monitor color bg data");
            ScreenScraperClient.LOG_MAIN.error("Progress so far: " + builder1.size() + " lines " + builder2.size() + " col");
            throw e;
        }
        return new LineBytePack(builder1);
    }
}
