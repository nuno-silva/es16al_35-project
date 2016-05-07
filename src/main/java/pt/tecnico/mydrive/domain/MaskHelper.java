package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.presentation.Sys;

/**
 * Static helper class containing mask helper methods.
 */
public class MaskHelper {
    public final static byte OWNER_READ_MASK = (byte) 0b10000000;
    public final static byte OWNER_WRITE_MASK = (byte) 0b01000000;
    public final static byte OWNER_EXEC_MASK = (byte) 0b00100000;
    public final static byte OWNER_DELETE_MASK = (byte) 0b00010000;
    public final static byte OTHER_READ_MASK = (byte) 0b00001000;
    public final static byte OTHER_WRITE_MASK = (byte) 0b00000100;
    public final static byte OTHER_EXEC_MASK = (byte) 0b00000010;
    public final static byte OTHER_DELETE_MASK = (byte) 0b00000001;
    private static final String HR_MASK_READ = "r";
    private static final String HR_MASK_WRITE = "w";
    private static final String HR_MASK_EXECUTE = "x";
    private static final String HR_MASK_DELETE = "d";
    private static final String HR_MASK_NONE = "-";
    private static final String DEFAULT_HR_MASK = HR_MASK_NONE + HR_MASK_NONE + HR_MASK_NONE + HR_MASK_NONE;

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 255;

    private MaskHelper() {
    } // prevent instantiation

    /**
     * Returns the {@link java.lang.String} representation of the binary mask.
     * This method assumes that the argument has a length of 8. If smaller size is used,
     * the default "--------" value is returned. In the binary string representation, any other character
     * but '1' results in '-', while '1' can result in either 'r', 'w', 'x' or 'd', depending on its position
     * in the string.
     *
     * @return the {@link java.lang.String} representation of the binary mask
     */
    public static String getStringMask(byte mask) {
        return String.format("%8s", Integer.toBinaryString(mask & 0xFF)).replace(' ', '0');
    }

    /**
     * Returns the {@link java.lang.String} human-readable representation of the binary mask.
     * For example, the byte mask 11110101 will result in rwxd-w-d.
     *
     * @return the {@link java.lang.String} human-readable representation of the binary mask
     */
    public static String getStringPermissions(byte mask) {
        String strMask = getStringMask(mask);

        if (strMask.length() < 8) {
            return DEFAULT_HR_MASK + DEFAULT_HR_MASK;
        }

        return maskBitsToHR(strMask.substring(0, 4)) + maskBitsToHR(strMask.substring(4, 8));

    }

    /**
     * Converts three bit {@link java.lang.String} representation to a human-readable format.
     * For example 1011 will be converted to r-xd. This method only considers the first 4 characters
     * of the {@link java.lang.String}. If less than 4 characters are provided, the default "----" value
     * is returned. If, within a string, the character is not '1', the '-' character will be used for that
     * conversion. For example, "1$ab" as an argument will return "r---", while "Still Dre" as an argument
     * will return "----".
     *
     * @return the {@link java.lang.String} human-readable representation of the provided {@link java.lang.String}
     * representation of the binary mask.
     */
    private static String maskBitsToHR(String bits) {
        if (bits.length() < 4) {
            return DEFAULT_HR_MASK;
        }
        String result = "";
        char[] chars = bits.toCharArray();

        result += (chars[0] == '1') ? HR_MASK_READ : HR_MASK_NONE;
        result += (chars[1] == '1') ? HR_MASK_WRITE : HR_MASK_NONE;
        result += (chars[2] == '1') ? HR_MASK_EXECUTE : HR_MASK_NONE;
        result += (chars[3] == '1') ? HR_MASK_DELETE : HR_MASK_NONE;

        return result;
    }

    public static byte andMasks(byte mask1, byte mask2) {
        return (byte) (mask1 & mask2);
    }

    public static byte getByteMask(String mask) throws NumberFormatException {
        if (mask.length() > 8) {
            throw new NumberFormatException( "Value out of range. Value:\"" + mask + " Mask can't have more" +
                    "that 8 characters \" Radix:2");
        }
        int i = Integer.parseInt(mask, 2);
        return (byte)i;
    }
}
