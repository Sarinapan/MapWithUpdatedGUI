package map;

import java.io.IOException;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main extends Application {
    private Stage Mapwindow;
    private Stage Listwindow;
    private TableView<Unit> table;
    private static String uniqID;
    private static String callSign;
    private static String defLocation;
    private static String currEvent;
    private static String time;
    private static String type;
    private static String status;
   private Text actionStatus;
  
  
    private int id;
    
    private int incr = 1;
    private static Unit[] x = null;
    WebView browser = new WebView();
    WebEngine webEngine = browser.getEngine();
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage)throws ParserConfigurationException, SAXException, IOException{       
        //Load Map
        webEngine.load(getClass().getResource("mapHtmlFile.html").toString());

        Mapwindow = new Stage();
        Mapwindow.setTitle("Web Map");
        Mapwindow.setScene(new Scene(browser,1000,700, Color.web("#666970")));
        Mapwindow.getIcons().add(new Image("/map/NCP.PNG"));
        Mapwindow.show();
        
         
        //Load UnitTable
        //Listwindow = primaryStage;
       
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image("/map/NCP.PNG"));
        primaryStage.setTitle("Unit Table");
        
        Image image = new Image("/map/NCP.PNG");
        ImageView TIcon = new ImageView();
        TIcon.setImage(image);
        TIcon.setFitWidth(45);
        TIcon.setPreserveRatio(true);
        TIcon.setSmooth(true);
        TIcon.setCache(true);
        TIcon.setTranslateX(5);
        TIcon.setTranslateY(-10);
        
       ImageView Exit = new ImageView("/map/ExitButton.PNG");
       Exit.setFitHeight(25);
       Exit.setFitWidth(25);
       Exit.setTranslateX(570);
       Exit.setTranslateY(10);
       Exit.setOnMouseClicked(new EventHandler<MouseEvent>(){
           @Override
           public void handle(MouseEvent t){
               System.exit(0);
           }
           
       });
       
        
        createTable();
        createContextMenu();
        
        
  
      actionStatus = new Text();
      actionStatus.setFill(Color.FIREBRICK);
       
             createTable();
        createContextMenu();
        
       
        
        VBox vbox = new VBox(-15);
        
        vbox.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        vbox.setPadding(new Insets(0, 0, 15, 0));

        vbox.getChildren().addAll(Exit,TIcon,table, actionStatus);
        Scene scene = new Scene(vbox, 600,450); // w x h
       
       
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.getScene().setOnMouseDragged(new EventHandler<MouseEvent>() { 

          @Override 
          public void handle(MouseEvent event) { 
            primaryStage.setX(event.getScreenX()); 
            primaryStage.setY(event.getScreenY()); 
          } 
           
        }); 

         
         
       //createTable();
        //createContextMenu();
        
        //StackPane root = new StackPane();
        //root.getChildren().addAll(table);
        /*VBox vBox = new VBox();
        vBox.getChildren().addAll(table);   
        Scene scene = new Scene(vBox);*/
       
        //Listwindow.setScene(scene);
        //Listwindow.show();
        
    }
    
   public void createContextMenu(){
        ObservableList<Unit> unitSelected, allUnits;   
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        allUnits = table.getItems();
        unitSelected = table.getSelectionModel().getSelectedItems(); 
     
        //ContextMenu
        ContextMenu contextMenu = new ContextMenu();
        
        //menu items
        MenuItem item1 = new MenuItem("ChangeLocation");
        MenuItem item2 = new MenuItem("Dispatch");
        
        //Add item to context menu
        contextMenu.getItems().addAll(item1, item2);
        
        //set context menu to table
        table.setContextMenu(contextMenu);
        
        //changeLocationFunction
        item1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                unitSelected.sorted();
                id = checkId(unitSelected.get(0).getId());  
                webEngine.executeScript("changeLocation(" + id + ")"); 
                table.getSelectionModel().clearSelection();
            }
        });
        
        //dispatchLocation function
        item2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                unitSelected.sorted();
                for(int x = 0; x < unitSelected.size(); x++){
                   id = checkId(unitSelected.get(x).getId());
                   webEngine.executeScript("setMarkerId(" + id + ")");
                }    
                webEngine.executeScript("createMarker()");  
                table.getSelectionModel().clearSelection();
            }
        });        
    }
    public int checkId(String id){
        switch(id){
           case "U01": return 1;
           case "U02": return 2; 
           case "U03": return 3; 
           default: return -1;
       }      
    }
    
    public void createTable()throws ParserConfigurationException, SAXException, IOException{
        //ID column
        TableColumn<Unit, String> uniqIdCol = new TableColumn<>("ID");
        uniqIdCol.setMinWidth(100);
        uniqIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        //Location column
        TableColumn<Unit, String> currLocationCol = new TableColumn<>("Location");
        currLocationCol.setMinWidth(300);
        currLocationCol.setCellValueFactory(new PropertyValueFactory<>("currLocation"));
        //Time column
        TableColumn<Unit, String> timeCol = new TableColumn<>("Time");
        timeCol.setMinWidth(100);
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        //Type column
        TableColumn<Unit, String> typeCol = new TableColumn<>("Type");
        typeCol.setMinWidth(100);
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    
        //create Table
        table = new TableView<>();
        table.setItems(getUnits());
        table.getColumns().addAll(uniqIdCol,currLocationCol,timeCol,typeCol);        
    }
    
    public ObservableList<Unit> getUnits()throws ParserConfigurationException, SAXException, IOException{
        
        ObservableList<Unit> unit = FXCollections.observableArrayList();    
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse("UnitInfo.xml");
        NodeList nList = doc.getElementsByTagName("unit");
        
        x = new Unit[nList.getLength()];
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) node;
                uniqID = eElement.getAttribute("id");
                callSign = eElement.getElementsByTagName("callSign").item(0).getTextContent();
                defLocation = eElement.getElementsByTagName("defLocation").item(0).getTextContent();
                currEvent = eElement.getElementsByTagName("currEvent").item(0).getTextContent();
                time = eElement.getElementsByTagName("time").item(0).getTextContent();
                type = eElement.getElementsByTagName("type").item(0).getTextContent();
                status = eElement.getElementsByTagName("status").item(0).getTextContent();
                x[i] = new Unit(uniqID, callSign, defLocation, currEvent, time, type, status);
                unit.add(x[i]);
            }
        }
        return unit;
    }
}
  
