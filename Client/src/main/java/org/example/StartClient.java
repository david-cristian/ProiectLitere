package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controller.LoginController;

public class StartClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        IService service = new ServiceProxy("localhost", 5555);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setService(service);

        Scene scene = new Scene(root, 700, 500);
        primaryStage.setTitle("Gaseste Pozitia! - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}