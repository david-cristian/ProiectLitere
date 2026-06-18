package org.example.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.IService;
import org.example.Joc;

public class LoginController {
    @FXML private TextField aliasField;
    @FXML private Button loginButton;

    private IService service;
    private static final Logger logger= LogManager.getLogger();

    public void setService(IService service){
        this.service = service;
    }
    public LoginController() {
    }

    @FXML
    public void handleLogin() {
        String alias = aliasField.getText();

        if (alias.isEmpty()) {
            showError("Date Invalide", "Campul nu poate fi gol!");
            return;
        }

        loginButton.setDisable(true);
        loginButton.setText("Se incarca...");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setService(service);

            new Thread(() -> {
                try {
                    Joc joc = service.incepeJoc(alias, mainController);
                    Platform.runLater(() -> {
                        mainController.setJoc(joc);
                        logger.info("Jocul pentru jucatorul {} a inceput", alias);

                        Stage stage = (Stage) aliasField.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Gaseste Pozitia " + alias + "!");
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        logger.error(e.getMessage());
                        loginButton.setDisable(false);
                        loginButton.setText("Login");
                        showError("Eroare Autentificare", "Alias ul nu exista!");
                    });
                }
            }).start();
        } catch (Exception e) {
            logger.error(e.getMessage());
            showError("Eroare", "Nu s-a putut incarca fereastra principala");
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "A aparut o eroare!");
        alert.show();
    }
}
