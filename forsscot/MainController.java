/*
    MainController.java
    Author: Scott Forsyth
    Date: April 13th, 2020

    Description
    The main controller for the Wine GUI application. Contains the
    WineController and the WineTableViewController as datamembers.
 */

/**
 * @author Scott Forsyth
 */
package forsscot;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import models.Wine;

public class MainController implements Initializable {

    @FXML private Button btnSave;
    @FXML private Menu mnuFile;
    @FXML private MenuItem itemExit;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    
    @FXML private WineController pnlWineController;
    @FXML private WineTableViewController pnlViewController;
    
    /* 
    * Declare as constants the length of the String fields in characters and the 
    * length of the record in bytes
    */
    final int FIELD_SIZE = 15;
    final long RECORD_SIZE = 80;
    
    // END of Part 1
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        // show record that is selected in the tables view into the pnlWine view
        pnlViewController.getTableView().setOnMouseClicked
        (eh -> showWineRecord());
    }
    
    /**
     * This methods gets the wine record from the row selected in the TableView
     * and displays it in the other view in each text field
     */
    private void showWineRecord() {
        btnUpdate.setDisable(false);
        btnDelete.setDisable(false);
        Wine selectedWine = pnlViewController.getSelectedWine();
        pnlWineController.getTxtWineID().setText
        (String.valueOf(selectedWine.getWineID()));
        pnlWineController.getTxtEstate().setText(selectedWine.getEstate());
        pnlWineController.getTxtGrape().setText(selectedWine.getGrape());
        pnlWineController.getTxtYear().setText
        (String.valueOf(selectedWine.getYear()));
        pnlWineController.getTxtQuantity().setText
        (String.valueOf(selectedWine.getQuantity()));
        pnlWineController.getTxtPrice().setText
        (String.valueOf(selectedWine.getPrice()));
    }

    /**
     * Call writeRecord() method to write a Wine record on to the file
     */
    @FXML
    private void addNew(ActionEvent event) throws FileNotFoundException, IOException {
        writeRecord();
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
    }

    /**
     * Method for writing a single wine record in the file. It uses writeWine()
     * to do the actual writing
     */
    private void writeRecord() throws FileNotFoundException, IOException {
        
        File f = new File("src/res/wines.dat");
        
         RandomAccessFile raf = new RandomAccessFile(f, "rw");
         int wineID = (int)((raf.length()/RECORD_SIZE));
         try{
         Wine wine = this.readTextFields();
         wine.setWineID(wineID);
         this.writeWine(raf, wine);
         pnlViewController.getObservableList().add(wine);
         pnlViewController.getTableView().setItems
        (pnlViewController.getObservableList());
         }catch(Exception e){
             this.showAlert("error", "Input error: "
                     + "Please check the field values");
         }     

    }
    
    /**
     * This method is used by other methods to write a wine record on the file
     */
    private void writeWine(RandomAccessFile raf, Wine w) throws IOException {             
        String estate = this.prepStringField(w.getEstate(), 15);
        String grape = this.prepStringField(w.getGrape(), 15);
        int id = w.getWineID();
        int year = w.getYear();
        int quantity = w.getQuantity();
        double price = w.getPrice();
       raf.seek(RECORD_SIZE * w.getWineID());
        raf.writeInt(id);
        raf.writeChars(estate);
        raf.writeChars(grape);
        raf.writeInt(year);
        raf.writeInt(quantity);
        raf.writeDouble(price);
    }

    /**
     * Method for reading the data from the text fields and creating a wine
     * object without wineID
     * @return a wine object that contains the values from the text fields as
     * properties.
     */
    private Wine readTextFields() {
        /* 
        *  Get values from all of the textfields (excluding txtWineID), create
        *  a wineobject and return it
        */
    
       String estate = pnlWineController.getTxtEstate().getText();
       String grape = pnlWineController.getTxtGrape().getText();
       int year = Integer.parseInt(pnlWineController.getTxtYear().getText());
       int quantity = Integer.parseInt
        (pnlWineController.getTxtQuantity().getText());
       double price = Double.parseDouble
        (pnlWineController.getTxtPrice().getText());
       Wine wine = new Wine(estate, grape, year, quantity, price);

        return wine; // to be replaced with the actual return
        //END of Part 4
    }

    /**
     * Method for displaying different alerts. 
     * @param alertType the type of alert (error, confirmation or information)
     * @param message the massage the alert will display
     * @return the alert object to the caller
     */
    private ButtonType showAlert(String alertType, String message) {
        Alert alert = null;
        Optional<ButtonType> result = null;
        if (alertType.equalsIgnoreCase("error")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if (alertType.equalsIgnoreCase("confirmation")) {
            alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Are you sure you wish to exit?", 
                    ButtonType.YES, ButtonType.NO);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setTitle(alertType + " Dialog");
        alert.setHeaderText(null);
        alert.setContentText(message);
        result = alert.showAndWait();
        return result.get();
    }

    /**
     * This method is use by writeWine() method to make sure that all strings
     * are of the same size
     * @param value the value of the string to be prepared
     * @param size the number of the characters the string must have
     * @return the string value that was passed as parameter, with added spaced
     * at the end if the string that was passed was less than the size, or a
     * truncated string if it was longer that size
     */
    private String prepStringField(String value, int size) {
        if (value.length() < size) {
            int numSpaces = size - value.length();
            for (int i = 0; i < numSpaces; i++) {
                value += " ";
            }
        } else {
            value = value.substring(0, size);
        }
        return value;
    }
    /**
     * Updates a record in the list and the wines.dat file according to the
     * user input.
     * 
     * @param event when the update button is pressed
     * @throws FileNotFoundException when the file is not found
     * @throws IOException if there is a problem with input and output
     */
    @FXML
    private void updateRecord(ActionEvent event) 
            throws FileNotFoundException, IOException {
        
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        File f = new File("src/res/wines.dat");
        
    
     
       int wineID = Integer.parseInt
        (pnlWineController.getTxtWineID().getText());
       Wine wine = this.readTextFields();
       wine.setWineID(wineID);
       RandomAccessFile raf = new RandomAccessFile(f, "rw");
      
        // moving the pointer in the file to the correct record based on id
        raf.seek((RECORD_SIZE * wineID));
        this.writeWine(raf, wine);
        raf.close();
        pnlViewController.getObservableList().set(wineID, wine);
        pnlViewController.getTableView().setItems
        (pnlViewController.getObservableList());   
    }
    
    /**
     * This method will delete the last record in the file
     * 
     */
    @FXML
    private void deleteRecord(ActionEvent event) 
            throws FileNotFoundException, IOException {
        
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        int id = Integer.parseInt(pnlWineController.getTxtWineID().getText());
        Wine w = readTextFields();
        w.setWineID(id);
        File f = new File("src/res/wines.dat");
        
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        raf.setLength((raf.length() - RECORD_SIZE));
        int length = pnlViewController.getObservableList().size();
        pnlViewController.getObservableList().remove(length - 1);
        pnlViewController.getTableView().setItems
        (pnlViewController.getObservableList());
    }
    
    
    /**
     * Displays a confirmation alert box, asking the user if they want to leave
     * the program. If yes is selected, the program exits.
     * 
     * @param event the exit button from the menubar is selected
     */
    @FXML
    private void exit(ActionEvent event) {
        /* 
        *  Use the showAlert() method to confirm before closing the application 
        */              
       ButtonType choice =  this.showAlert
        ("confirmation", "are you sure you want to exit?");
       if (choice.equals(ButtonType.YES)){
           System.exit(0);
       }             
    }
    
    /**
     * Opens the readme.txt file as an external file
     * 
     * @param event the about button is selected from the menubar.
     * @throws IOException if there is a problem opening the file
     */
    @FXML
    private void aboutHandler(ActionEvent event) throws IOException {
        /* 
        *  Write the code that will open readme.txt file as an external file
        *  using your default text editor such as notpad or vi.
        */
        File readme = new File("src/res/readme.txt");
        Desktop.getDesktop().edit(readme);
    }
}
