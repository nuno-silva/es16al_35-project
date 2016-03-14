package pt.tecnico.mydrive.domain;

// TODO: refractor, rename class
public class PathHelper {
    /**
     * Returns a Sting representation of the byte umask.
     *
     * @return String representation of the byte umask
     */
    public static String getStringUmask(byte umask) {
        return Integer.toBinaryString(umask & 0xFF);
    }
}
