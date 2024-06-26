package data;

import io.socket.client.Ack;
import io.socket.client.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import org.json.JSONException;
import org.json.JSONObject;
import swing.PanelStatus_Item;

/**
 * DataFileServer handles the operations for file transfer over a socket connection.
 * It can save the received file data and provides status updates using GUI components.
 */
public class DataFileServer {

    private int fileID;
    private String fileName;
    private String fileSize;
    private long fileSizeLength;
    private File outPutPath;
    private boolean status;
    private PanelStatus_Item item;
    private JTable table;
    private DataWriter writer;
    private Socket socket;
    private boolean pause;
    
    public PanelStatus_Item getItem() {
        return item;
    }

    public void setItem(PanelStatus_Item item) {
        this.item = item;
    }

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

    public File getOutPutPath() {
        return outPutPath;
    }

    public void setOutPutPath(File outPutPath) {
        this.outPutPath = outPutPath;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public DataFileServer(int fileID, String fileName, String fileSize, File outPutPath, boolean status) {
        this.fileID = fileID;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.outPutPath = outPutPath;
        this.status = status;
    }
    
    /**
     * Constructor to initialize the DataFileServer with a JSON object and setup the UI.
     *
     * @param json   the JSON object containing file details
     * @param table  the JTable to display status
     * @param socket the socket used for file transfer
     * @throws JSONException if a JSON error occurs
     */
    public DataFileServer(JSONObject json, JTable table, Socket socket) throws JSONException {
        fileID = json.getInt("fileID");                                         // Parsing file details from JSON object
        fileName = json.getString("fileName");
        fileSize = json.getString("fileSize");
        fileSizeLength = json.getLong("fileSizeLength");
        item = new PanelStatus_Item();
        this.table = table;
        this.socket = socket;
        item.addEventSave(new ActionListener() {                                
            @Override
            public void actionPerformed(ActionEvent ae) {                       // Event listener for saving the file
                JFileChooser ch = new JFileChooser();
                ch.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int opt = ch.showSaveDialog(null);
                if (opt == JFileChooser.APPROVE_OPTION) {                       // Set output path and start saving the file
                    outPutPath = new File(ch.getSelectedFile().getAbsolutePath() + "/" + fileName);
                    item.startFile();
                    try {
                        saveFile();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        item.addEvent(new ActionListener() {                                    // Event listener for pause and resume functionality
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!item.isPause() && pause) {
                    pause = false;
                    try {
                        saveFile();                                             // Resume file saving
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    
     /**
     * Saves the file by requesting chunks of data from the server and writing them to the output file.
     *
     * @throws IOException  if an I/O error occurs
     * @throws JSONException if a JSON error occurs
     */
    private void saveFile() throws IOException, JSONException {
        // Initialize DataWriter if not already initialized, also needs the file size.
        if (writer == null) {
            writer = new DataWriter(outPutPath, fileSizeLength);
        }
        JSONObject data = new JSONObject();                                     // Create a JSON object to request the next file chunk, also gonna need a socket to request.
        data.put("fileID", fileID);                                              
        data.put("length", writer.getFileLength());                              
        
        socket.emit("request_file", data, new Ack() {                           // Emit the request to the server
            @Override
            public void call(Object... os) {
                try {
                    if (os.length > 0) {
                        byte[] b = (byte[]) os[0];
                        writer.writeFile(b);                                    // Write the received chunk to the file
                        item.showStatus((int) writer.getPercentage());          // Updating the status
                        table.repaint();
                        if (!item.isPause()) {                                  // Handling pause logic
                            saveFile();
                        } else {
                            pause = true;
                        }
                    } else {
                        item.showStatus((int) writer.getPercentage());          // Finalizing the file saving process
                        item.done();
                        table.repaint();
                        writer.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Converts the file details into a row for the JTable.
     *
     * @param row the row number
     * @return an array of objects representing the table row
     */
    public Object[] toTableRow(int row) {
        return new Object[]{this, row, fileName, fileSize, "Next Update"};
    }
}
