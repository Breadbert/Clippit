package data;

/**
 * This class initializes file data, storing the file name and file size.
 */
public class DataInitFile {

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Constructs a DataInitFile object with the specified file name and size.
     * @param fileName the name of the file.
     * @param fileSize the size of the file in bytes.
     */
    public DataInitFile(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    /**
     * Default constructor for DataInitFile.
     */
    public DataInitFile() {
    }

    private String fileName;
    private long fileSize;
}
