package pt.tecnico.mydrive.service.dto;

import org.joda.time.DateTime;

public class FileDto implements Comparable<FileDto> {
    public enum FileType {
        DIRECTORY,
        PLAINFILE, LINK, APP
    }

    private String name;
    private long id;
    private byte permissions;
    private DateTime lastMod;
    private FileType type;

    public FileDto(String name) {
        /* TODO */
    }

    @Override
    public int compareTo(FileDto other) {
        /* TODO */
        return 0;
    }
}
