package pt.tecnico.mydrive.service.dto;

import org.joda.time.DateTime;


public class FileDto implements Comparable<FileDto> {
    public enum FileType {
        DIRECTORY,
        PLAINFILE, LINK, APP, FILE
    }

    private String name;
    private long id;
    private byte permissions;
    private DateTime lastMod;
    private FileType type;

    public FileDto(String name, long id, byte permissions, DateTime lastMod, FileType type) {
        this.name = name;
        this.id   = id;
        this.permissions = permissions;
        this.lastMod = lastMod;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public byte getPermissions() {
        return permissions;
    }

    public DateTime getLastMod() {
        return lastMod;
    }

    public FileType getType() {
        return type;
    }

    @Override
    public int compareTo(FileDto other) {
        return getName().compareTo(other.getName());
    }

}
