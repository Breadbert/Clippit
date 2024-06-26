package data;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;

/**
 * DataWriter provides methods to write data to a specified file.
 * It allows writing data in chunks and keeps track of the file size and write progress.
 * Class used for DataFileServer functionality.
 */
public class DataWriter {

    private File file;
    private long fileSize;
    private RandomAccessFile accFile;
    
    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public RandomAccessFile getAccFile() {
        return accFile;
    }

    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    /**
     * Constructor to initialize the DataWriter with a file and its size.
     *
     * @param file     the file to write data to
     * @param fileSize the total size of the file
     * @throws IOException if an I/O error occurs
     */
    public DataWriter(File file, long fileSize) throws IOException {
        accFile = new RandomAccessFile(file, "rw");                             // Both read and write privileges have been granted.
        this.file = file;
        this.fileSize = fileSize;
    }
    
    /**
     * Writes the specified byte array to the file at the current position.
     * 
     * @param data the byte array to write file data toS
     * @return the new length of the file after writing the data
     * @throws IOException if an I/O error occurs
     */
    public synchronized long writeFile(byte[] data) throws IOException {
        accFile.seek(accFile.length());
        accFile.write(data);
        return accFile.length();
    }

    public void close() throws IOException {
        accFile.close();
    }

    public String getMaxFileSize() {
        return convertFile(fileSize);
    }

    public String getCurrentFileSize() throws IOException {
        return convertFile(accFile.length());
    }

    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.length();
        percentage = filePointer * 100 / fileSize;
        return percentage;
    }

    public long getFileLength() throws IOException {
        return accFile.length();
    }
    
    /**
     * Converts a file size in bytes to a more legible format.
     *
     * @param bytes the size in bytes
     * @return the size in a readable format with appropriate units
     */
    private String convertFile(double bytes) {
        String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String sizeToReturn;
        DecimalFormat df = new DecimalFormat("0.#");
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;                                               // EZ byte-to-next-unit conversion
        }
        sizeToReturn = df.format(bytes) + " " + fileSizeUnits[index];
        return sizeToReturn;
    }
}
