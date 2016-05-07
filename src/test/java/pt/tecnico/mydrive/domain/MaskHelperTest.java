package pt.tecnico.mydrive.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests related to {@link MaskHelper}
 */
public class MaskHelperTest {

    @Test
    public void stringToByteSuccess() {
        final String v1_str = "00000001";
        final String v2_str = "10000001";
        final String v3_str = "00000000";
        final String v4_str = "11111111";
        final String v5_str = "00101001";

        final byte v1_byte = (byte)0b00000001;
        final byte v2_byte = (byte)0b10000001;
        final byte v3_byte = (byte)0b00000000;
        final byte v4_byte = (byte)0b11111111;
        final byte v5_byte = (byte)0b00101001;

        Assert.assertEquals(MaskHelper.getByteMask(v1_str), v1_byte);
        Assert.assertEquals(MaskHelper.getByteMask(v2_str), v2_byte);
        Assert.assertEquals(MaskHelper.getByteMask(v3_str), v3_byte);
        Assert.assertEquals(MaskHelper.getByteMask(v4_str), v4_byte);
        Assert.assertEquals(MaskHelper.getByteMask(v5_str), v5_byte);
    }

    @Test (expected=NumberFormatException.class)
    public void stringToByteFail() {
        final String val = "100000000"; // 256
        MaskHelper.getByteMask(val);

    }
}
