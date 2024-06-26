package client;

import data.DataFileServer;
import data.DataReader;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import java.awt.Component;
import java.io.File;
import java.net.URISyntaxException;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.json.JSONException;
import org.json.JSONObject;
import swing.CellEditor;
import swing.CellEditorFile;


/**
 * The Client class provides the GUI and handles interactions with the server for file transfer operations.
 * It allows users to connect to a server, select files for upload, and manage file transfer status.
 */
public class Client extends javax.swing.JFrame {
    
    private final int SERVER_PORT = 9999;  
    private final DefaultTableModel defaultTableModel;
    private final DefaultTableModel defaultTableModelFile;
    private Socket client;
    private String IP = "localhost";
    
    /**
     * Creates new form Client and initializes the components.
     */
    public Client() {
        initComponents();
        defaultTableModel = (DefaultTableModel) table.getModel();
        defaultTableModelFile = (DefaultTableModel) tableFile.getModel();                    // Set custom cell renderer for the status column in the UPLOAD table
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                Object data = jtable.getValueAt(i, 0);
                if (data instanceof DataReader) {
                    DataReader dataReader = (DataReader) data;
                    Component component = dataReader.getStatus();
                    component.setBackground(com.getBackground());
                    return component;
                } else {
                    return com;
                }
            }
        });
        table.getColumnModel().getColumn(4).setCellEditor(new CellEditor());    // Set custom cell editor for the status column in the UPLOAD table
        tableFile.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() { // Set custom cell renderer for the status column in the SERVER FILES table
            @Override
            public Component getTableCellRendererComponent(JTable jtable, Object o, boolean bln, boolean bln1, int i, int i1) {
                Component com = super.getTableCellRendererComponent(jtable, o, bln, bln1, i, i1);
                DataFileServer data = (DataFileServer) jtable.getValueAt(i, 0);
                Component component = data.getItem();
                component.setBackground(com.getBackground());
                return component;
            }
        });
        tableFile.getColumnModel().getColumn(4).setCellEditor(new CellEditorFile());
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        nameTextField = new javax.swing.JTextField();
        nameJLabel = new javax.swing.JLabel();
        cmdConnect = new javax.swing.JButton();
        fileSelectButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableFile = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Client");

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "No", "File Name", "Size", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table);
        if (table.getColumnModel().getColumnCount() > 0) {
            table.getColumnModel().getColumn(0).setMinWidth(0);
            table.getColumnModel().getColumn(0).setPreferredWidth(0);
            table.getColumnModel().getColumn(0).setMaxWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        nameJLabel.setText("Name");

        cmdConnect.setText("Connect");
        cmdConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdConnectActionPerformed(evt);
            }
        });

        fileSelectButton.setText("File");
        fileSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileSelectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(fileSelectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(nameJLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmdConnect)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameJLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmdConnect))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileSelectButton)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Client", jPanel1);

        tableFile.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Data", "No", "File Name", "Size", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tableFile);
        if (tableFile.getColumnModel().getColumnCount() > 0) {
            tableFile.getColumnModel().getColumn(0).setMinWidth(0);
            tableFile.getColumnModel().getColumn(0).setPreferredWidth(0);
            tableFile.getColumnModel().getColumn(0).setMaxWidth(0);
            tableFile.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 745, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("File On Server", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addGap(0, 0, 0))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents
    /**
     * Handles the Connect button click event.
     * Connects to the server and sets up event listeners for socket events.
     *
     * @param evt the ActionEvent triggered by the Connect button
     */
    private void cmdConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmdConnectActionPerformed
        if (client == null) {
            try {
                client = IO.socket("http://" + IP + ":" + SERVER_PORT);         // Init socket connection to the server
                client.on("exit_app", new Emitter.Listener() {                  // Event listener to handle exit_app event from the server
                    @Override
                    public void call(Object... os) {
                        System.exit(0);
                    }
                });
                
                client.on("new_file", new Emitter.Listener() {                  // Event listener to handle new_file event from the server
                    @Override
                    public void call(Object... os) {
                        //  Add new File
                        try {                                                   // Try adding new file information to the table
                            addFile(new DataFileServer((JSONObject) os[0], table, client));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                
                client.open();                                                  // Open the socket connection
                String userName = nameTextField.getText().trim();               // Set the username and request the list of files from the server
                client.emit("set_user", userName);                              // Emitting set_user event to socket
                client.emit("request", "list_file", new Ack() {                 // Note: Takes eventname(s), arg(s), and ack
                    @Override
                    public void call(Object... os) {
                        try {                                                   // Try adding each file to the table
                            for (Object o : os) {
                                addFile(new DataFileServer((JSONObject) o, table, client));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (URISyntaxException e) {
                System.err.println(e);
            }
        } else {                                                                // If already connected, just set the user
            client.emit("set_user", nameTextField.getText().trim());
        }
    }//GEN-LAST:event_cmdConnectActionPerformed
    /**
     * Handles the File button click event.
     * Opens a file chooser to select files for upload and starts the file upload process.
     *
     * @param evt the ActionEvent triggered by the File button
     */
    private void fileSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileSelectButtonActionPerformed
        JFileChooser fileChooser = new JFileChooser();                          // Create a file chooser with multi-selection enabled
        fileChooser.setMultiSelectionEnabled(true);
        int opt = fileChooser.showOpenDialog(this);
        
        if (opt == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();
            for (File file : files) {
                try {                                                           // Try creating a DataReader for each selected file and add it to the table
                    DataReader reader = new DataReader(file, table);
                    defaultTableModel.addRow(reader.toRowTable(table.getRowCount() + 1));   // Process changes to table
                    reader.startSend(client);                                   // File sending can be started from the client
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_fileSelectButtonActionPerformed
    /**
     * Adds a DataFileServer object to the server files table.
     *
     * @param data the DataFileServer object to be added to the table
     */
    private void addFile(DataFileServer data) {
        defaultTableModelFile.addRow(data.toTableRow(tableFile.getRowCount() + 1));
    }
    
    /* Create and display the form */
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
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cmdConnect;
    private javax.swing.JButton fileSelectButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel nameJLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JTable table;
    private javax.swing.JTable tableFile;
    // End of variables declaration//GEN-END:variables
}
