import com.mongodb.client.*;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.util.ArrayList;

public class Geo_search extends JFrame {
    private JPanel searchPanel;
    JTextField longitude;
    JTextField latitude;
    //private JTextArea output;
    private JButton Search;
    private JScrollPane scroller;
    private JButton clearSearchButton;
    private JButton homeButton;
    private JTable outputTable;
    private JLabel latitudeLabel;
    private JLabel longitudeLabel;
    private JButton colorDisplayButton;


    //    private JLabel colorStatusLabel;
    String filePath = "/Users/shreyasmc/IdeaProjects/Covid_Tracker/src";
    static int cases = 0;

    MongoDatabase sampleDB = null;
    MongoClient client = null;
    MongoCollection<Document> collection = null;
    MongoCursor<Document> cursor = null;
    MongoCursor<String> dbList = null;
    MongoCursor<String> collList =null;
    WindowListener exitListener = null;
    DefaultTableModel model;

    public Geo_search(String title) {

        super(title);


        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(searchPanel);
        //this.getContentPane().setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.pack();

        searchPanel.setBackground(Color.WHITE);
        searchPanel.setLayout(null);

        outputTable = new JTable(2,1);
        outputTable.setToolTipText("");
        outputTable.setFont(new Font("Tahoma" ,Font.PLAIN, 15));
        outputTable.setModel(new DefaultTableModel(
                new Object[][] {
                },
                new String[] {
                        "                                           RESULTS"
                }
        ) {
            Class[] columnTypes = new Class[] {
                    String.class
            };
            public Class getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }
        });
        outputTable.getColumnModel().getColumn(0).setPreferredWidth(250);
        outputTable.getColumnModel().getColumn(0).setMinWidth(75);

        outputTable.setRowSelectionAllowed(true);
        outputTable.setColumnSelectionAllowed(false);
        model = (DefaultTableModel)outputTable.getModel();

        Search.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        Search.addActionListener(new Geo_search.GetMongo());

        clearSearchButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        clearSearchButton.addActionListener(new Geo_search.ClearMongo() {
        });

        homeButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        homeButton.addActionListener(new Geo_search.welcomePageListener(){

        });
        homeButton.setActionCommand("home");
        setVisible(true);

        outputTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null,getPanel(),"DETAILS : ",JOptionPane.INFORMATION_MESSAGE);
            }
        });


        colorDisplayButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
        colorDisplayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cases >= 1000){
                    try {
                        download("Red");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                else if(cases > 500 && cases < 1000) {
                    try {
                        download("Orange");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                else if(cases >= 50 && cases <= 500) {
                    try {
                        download("Yellow");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                else {
                    try {
                        download("Green");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

            }
        });

    }

    private JPanel getPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        ArrayList<String> val = new ArrayList<>();
        int index = outputTable.getSelectedRow();
        TableModel model = outputTable.getModel();
        int totalCases = (int) model.getValueAt(index,0);
        JLabel casesConfirmed = getLabel("TotalCases: " + totalCases);
        panel.add(casesConfirmed);
        return panel;
    }

    private JLabel getLabel(String title) {
        return new JLabel(title);
    }


    class GetMongo implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            outputTable.setVisible(true);
            client = MongoClients.create("mongodb+srv://dbcovid:ISTE612@cluster0.igipu.mongodb.net/dbcovid?retryWrites=true&w=majority");
            dbList = client.listDatabaseNames().iterator();
            sampleDB = client.getDatabase("dbcovid");
            collList = sampleDB.listCollectionNames().iterator();
            collection = sampleDB.getCollection("covid19");

            // retrieving text
            String input1 = longitude.getText();
            String input2 = latitude.getText();

            float longitudeValue = Float.parseFloat(input1);
            float latitudeValue = Float.parseFloat(input2);



            //System.out.println("Pinged Old Code");
           // try {
            double distanceInRad = 100.0 / 6371;
            FindIterable<Document> result = collection.find(
                    Filters.geoWithinCenterSphere("loc", longitudeValue, latitudeValue, distanceInRad));

            for(Document doc : result) {
                //System.out.println("Pinged the Document Loop");
                //access documents e.g. doc.get()
                cases += Integer.parseInt(doc.get("confirmed").toString());

//                System.out.println(doc.get("country"));
//                System.out.println(doc.get("confirmed"));
            }

                model.addRow(new Object[]{cases});

                scroller.setViewportView(outputTable);
         //   }
            //catch (Exception a){
            //    model.addRow(new Object[]{"The Geolocation entered is either contradicting or out of bound in our dataset"});

            //}

        }
    }

    public class welcomePageListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            String cmd = e.getActionCommand();

            if (cmd.equals("home")) {
                dispose();

                new welcomePage("Welcome Page").setVisible(true);

            }
        }


    }


    public class ClearMongo implements ActionListener {
        public void actionPerformed (ActionEvent event) {
            //in this section open the connection. Should be able to see if it is not null
            // to see if ti is already open
            // output.setText("");
            DefaultTableModel model = (DefaultTableModel) outputTable.getModel();
            model.setRowCount(0);

            longitude.setText("Enter the new values");
            latitude.setText("Enter the new values");
            cases = 0;


        }//actionPerformed
    }

    public void download(String fileName) throws InterruptedException {

        try {
            MongoDatabase database = client.getDatabase("dbcovid");
            GridFSBucket gridBucket = GridFSBuckets.create(database);

            FileOutputStream fileOutputStream = new FileOutputStream(filePath+fileName+".jpg");
            gridBucket.downloadToStream(fileName, fileOutputStream);

            //gridBucket.
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }

        JFrame f = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize(); //this is your screen size

        f.setUndecorated(true); //removes the surrounding border

        ImageIcon image = new ImageIcon(filePath+fileName+".jpg"); //imports the image

        JLabel lbl = new JLabel(image); //puts the image into a jlabel

        f.getContentPane().add(lbl); //puts label inside the jframe

        f.setSize(image.getIconWidth(), image.getIconHeight()); //gets h and w of image and sets jframe to the size

        int x = (screenSize.width - f.getSize().width)/2; //These two lines are the dimensions
        int y = (screenSize.height - f.getSize().height)/2;//of the center of the screen

        f.setLocation(x, y); //sets the location of the jframe
        f.setVisible(true); //makes the jframe visible

        //Thread.sleep(10000);
//
//        f.dispose();

    }

//    public class displayImage implements ActionListener {
//        public void actionPerformed (ActionEvent event) {
//            //in this section open the connection. Should be able to see if it is not null
//            // to see if ti is already open
//            // output.setText("");
//            image1 = new ImageIcon(getClass().getResource("Orange.jpg"));
//            display = new JLabel(image1);
//            add(display);
//
//        }//actionPerformed
//    }

}
