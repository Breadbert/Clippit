package data;

/**
 * This class represents the data being sent of a file, including the file ID, data, and whether the transfer is complete.
 */
public class DataFileSending {

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
    
    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    /**
     * Constructs a DataFileSending object with the specified file ID, data, and completion status.
     * @param fileID the ID of the file.
     * @param data the data being sent.
     * @param finish the completion status of the transfer.
     */
    public DataFileSending(int fileID, byte[] data, boolean finish) {
        this.fileID = fileID;
        this.data = data;
        this.finish = finish;
    }

    /**
     * Default constructor for DataFileSending.
     */
    public DataFileSending() {
    }

    private int fileID;
    private byte[] data;
    private boolean finish;
}
