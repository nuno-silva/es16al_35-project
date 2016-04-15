package pt.tecnico.mydrive.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import pt.tecnico.mydrive.service.dto.FileDto;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.Directory;

public class ListDirectoryService extends MyDriveService {

    private long _token;
    private ArrayList<FileDto> _files = null;

    /** List the content of the current working directory */
    public ListDirectoryService(long token) {
        _token   = token;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(_token);

        String workingPath = s.getWorkingPath();
        File wf = fs.getFile(workingPath);
        if(wf.isCdAble()) {
            Directory workingDir = (Directory)wf;
            _files = new ArrayList<FileDto>();
            // add all Files in workingDir
            for( File f : workingDir.getFileSet()) {
                _files.add(new FileDto(f.getName(),
                                       f.getId(),
                                       f.getPermissions(),
                                       f.getLastMod(),
                                       getFileType(f) ));
            }
            // add '.'
            _files.add(new FileDto(".",
                                   workingDir.getId(),
                                   workingDir.getPermissions(),
                                   workingDir.getLastMod(),
                                   getFileType(workingDir) ));

            // add '..'
            Directory parent = workingDir.getParentDir();
            _files.add(new FileDto("..",
                                   parent.getId(),
                                   parent.getPermissions(),
                                   parent.getLastMod(),
                                   getFileType(parent) ));

            // sort files
            Collections.sort(_files);
        } else {
            // should never happen, but who knows?
            throw new IsNotCdAbleException("Working path '"+workingPath+"' is not a Directory!");
        }
    }

    protected List<FileDto> result() {
        return _files;
    }

    public static FileDto.FileType getFileType(File f) {
        // this is dirty. I'm sorry.
        // should I use the XML_TAG instead?
        switch(f.getClass().getName()) {
            case "pt.tecnico.mydrive.domain.Directory":
                return FileDto.FileType.DIRECTORY;
            case "pt.tecnico.mydrive.domain.PlainFile":
                return FileDto.FileType.PLAINFILE;
            case "pt.tecnico.mydrive.domain.App":
                return FileDto.FileType.APP;
            case "pt.tecnico.mydrive.domain.Link":
                return FileDto.FileType.LINK;
            default:
                return FileDto.FileType.FILE;
        }
    }
}
