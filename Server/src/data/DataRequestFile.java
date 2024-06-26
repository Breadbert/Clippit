package data;

/**
 * This class represents a request for a file, storing the file ID and the length of the requested data.
 */
public class DataRequestFile {

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Constructs a DataRequestFile object with the specified file ID and data length.
     * @param fileID the ID of the file.
     * @param length the length of the requested data in bytes.
     */
    public DataRequestFile(int fileID, long length) {
        this.fileID = fileID;
        this.length = length;
    }

    /**
     * Default constructor for DataRequestFile.
     */
    public DataRequestFile() {
    }

    private int fileID;
    private long length;
}
