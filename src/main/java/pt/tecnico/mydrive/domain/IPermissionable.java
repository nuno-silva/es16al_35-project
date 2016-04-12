package pt.tecnico.mydrive.domain;

/**
 * Defines the behaviour of classes that have "permissions" or "mask".
 */
public interface IPermissionable {
    public String getStringPermissions();

    public String getStringMask();

    public byte getByteMask();

    public String getANDedStringPermissions(IPermissionable other);

    public String getANDedStringMask(IPermissionable other);

    public byte getANDedByteMask(IPermissionable other);
}
