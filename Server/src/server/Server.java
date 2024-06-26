package server;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import data.DataClient;
import data.DataFileSending;
import data.DataFileServer;
import data.DataInitFile;
import data.DataRequestFile;
import data.DataWriter;
import java.awt.Color;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


/**
 * The Server class is responsible for handling file transfers between clients using Socket.IO.
 * It extends JFrame to provide a GUI for monitoring connected clients
 * and managing file transfers. This class sets up the server configurations, event listeners,
 * and handles client connections, disconnections, and file transfer processes.
 */
public class Server extends javax.swing.JFrame {
    
    private final int SERVER_PORT = 9999;
    private final List<DataFileServer> listFiles = new ArrayList<>();
    private SocketIOServer server;
    
    /**
    * Initializes the server GUI and sets up the custom table cell renderer for displaying client statuses.
    * The table cell renderer customizes the display of the client status in the table.
    */
    public Server() {
        initComponents();
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component component = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                Object data = jtable.getValueAt(i, 0);
                if (data instanceof DataClient) {
                    Component c = ((DataClient) data).getStatus();
                    c.setBackground(component.getBackground());
                    return c;
                } else {
                    return component;
                }
            }
        });
    }
    
    /**
    * This method initializes the GUI components. It sets up the table, start button,
    * and the context menu for disconnecting clients. This code is auto-generated by the Swing GUI designer.
    * Can be ignored for manual edits.
    */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menu = new javax.swing.JPopupMenu();
        disconnect = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        startServerButton = new javax.swing.JButton();
        connectionStatusLabel = new javax.swing.JLabel();

        disconnect.setText("Disconnect This Client");
        disconnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectActionPerformed(evt);
            }
        });
        menu.add(disconnect);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("File Server");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "No", "Name", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tableMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(30);
            table.getColumnModel().getColumn(3).setPreferredWidth(300);
        }

        startServerButton.setText("Start Server");
        startServerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startServerButtonActionPerformed(evt);
            }
        });

        connectionStatusLabel.setText("Server not running.");
        connectionStatusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 913, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(startServerButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(connectionStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startServerButton)
                    .addComponent(connectionStatusLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    /**
    * Starts the Socket.IO server and initializes event listeners for various client actions.
    * This method is triggered by clicking the start button in the GUI.
    * @param evt the ActionEvent triggered by clicking the start button.
    */
    private void startServerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startServerButtonActionPerformed
        if (server == null) {
            Configuration configuration = new Configuration();
            configuration.setPort(SERVER_PORT);
            server = new SocketIOServer(configuration);
            
            connectionStatusLabel.setText("Server running!");
            connectionStatusLabel.setForeground(Color.RED);
            
            //  Add event to server when client connected
            server.addConnectListener(new ConnectListener() {
                @Override
                public void onConnect(SocketIOClient socketIOClient) {
                    //  This method runs when new client connected and doesn't have a name 
                    DataClient client = new DataClient(socketIOClient, "", table);
                    //  Adds this data to the table
                    addTableRow(client);
                }
            });
            
            // Listening for the function onDisconnect();
            server.addDisconnectListener(new DisconnectListener() {
                @Override
                public void onDisconnect(SocketIOClient socketIOClient) {
                    removeClient(socketIOClient);
                }
            });
            
            // Listening for the function setUserName();
            server.addEventListener("set_user", String.class, new DataListener<String>() {
                @Override
                public void onData(SocketIOClient socketIOClient, String t, AckRequest ackReq) throws Exception {
                    setUserName(socketIOClient, t);
                }
            });
            
            // Listening for initFileTransfer();
            server.addEventListener("send_file", DataInitFile.class, new DataListener<DataInitFile>() {
                @Override
                public void onData(SocketIOClient socketIOClient, DataInitFile dataInit, AckRequest ackReq) throws Exception {
                    int fileID = initFileTransfer(socketIOClient, dataInit);
                    if (fileID > 0) {
                        //  call back function to client
                        ackReq.sendAckData(true, fileID);
                    }
                }
            });
            
            // Listening for writeFile() and executes.;
            server.addEventListener("sending", DataFileSending.class, new DataListener<DataFileSending>() {
                @Override
                public void onData(SocketIOClient socketIOClient, DataFileSending t, AckRequest ackReq) throws Exception {
                    if (!t.isFinish()) {
                        writeFile(socketIOClient, t);
                        ackReq.sendAckData(true);
                    } else {
                        ackReq.sendAckData(false);
                        DataFileServer data = closeFile(socketIOClient, t);
                        if (data != null) {
                            server.getBroadcastOperations().sendEvent("new_file", data);
                        }
                    }
                }
            });
            
            server.addEventListener("req_file_length", Integer.class, new DataListener<Integer>() {
                @Override
                public void onData(SocketIOClient socketIOClient, Integer t, AckRequest ackReq) throws Exception {
                    try {
                        long length = getFileLength(socketIOClient, t);
                        if (length > 0) {
                            ackReq.sendAckData(length + "");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            
            server.addEventListener("request", String.class, new DataListener<String>() {
                @Override
                public void onData(SocketIOClient socketIOClient, String t, AckRequest ackReq) throws Exception {
                    if (t.equals("list_file")) {
                        ackReq.sendAckData(listFiles.toArray());
                    }
                }
            });
            
            server.addEventListener("request_file", DataRequestFile.class, new DataListener<DataRequestFile>() {
                @Override
                public void onData(SocketIOClient socketIOClient, DataRequestFile t, AckRequest ackReq) throws Exception {
                    try {
                        byte b[] = getFile(t);
                        if (b != null) {
                            ackReq.sendAckData(b);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            server.start();
        }
    }//GEN-LAST:event_startServerButtonActionPerformed
    /**
    * Disconnects the selected client by sending an "exit_app" event to the client.
    * This method is triggered by selecting the disconnect menu item.
    * @param evt the ActionEvent triggered by selecting the disconnect menu item.
    */
    private void disconnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectActionPerformed
        if (table.getSelectedRow() >= 0) {
            int row = table.getSelectedRow();
            DataClient data = (DataClient) table.getValueAt(row, 0);
            data.getClient().sendEvent("exit_app", "");
        }
    }//GEN-LAST:event_disconnectActionPerformed
    /**
    * Displays the context menu when a row in the table is right-clicked.
    * @param evt the MouseEvent triggered by right-clicking on the table.
    */
    private void tableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tableMouseReleased
        if (table.getSelectedRow() >= 0 && SwingUtilities.isRightMouseButton(evt)) {
            menu.show(table, evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_tableMouseReleased
    /**
     * Adds a row to the table representing the connected client.
     * @param data the DataClient object representing the client.
     */
    private void addTableRow(DataClient data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(data.toRowTable(table.getRowCount() + 1));
    }
    /**
     * Removes the client from the table when they disconnect.
     * @param client the SocketIOClient object representing the client.
     */
    private void removeClient(SocketIOClient client) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                model.removeRow(i);
                break;
            }
        }
    }
    
    /**
     * Sets the username for the connected client in the table.
     * @param client the SocketIOClient object representing the client.
     * @param name the username to be set for the client.
     */
    private void setUserName(SocketIOClient client, String name) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                data.setName(name);
                model.setValueAt(name, i, 2);
                break;
            }
        }
    }
    
    /**
    * Initializes the file transfer process for a client.
    * @param client the SocketIOClient object representing the client.
    * @param dataInit the DataInitFile object containing initial file data.
    * @return the ID of the initialized file transfer.
    */
    private int initFileTransfer(SocketIOClient client, DataInitFile dataInit) {
        int id = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                try {
                    id = generateFileID();
                    File file = new File("C:/Users/bobas/Desktop/socket_data" + id + "-" + dataInit.getFileName());
                    DataWriter writer = new DataWriter(file, dataInit.getFileSize());
                    data.addWrite(writer, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return id;
    }

    /**
    * Writes data to the file being transferred.
    * @param client the SocketIOClient object representing the client.
    * @param file the DataFileSending object containing the data to be written.
    * @return true if the write operation was successful, false otherwise.
    */
    private boolean writeFile(SocketIOClient client, DataFileSending file) {
        boolean error = false;
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                try {
                    data.writeFile(file.getData(), file.getFileID());
                } catch (Exception e) {
                    error = true;
                    e.printStackTrace();
                }
                break;
            }
        }
        //  return true if not error
        return !error;
    }
    
    /**
     * Closes the file being transferred and adds it to the list of completed transfers.
     * @param client the SocketIOClient object representing the client.
     * @param file the DataFileSending object containing the data to be closed.
     * @return the DataFileServer object representing the completed file transfer.
     */
    private DataFileServer closeFile(SocketIOClient client, DataFileSending file) {
        DataFileServer fileServer = null;
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                try {
                    fileServer = data.getDataFileServer(file.getFileID());
                    listFiles.add(fileServer);
                    data.closeWriter(file.getFileID());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return fileServer;
    }
    
    /**
     * Retrieves the length of the file being transferred.
     * @param client the SocketIOClient object representing the client.
     * @param fileID the ID of the file being transferred.
     * @return the length of the file in bytes.
     * @throws IOException if an I/O error occurs.
     */
    private long getFileLength(SocketIOClient client, int fileID) throws IOException {
        for (int i = 0; i < table.getRowCount(); i++) {
            DataClient data = (DataClient) table.getValueAt(i, 0);
            if (data.getClient() == client) {
                return data.getFileLength(fileID);
            }
        }
        return 0;
    }
    
    /**
     * Generates a unique file ID for each file transfer.
     * returns the generated file ID.
     */
    private int fileID;
    private synchronized int generateFileID() {
        fileID++;
        return fileID;
    }

    /**
     * Retrieves the file data for the requested file.
     * @param data the DataRequestFile object containing the request details.
     * @return the file data as a byte array, or null if the end of the file is reached.
     * @throws IOException if an I/O error occurs.
     */
    private byte[] getFile(DataRequestFile data) throws IOException {
        for (DataFileServer d : listFiles) {
            if (d.getFileID() == data.getFileID()) {
                RandomAccessFile accFile = new RandomAccessFile(d.getOutPutPath(), "r");
                accFile.seek(data.getLength());
                long filePointer = data.getLength();
                long fileSize = d.getFileSizeLength();
                if (filePointer != fileSize) {
                    int max = 2000;
                    //  2000 is max send file per package
                    //  we spite it to send large file
                    long length = filePointer + max >= fileSize ? fileSize - filePointer : max;
                    byte[] b = new byte[(int) length];
                    accFile.read(b);
                    return b;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Server.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel connectionStatusLabel;
    private javax.swing.JMenuItem disconnect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menu;
    private javax.swing.JButton startServerButton;
    private javax.swing.JTable table;
    // End of variables declaration//GEN-END:variables
}
