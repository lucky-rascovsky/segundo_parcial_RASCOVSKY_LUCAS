/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package tiendafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class TiendaFX extends Application {

     @Override
    public void start(Stage stage) throws Exception {
        
        Parent root = FXMLLoader.load(getClass().getResource("/vista/TiendaView.fxml"));
        Scene scene= new Scene(root);
        stage.setTitle("Sistema de Ventas");
        stage.setScene(scene);
        
        stage.show();
    }
    
    
    public static void main(String[] args) {
        Application.launch();
    }
    
}
