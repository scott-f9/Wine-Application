/*
    MainController.java
    Author: Scott Forsyth
    Date: April 13th, 2020

    Description
    This class controls the wine TableView in the Winery GUI application
 */
package forsscot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Wine;

public class WineTableViewController implements Initializable {

    @FXML
    private TableView<Wine> tblWines;
    @FXML
    private TableColumn<Wine, Integer> idCol;
    @FXML
    private TableColumn<Wine, String> estCol;
    @FXML
    private TableColumn<Wine, String> grapeCol;
    @FXML
    private TableColumn<Wine, Integer> yearCol;
    @FXML
    private TableColumn<Wine, Integer> qtyCol;
    @FXML
    private TableColumn<Wine, Double> priceCol;

    private ObservableList<Wine> olWines;

    final int FIELD_SIZE = 15;
    final long RECORD_SIZE = 80;

     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idCol.setCellValueFactory(new PropertyValueFactory<>("wineID"));
        estCol.setCellValueFactory(new PropertyValueFactory<>("estate"));
        grapeCol.setCellValueFactory(new PropertyValueFactory<>("grape"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        try {
            //load values from file here
            loadWines();
        } catch (FileNotFoundException ex) {

        } catch (IOException ex) {

        }
    }

    /**
     * Method for loading the content of the file on to the TableView
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void loadWines() throws FileNotFoundException, IOException {
        olWines = FXCollections.observableArrayList();
        File f = new File("src/res/wines.dat");
        RandomAccessFile raf = new RandomAccessFile(f, "r");
        raf.seek(0);
        long num = raf.length() / RECORD_SIZE;
        for (int i = 0; i < num; i++) {
            // loading the table from the values in the file
            int id = raf.readInt();
            String estate = readString(raf, FIELD_SIZE);
            String grape = readString(raf, FIELD_SIZE);
            int year = raf.readInt();
            int quantity = raf.readInt();
            double price = raf.readDouble();
            String trimEstate = estate.trim();
            String trimGrape = grape.trim();
            Wine wine = new Wine(trimEstate, trimGrape, year, quantity, price);
            wine.setWineID(id);
            olWines.add(wine);
        }
        tblWines.setItems(olWines);
    }

    /**
     * Method for reading a String of a length size using a RandomAccessFile
     * object that is passed as a parameter. It reads the string one character
     * at a time and concatinates them into a string that is returned back
     *
     * @param raf RandomAccessFile object
     * @param size the length od the string that needs to be read
     * @return the String that was read from the file
     * @throws IOException throws back the IOExceptin thrown by readChar()
     * method
     */
    private String readString(RandomAccessFile raf, int size) throws IOException {
        String s = "";
        for (int i = 0; i < size; i++) {
            s += String.valueOf(raf.readChar());
        }
        return s; 
    }

    /**
     * Accessor method for getting the item (row) that is selected in the
     * TableView and return the wine object that correspond to that table row.
     * This method can be used in other controller to find out which row/wine
     * was selected
     *
     * @return the wine object represented by the tableView selection
     */
    public Wine getSelectedWine() {
        Wine w = tblWines.getSelectionModel().getSelectedItem();
        return w;
    }

    /**
     * Accessor for getting the observable list that is used by the TableView.
     * This method can be used in other controllers
     *
     * @return the ObservableList with Wine objects used in the TableView
     */
    public ObservableList<Wine> getObservableList() {
        return olWines;
    }

    /**
     * Accessor for getting the TableView from other classes (controllers)
     *
     * @return the TableView object used in this controller
     */
    public TableView<Wine> getTableView() {
        return tblWines;
    }
}
