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

        // BEWARE XXX This code doesn't allow listing a single file
        // FIXME: This needs refactor asap
        String workingPath="";
        File wf;
        if(_path.trim()=="" || _path.trim()=="."){
          workingPath=s.getWorkDir().getFullPath();
          wf = fs.getFile(workingPath);
        }
        else if(_path.trim()==".."){
          Directory d = s.getWorkDir();
          String workPath=d.getFullPath();
          workingPath="Parent of "+workPath;
          wf=d.getParentDir();
        }
        else{
          Directory workingDir = s.getWorkDir();
          wf=(File)workingDir;
        }


        if(!wf.isCdAble()) {
            // should never happen, but who knows?
            throw new IsNotCdAbleException("Working path '"+workingPath+"' is not a Directory!");
        }

        else {
            Directory workingDir = (Directory)wf;
            _files = new ArrayList<FileDto>();
            // add all Files in workingDir
            for( File f : workingDir.getFileSet(u)) {
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
        }
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
