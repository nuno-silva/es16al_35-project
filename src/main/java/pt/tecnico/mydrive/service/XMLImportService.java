package pt.tecnico.mydrive.service;

import org.jdom2.JDOMException;
import pt.tecnico.mydrive.domain.FileSystem;
import pt.tecnico.mydrive.exception.FileNotFoundException;
import pt.tecnico.mydrive.exception.XMLFormatException;

import java.io.IOException;

public class XMLImportService extends MyDriveService {
    private final String fileName;

    public XMLImportService(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void dispatch() {
        FileSystem fs = FileSystem.getInstance();
        try {
            fs.xmlImportFromFile(fileName);
        } catch (JDOMException e) {
            throw new XMLFormatException(e.getMessage());
        } catch (IOException e) {
            throw new FileNotFoundException(fileName + " not found");
        }
    }
}
