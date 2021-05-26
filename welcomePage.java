import com.mongodb.client.*;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;

public class welcomePage extends JFrame{
   private JPanel mainPanel;
   private JTextField pageTitle;
   private JTextField introduction;
   private JButton patternSearchBtn;
   private JButton geoLocationSearchBtn;
   private JButton dbConnectButton;
   private JTextArea mongoConnectTextArea;
   private int connectionHandle = 0;

   MongoDatabase sampleDB = null;
   MongoClient client = null;
   MongoCollection<Document> collection = null;
   MongoCursor<Document> cursor = null;
   MongoCursor<String> dbList = null;
   MongoCursor<String> collList =null;

   public welcomePage(String title){
      super(title);
   
      this.setLayout(null);
      this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      this.setContentPane(mainPanel);
      this.pack();
   
      mainPanel.setBackground(Color.WHITE);
   
      pageTitle.setBorder(BorderFactory.createLineBorder(Color.white));
      pageTitle.setBackground(Color.white);
   
      introduction.setBorder(BorderFactory.createLineBorder(Color.white));
      introduction.setBackground(Color.white);
   
   
      patternSearchBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW,5,true));
      geoLocationSearchBtn.setBorder(BorderFactory.createLineBorder(Color.YELLOW,5,true));
   
   
      patternSearchBtn.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //new GUI_Test();
               if(connectionHandle == 1)
                  new patternSearch("Pattern Search Page");
               else
                  System.out.println("You did not connect");
               dispose();
            //setState(JFrame.ICONIFIED);
            }
         });
      geoLocationSearchBtn.addActionListener(
         new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            //new GUI_Test();
               if (connectionHandle == 1)
                  new Geo_search("Geo Location Search Page");
               else
                  System.out.println("You did not connect");
               dispose();
            //setState(JFrame.ICONIFIED);
            }
         });
   
      dbConnectButton.setBorder(BorderFactory.createLineBorder(Color.GREEN,5,true));
      dbConnectButton.addActionListener(new ConnectMongo());
   
      mongoConnectTextArea.setVisible(false);
      mongoConnectTextArea.addComponentListener(
         new ComponentAdapter() {
         });
   }

   public static void main(String[] args) {
      JFrame frame = new welcomePage("Welcome Page");
      frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
      frame.setVisible(true);
   }


   class ConnectMongo implements ActionListener {
      public void actionPerformed (ActionEvent event) {
         //in this section open the connection to MongoDB.
         //You should enter the code to connect to the database here
         //Remember to connect to MongoDB, connect to the database and connect to the
         //    desired collection
      
         client = MongoClients.create("mongodb+srv://David:ISTE610@cluster0.igipu.mongodb.net/dbcovid?retryWrites=true&w=majority");
         //   client = MongoClients.create("mongodb://localhost:27017");
      
         mongoConnectTextArea.setVisible(true);
         //mongoConnectTextArea.append("Connection to server completed\n");
      
         //Get a List of databases on the server connection
         dbList = client.listDatabaseNames().iterator();
         //output.append("LIST OF DATABASES\n");
      
         //   while (dbList.hasNext()) {
         //       output.append(dbList.next());
         //output.append(cursor.next().toJson());
         //       output.append("\n");
         //   }
      
      
         //access the database
         sampleDB = client.getDatabase("dbcovid");
         //mongoConnectTextArea.append("Connection to database completed\n");
      //Get a List of collection in the database
         collList = sampleDB.listCollectionNames().iterator();
         //mongoConnectTextArea.append("LIST OF COLLECTIONS\n");
      
      //            while (collList.hasNext()) {
      //                mongoConnectTextArea.append(collList.next());
      //                mongoConnectTextArea.append("\n");
      //            }
      
         //get the collection
      
         collection = sampleDB.getCollection("covid19");
      
         //mongoConnectTextArea.append("Collection obtained\n");
         mongoConnectTextArea.append("SUCCESS"); 
         connectionHandle = 1;
      
      
      }//actionPerformed
   
   
   }//class ConnectMongo

}