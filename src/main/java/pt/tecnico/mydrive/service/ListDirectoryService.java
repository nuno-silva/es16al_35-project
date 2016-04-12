package pt.tecnico.mydrive.service;

import java.util.List;
import pt.tecnico.mydrive.service.dto.FileDto;

import pt.tecnico.mydrive.domain.FileSystem;

public class ListDirectoryService extends MyDriveService {

    private long _token;

    /** List the content of the current working directory */
    public ListDirectoryService(long token) {
        _token   = token;
    }

    @Override
    protected void dispatch() {
        /* TODO */
    }

    protected List<FileDto> result() {
        /* TODO */
        return null;
    }
}
