package data;

import java.io.File;

/**
 * This class represents a data file on the server, storing various attributes of the file
 * such as file ID, name, size, and output path.
 */
public class DataFileServer {

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public long getFileSizeLength() {
        return fileSizeLength;
    }

    public void setFileSizeLength(long fileSizeLength) {
        this.fileSizeLength = fileSizeLength;
    }

    public File getOutPutPath() {
        return outPutPath;
    }

    public void setOutPutPath(File outPutPath) {
        this.outPutPath = outPutPath;
    }

    /**
     * Constructs a DataFileServer object with the specified details.
     * @param fileID the ID of the file.
     * @param fileName the name of the file.
     * @param fileSize the size of the file as a string.
     * @param fileSizeLength the size of the file in bytes.
     * @param outPutPath the output path where the file is stored.
     */
    public DataFileServer(int fileID, String fileName, String fileSize, long fileSizeLength, File outPutPath) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileSizeLength = fileSizeLength;
        this.outPutPath = outPutPath;
    }

    /**
     * Default constructor for DataFileServer.
     */
    public DataFileServer() {
    }

    private int fileID;
    private String fileName;
    private String fileSize;
    private long fileSizeLength;
    private File outPutPath;
}
