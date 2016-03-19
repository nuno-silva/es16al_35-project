package pt.tecnico.mydrive.domain;

/**
 * Static helper class containing mask helper methods.
 */
public class MaskHelper {
    private static final String HR_MASK_READ = "r";
    private static final String HR_MASK_WRITE = "w";
    private static final String HR_MASK_EXECUTE = "x";
    private static final String HR_MASK_NONE = "-";
    private static final String DEFAULT_HR_MASK = HR_MASK_NONE + HR_MASK_NONE + HR_MASK_NONE;

    private MaskHelper() { } // prevent instantiation

    /**
     * Returns the {@link java.lang.String} representation of the binary mask.
     * This method assumes that the argument has a length of 6. If smaller size is used,
     * the default "------" value is returned. In the binary string representation, any other character
     * but '1' results in '-', while '1' can result in either 'r', 'w' or 'x', depending on its position
     * in the string.
     *
     * @return the {@link java.lang.String} representation of the binary mask
     */
    public static String getStringMask(byte mask) {
        return String.format("%6s", Integer.toBinaryString(mask).replace(' ', '0'));
    }

    /**
     * Returns the {@link java.lang.String} human-readable representation of the binary mask.
     * For example, the byte mask 111001 will result in rwx--x.
     *
     * @return the {@link java.lang.String} human-readable representation of the binary mask
     */
    public static String getStringPermissions(byte mask) {
        String strMask = getStringMask(mask);

        if (strMask.length() < 6) {
            return DEFAULT_HR_MASK + DEFAULT_HR_MASK;
        }

        return maskBitsToHR(strMask.substring(0, 3)) + maskBitsToHR(strMask.substring(3, 6));

    }

    /**
     * Converts three bit {@link java.lang.String} representation to a human-readable format.
     * For example 101 will be converted to r-x. This method only considers the first 3 characters
     * of the {@link java.lang.String}. If less than 3 characters are provided, the default "---" value
     * is returned. If, within a string, the character is not '1', the '-' character will be used for that
     * conversion. For example, "1$a" as an argument will return "r--", while "Still Dre" as an argument
     * will return "---".
     *
     * @return the {@link java.lang.String} human-readable representation of the provided {@link java.lang.String}
     *  representation of the binary mask.
     */
    private static String maskBitsToHR(String bits) {
        if (bits.length() < 3) {
            return DEFAULT_HR_MASK;
        }
        String result = "";
        char[] chars = bits.toCharArray();

        result += (chars[0] == '1') ? HR_MASK_READ : HR_MASK_NONE;
        result += (chars[1] == '1') ? HR_MASK_WRITE : HR_MASK_NONE;
        result += (chars[2] == '1') ? HR_MASK_EXECUTE : HR_MASK_NONE;

        return result;
    }
}
