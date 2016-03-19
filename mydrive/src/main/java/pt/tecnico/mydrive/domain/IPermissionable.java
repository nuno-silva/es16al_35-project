package pt.tecnico.mydrive.domain;

/**
 * Defines the behaviour of classes that have "permissions" or "mask".
 */
public interface IPermissionable {
    public String getStringPermissions();
    public String getStringMask();
    public byte getBytePermissions();
    public String getANDedPermissions(IPermissionable other);
    public String getANDedMask(IPermissionable other);
}
