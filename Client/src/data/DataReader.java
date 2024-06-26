package data;

import io.socket.client.Ack;
import io.socket.client.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import javax.swing.JTable;
import org.json.JSONException;
import org.json.JSONObject;
import swing.PanelStatus;

/**
 * DataReader contains methods to read data from a specified file.
 * It integrates with a socket client to manage file transfers and updates the status
 * in a UI component.
 */
public class DataReader {

    private int fileID;
    private File file;
    private long fileSize;
    private String fileName;
    private RandomAccessFile accFile;
    private PanelStatus status;
    private JTable table;
    private Socket client;
    
    public PanelStatus getStatus() {
        return status;
    }

    public int getFileID() {
        return fileID;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public RandomAccessFile getAccFile() {
        return accFile;
    }

    public void setAccFile(RandomAccessFile accFile) {
        this.accFile = accFile;
    }

    
    /**
     * Constructor to initialize the DataReader with a file and a JTable for status display.
     *
     * @param file  the file to be read
     * @param table the JTable to display status
     * @throws IOException if an I/O error occurs
     */
    public DataReader(File file, JTable table) throws IOException {
        accFile = new RandomAccessFile(file, "r");                              // Opening in read-only
        this.file = file;
        this.fileSize = accFile.length();
        this.fileName = file.getName();
        this.status = new PanelStatus();
        this.status.addEvent(new ActionListener() {                             // Event listener to handle pause/resume functionality
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!status.isPause() && pause) {
                    pause = false;
                    client.emit("req_file_length", fileID, new Ack() {          // Request current file length from server by file id
                        @Override
                        public void call(Object... os) {
                            if (os.length > 0) {
                                long length = Long.valueOf(os[0].toString());
                                try {
                                    accFile.seek(length);                       // Move the file pointer to the resumed position
                                    sendingFile(client);                        // Continue sending the file
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                }
            }
        });
        this.table = table;
    }


    
    /**
     * Reads the next chunk of data from the file.
     *
     * @return a byte array containing the data read, or null if the end of the file is reached
     * @throws IOException if an I/O error occurs
     */
    public synchronized byte[] readFile() throws IOException {
        long filePointer = accFile.getFilePointer();
        if (filePointer != fileSize) {                                          //  2000 is max send file per package
            int max = 2000;                                                     // Split it to send a large file
            long length = filePointer + max >= fileSize ? fileSize - filePointer : max;
            byte[] data = new byte[(int) length];                               
            accFile.read(data);
            return data;
        } else {
            return null;
        }
    }
    
    /**
     * Closes the RandomAccessFile.
     *
     * @throws IOException if an I/O error occurs
     */
    public void close() throws IOException {
        accFile.close();
    }
    
    /**
     * Converts the file size to a much more readable format, units included.
     *
     * @return the file size in a readable format with appropriate units
     */
    public String getFileSizeConverted() {
        double bytes = fileSize;
        String[] fileSizeUnits = {"bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"};
        String sizeToReturn;
        DecimalFormat df = new DecimalFormat("0.#");
        int index;
        for (index = 0; index < fileSizeUnits.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        sizeToReturn = df.format(bytes) + " " + fileSizeUnits[index];
        return sizeToReturn;
    }

    /**
     * Calculates the percentage of the file that has been read.
     *
     * @return the percentage of the file read
     * @throws IOException if an I/O error occurs
     */
    public double getPercentage() throws IOException {
        double percentage;
        long filePointer = accFile.getFilePointer();
        percentage = filePointer * 100 / fileSize;
        return percentage;
    }
    
    /**
     * Prepares a row for the JTable with file information.
     *
     * @param no the row number
     * @return an array of objects representing the table row
     */
    public Object[] toRowTable(int no) {
        return new Object[]{this, no, fileName, getFileSizeConverted(), "Next update"};
    }
    
    /**
     * Starts sending the file over the socket connection.
     *
     * @param socket the socket to use for sending the file
     * @throws JSONException if a JSON error occurs
     */
    public void startSend(Socket socket) throws JSONException {
        this.client = socket;
        JSONObject data = new JSONObject();
        data.put("fileName", fileName);
        data.put("fileSize", fileSize);
        
        socket.emit("send_file", data, new Ack() {                              // Emit the request to send the file
            @Override
            public void call(Object... os) {                                    // Index 0 Boolean, Index 1 FileID
                if (os.length > 0) {
                    boolean action = (boolean) os[0];
                    if (action) {
                        fileID = (int) os[1];                                   // Server generates a fileID and returns it
                        try {
                            sendingFile(socket);                                // Finally file sending may start
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private boolean pause = false;

    /**
     * Recursively sends chunks of the file until the entire file is sent.
     *
     * @param socket the socket to use for sending the file
     * @throws IOException if an I/O error occurs
     * @throws JSONException if a JSON error occurs
     */
    private void sendingFile(Socket socket) throws IOException, JSONException {
        JSONObject data = new JSONObject();
        data.put("fileID", fileID);
        byte[] bytes = readFile();
        if (bytes != null) {
            data.put("data", bytes);
            data.put("finish", false);
        } else {
            data.put("finish", true);
            close();    //  to close file
            status.done();
        }
        socket.emit("sending", data, new Ack() {
            @Override
            public void call(Object... os) {
                
                /* Call back function for sending more file data. This function
                   indicates the server has received the file data we're sending
                   so we need send more file data until we finish, thus the
                   response Boolean true or false                             */
                
                if (os.length > 0) {
                    boolean act = (boolean) os[0];
                    if (act) {
                        try {
                            if (!status.isPause()) {                            // This function will call recursively until act = false
                                showStatus((int) getPercentage());
                                sendingFile(socket);                            // Continues sending more file chunks
                            } else {
                                pause = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    
    /**
     * Updates the status and repaints the table to reflect the current status.
     *
     * @param values the current percentage of the file sent
     */
    public void showStatus(int values) {
        status.showStatus(values);
        table.repaint();
    }
}
