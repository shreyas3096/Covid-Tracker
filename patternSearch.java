import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static com.mongodb.client.model.Filters.regex;

public class patternSearch extends JFrame{
    //private JButton dbConnectButton;
    private JLabel searchLabel;
    JTextField searchText;
    private JPanel searchPanel;
    private JButton searchButton;
    private JTable outputTable;
    private JScrollPane scroller;
    //JTextArea output;
    private JButton clearSearch;
    private JButton homeButton;

    MongoDatabase sampleDB = null;
    MongoClient client = null;
    MongoCollection<Document> collection = null;
    MongoCursor<Document> cursor = null;
    MongoCursor<String> dbList = null;
    MongoCursor<String> collList =null;
    WindowListener exitListener = null;
    DefaultTableModel model;


    public patternSearch(String title){
        super(title);


        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(searchPanel);
        //this.getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.pack();

        searchPanel.setBackground(Color.WHITE);

        //dbConnectButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,25,true));
        //dbConnectButton.addActionListener(new ConnectMongo());



        //mongoConnectTextArea.setVisible(false);
        //mongoConnectTextArea.addComponentListener(new ComponentAdapter() {
        // });

        //To set scroll positions at the top of scrollpane
        //DefaultCaret caret = (DefaultCaret) output.getCaret();
        //caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        //scroller = new JScrollPane(outputTable);
        //scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //scroller.setBounds(32, 77, 943, 481);
        //this.add(scroller);

        outputTable = new JTable(200,1);
        outputTable.setToolTipText("");
        outputTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        outputTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "Overview"
                }
        ) {
            Class[] columnTypes = new Class[] {
                    String.class
            };
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
//        outputTable.setModel(new DefaultTableModel(
//                new Object[][] {
//                },
//                new String[] {
//                        "Country", "State", "Date", "Deaths", "Confirmed"
//                }
//        ) {
//            Class[] columnTypes = new Class[] {
//                    String.class, String.class, String.class, Double.class, Double.class
//            };
//            public Class getColumnClass(int columnIndex) {
//                return columnTypes[columnIndex];
//            }
//        });
        outputTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        outputTable.getColumnModel().getColumn(0).setMinWidth(75);
//        outputTable.getColumnModel().getColumn(1).setPreferredWidth(450);
//        outputTable.getColumnModel().getColumn(1).setMinWidth(150);
//        outputTable.getColumnModel().getColumn(2).setPreferredWidth(280);
//        outputTable.getColumnModel().getColumn(2).setMinWidth(150);
//        outputTable.getColumnModel().getColumn(3).setPreferredWidth(125);
//        outputTable.getColumnModel().getColumn(3).setMinWidth(100);
//        outputTable.getColumnModel().getColumn(4).setMinWidth(25);
        outputTable.setRowSelectionAllowed(true);
        outputTable.setColumnSelectionAllowed(false);
        model = (DefaultTableModel)outputTable.getModel();

        searchButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        searchButton.addActionListener(new GetMongo());

        clearSearch.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        clearSearch.addActionListener(new ClearMongo() {
        });

        homeButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        homeButton.addActionListener(new welcomePageListener(){

        });
        homeButton.setActionCommand("home");
        setVisible(true);

        outputTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
//                ArrayList<String> val = new ArrayList<>();
//                int index = outputTable.getSelectedRow();
//                TableModel model = outputTable.getModel();
//                String state = (String) model.getValueAt(index,0);
//                String[] values = state.split(",");
//                for (int i=0;i<values.length;i++){
//
//                    val.add(values[i]);
//                }

                //JOptionPane.showMessageDialog(null,"Details about this state:\n Country: " + val.get(0)
                //      +"\n State: " + val.get(1) + "\n State code:" + val.get(2) +"\n Deaths: " + val.get(3) + "\n Confirmed: " + val.get(4));

                JOptionPane.showMessageDialog(null,getPanel(),"DETAILS : ",JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private JPanel getPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        ArrayList<String> val = new ArrayList<>();
        int index = outputTable.getSelectedRow();
        TableModel model = outputTable.getModel();
        String state = (String) model.getValueAt(index,0);
        String[] values = state.split("\t");
        for (int i=0;i<values.length;i++){

            val.add(values[i]);
        }
        JLabel country = getLabel("Country : " + val.get(0));
        JLabel stateName = getLabel("State : " + val.get(1));
        JLabel date = getLabel("Date : " + val.get(2));
        JLabel deaths = getLabel("Deaths: " + val.get(3));
        JLabel confirmed = getLabel("Confirmed: " + val.get(4));
        panel.add(country);
        panel.add(stateName);
        panel.add(date);
        panel.add(deaths);
        panel.add(confirmed);

        return panel;
    }

    private JLabel getLabel(String title) {
        return new JLabel(title);
    }


//    public static void main(String[] args) {
//        JFrame searchFrame = new patternSearch("Pattern Search Page");
//        searchFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//        searchFrame.setVisible(true);
//    }

//    class ConnectMongo implements ActionListener {
//        public void actionPerformed (ActionEvent event) {
//            //in this section open the connection to MongoDB.
//            //You should enter the code to connect to the database here
//            //Remember to connect to MongoDB, connect to the database and connect to the
//            //    desired collection
//
//            client = MongoClients.create("mongodb+srv://Priyanka:ISTE612@cluster0.igipu.mongodb.net/dbcovid?retryWrites=true&w=majority");
//            //   client = MongoClients.create("mongodb://localhost:27017");
//
//            mongoConnectTextArea.setVisible(true);
//            mongoConnectTextArea.append("Connection to server completed\n");
//
//            //Get a List of databases on the server connection
//            dbList = client.listDatabaseNames().iterator();
//            //output.append("LIST OF DATABASES\n");
//
//            //   while (dbList.hasNext()) {
//            //       output.append(dbList.next());
//            //output.append(cursor.next().toJson());
//            //       output.append("\n");
//            //   }
//
//
//            //access the database
//            sampleDB = client.getDatabase("dbcovid");
//            mongoConnectTextArea.append("Connection to database completed\n");
////Get a List of collection in the database
//            collList = sampleDB.listCollectionNames().iterator();
//            mongoConnectTextArea.append("LIST OF COLLECTIONS\n");
//
//            while (collList.hasNext()) {
//                mongoConnectTextArea.append(collList.next());
//                mongoConnectTextArea.append("\n");
//            }
//
//            //get the collection
//
//            collection = sampleDB.getCollection("covid19");
//
//            mongoConnectTextArea.append("Collection obtained\n");
//
//        }//actionPerformed
//
//
//    }//class ConnectMongo

    class GetMongo implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            // In this section you should retrieve the data from the collection
            // and use a cursor to list the data in the output JTextArea
            outputTable.setVisible(true);
            client = MongoClients.create("mongodb+srv://dbcovid:ISTE612@cluster0.igipu.mongodb.net/dbcovid?retryWrites=true&w=majority");
            dbList = client.listDatabaseNames().iterator();
            sampleDB = client.getDatabase("dbcovid");
            collList = sampleDB.listCollectionNames().iterator();
            collection = sampleDB.getCollection("covid19");

            //Normal Find regex
            String input = searchText.getText();
            String regexPattern = ".*\\b" + input + ".*\\b";
            cursor = collection.find(regex("state", regexPattern, "i")).sort(new BasicDBObject("state",1)).iterator();

            int cnt = 0;

            while(cursor.hasNext()) {
                Document d = cursor.next();
                //System.out.println(d.get("country")+"\t" + d.get("state") +"\t" +d.get("date") + "\t" +d.get("death") +"\t"+ d.get("confirmed") +"\n");

                model.addRow(new Object [] {d.get("country") +"     \t" + d.get("state") +"     \t" +d.get("date") + "     \t" +d.get("death") +"     \t"+ d.get("confirmed") +"     \t"});
                //model.addRow(new Object[] {d.get("state")});
                //model.addRow(new Object [] {d.get("country") +"\t" + d.get("state") +"\t" +d.get("date") + "\t" +d.get("death") +"\t"+ d.get("confirmed") +"\n"});
                //output.append(d.get("country") +"\t" + d.get("state") +"\t" +d.get("date") + "\t" +d.get("death") +"\t"+ d.get("confirmed") +"\n");
                cnt = cnt+1;
            }

            scroller.setViewportView(outputTable);
            //output.append("The count is " + cnt + "\n");
        }//actionPerformed
    }

    class ClearMongo implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            //in this section open the connection. Should be able to see if it is not null
            // to see if ti is already open
            // output.setText("");
            DefaultTableModel model = (DefaultTableModel) outputTable.getModel();
            model.setRowCount(0);

            searchText.setText("Enter a new search pattern");

        }//actionPerformed


    }//class ClearMongo

    class welcomePageListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();

            if (cmd.equals("home")) {
                dispose();

                new welcomePage("Welcome Page").setVisible(true);
            }
        }
    }

}

