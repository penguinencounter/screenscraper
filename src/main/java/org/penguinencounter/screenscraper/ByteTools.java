package org.penguinencounter.screenscraper;

import java.util.HashMap;

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

    public static final HashMap<Integer, Character> nybToHex = new HashMap<>();
    static {
        nybToHex.put(0, '0');
        nybToHex.put(1, '1');
        nybToHex.put(2, '2');
        nybToHex.put(3, '3');
        nybToHex.put(4, '4');
        nybToHex.put(5, '5');
        nybToHex.put(6, '6');
        nybToHex.put(7, '7');
        nybToHex.put(8, '8');
        nybToHex.put(9, '9');
        nybToHex.put(10, 'a');
        nybToHex.put(11, 'b');
        nybToHex.put(12, 'c');
        nybToHex.put(13, 'd');
        nybToHex.put(14, 'e');
        nybToHex.put(15, 'f');
    }
}
