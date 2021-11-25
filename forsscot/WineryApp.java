/*
    Name:  Scott Forsyth
    Date: April 13th, 2020
    
    Description:
    This class launches the GUI for the winery application
*/
package forsscot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Scott Forsyth
 */
public class WineryApp extends Application{
    
    public static void main(String[] args) {
        launch(args);        
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));
        stage.setTitle("Winery Application");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
