package data;

import com.corundumstudio.socketio.SocketIOClient;
import java.awt.Component;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.JTable;
import swing.PanelStatus;

/**
 * DataClient represents a client connected to the server, handling file transfers and updating the UI.
 */
public class DataClient {

    public PanelStatus getStatus() {
        return status;
    }

    public SocketIOClient getClient() {
        return client;
    }

    public void setClient(SocketIOClient client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Constructs a DataClient object with the specified SocketIO client, name, and table.
     * Initializes the panel status and associates the table.
     * @param client the SocketIO client object.
     * @param name the name of the client.
     * @param table the JTable to be associated with this client.
     */
    public DataClient(SocketIOClient client, String name, JTable table) {
        this.client = client;
        this.name = name;
        this.status = new PanelStatus();
        this.table = table;
    }

    public DataClient() {
    }

    private SocketIOClient client;
    private String name;
    // Key integer is fileID
    // Hash to store multiple transfers
    private final HashMap<Integer, DataWriter> list = new HashMap<>();
    private PanelStatus status;
    private JTable table;

    /**
     * Adds a DataWriter for a specific file ID and updates the panel status and table row height.
     * @param data the DataWriter to be added.
     * @param fileID the ID of the file.
     */
    public void addWrite(DataWriter data, int fileID) {
        list.put(fileID, data);
        status.addItem(fileID, data.getFile().getName(), data.getMaxFileSize());
        // Update table row height
        autoRowHeight(table, 3);
    }

    /**
     * Writes data to the file associated with the specified file ID and updates the panel status.
     * @param data the data to be written.
     * @param fileID the ID of the file.
     * @throws IOException if an I/O error occurs.
     */
    public void writeFile(byte[] data, int fileID) throws IOException {
        list.get(fileID).writeFile(data);
        status.updateStatus(fileID, (int) list.get(fileID).getPercentage());
        table.repaint();
    }

    /**
     * Closes the DataWriter associated with the specified file ID.
     * @param fileID the ID of the file.
     * @throws IOException if an I/O error occurs.
     */
    public void closeWriter(int fileID) throws IOException {
        list.get(fileID).close();
    }

    /**
     * Converts the client information to an array of objects for table representation.
     * @param row the row index.
     * @return an array of objects representing the client information.
     */
    public Object[] toRowTable(int row) {
        return new Object[]{this, row, name};
    }

    /**
     * Adjusts the row height of the specified table based on the preferred height of the cells in the specified columns.
     * @param table the JTable whose row height is to be adjusted.
     * @param cols the columns to consider for height adjustment.
     */
    private void autoRowHeight(JTable table, int... cols) {
        for (int row = 0; row < table.getRowCount(); row++) {
            int rowHeight = table.getRowHeight();
            for (int col : cols) {
                Component comp = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
                if (comp.getPreferredSize().height > rowHeight) {
                    rowHeight = comp.getPreferredSize().height;
                }
            }
            table.setRowHeight(row, rowHeight);
        }
    }

    /**
     * Retrieves the length of the file associated with the specified file ID.
     * @param fileID the ID of the file.
     * @return the length of the file in bytes.
     * @throws IOException if an I/O error occurs.
     */
    public long getFileLength(int fileID) throws IOException {
        return list.get(fileID).getFileLength();
    }

    /**
     * Retrieves the DataFileServer object representing the file transfer for the specified file ID.
     * @param fileID the ID of the file.
     * @return the DataFileServer object.
     * @throws IOException if an I/O error occurs.
     */
    public DataFileServer getDataFileServer(int fileID) throws IOException {
        DataWriter data = list.get(fileID);
        String fileName = data.getFile().getName();
        return new DataFileServer(fileID, fileName.substring(fileName.indexOf("-", 0) + 1), data.getMaxFileSize(), data.getFileLength(), data.getFile());
    }
}
