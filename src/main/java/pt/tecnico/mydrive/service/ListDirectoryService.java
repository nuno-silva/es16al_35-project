package pt.tecnico.mydrive.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import pt.tecnico.mydrive.service.dto.FileDto;
import pt.tecnico.mydrive.exception.IsNotCdAbleException;
import pt.tecnico.mydrive.exception.PermissionDeniedException;

import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.domain.Session;
import pt.tecnico.mydrive.domain.User;
import pt.tecnico.mydrive.domain.File;
import pt.tecnico.mydrive.domain.PlainFile;
import pt.tecnico.mydrive.domain.App;
import pt.tecnico.mydrive.domain.Link;
import pt.tecnico.mydrive.domain.Directory;
import pt.tecnico.mydrive.domain.SpecialDirectory;

public class ListDirectoryService extends MyDriveService {

    private long _token;
    private ArrayList<FileDto> _files = null;
    private String _path;

    /** List the content of the current working directory */
    public ListDirectoryService(long token) {
        _token = token;
        _path = "";
    }

    public ListDirectoryService(long token, String path) {
        _token = token;
        _path = path;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        Session s = fs.getSession(_token);
        User    u = s.getUser();

        File wf;
        if(_path.equals("") || _path.equals(".")) {
            wf = s.getWorkDir();
        } else if( FileSystem.PathHelper.isAbsolute(_path) ){
            wf = fs.getFile(_path, u);
        } else {
            Directory workingDir = s.getWorkDir();
            wf = workingDir.getFile(_path, u);
        }

        _files = new ArrayList<FileDto>();
        // add all Files in workingDir // should this be done in domain?
        for( File f : wf.getFileSet(u)) {
            _files.add(new FileDto(f.getName(),
                                   f.getId(),
                                   f.getPermissions(),
                                   f.getLastMod(),
                                   getFileType(f) ));
        }
        // add '.'
        _files.add(new FileDto(".",
                               wf.getId(),
                               wf.getPermissions(),
                               wf.getLastMod(),
                               getFileType(wf) ));

        // add '..'
        Directory parent = wf.getParentDir();
        _files.add(new FileDto("..",
                               parent.getId(),
                               parent.getPermissions(),
                               parent.getLastMod(),
                               getFileType(parent) ));

        // sort files
        Collections.sort(_files);
    }

    public List<FileDto> result() {
        assertExecuted();
        return _files;
    }

    public static FileDto.FileType getFileType(File f) {
        // this is dirty. I'm sorry.
        // should I use the XML_TAG instead?
        switch(f.getClass().getName()) {
            case "pt.tecnico.mydrive.domain.Directory":
            case "pt.tecnico.mydrive.domain.SpecialDirectory":
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
