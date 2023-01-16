package org.penguinencounter.screenscraper;

public class ByteTools {

    /**
     * Return the two "nybbles" in a byte
     * A nybble is 4 bits, or half a byte.
     * @param b input byte
     * @return int[2] two nybbles, high then low
     */
    public static int[] nybbles(byte b) {
        return new int[]{(b & 0xf0) >>> 4, b & 0x0f};
    }
}
