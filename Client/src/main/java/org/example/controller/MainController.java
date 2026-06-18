package org.example.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.IObserver;
import org.example.IService;
import org.example.Joc;
import org.example.Status;
import org.example.dto.ClasamentDTO;
import org.example.dto.RezultatIncercareDTO;

import java.util.List;

public class MainController implements IObserver {
    private static final Logger logger = LogManager.getLogger();

    @FXML private HBox tablaJoc;
    @FXML private TableView<ClasamentDTO> tabelClasament;
    @FXML private TableColumn<ClasamentDTO, String> colAlias;
    @FXML private TableColumn<ClasamentDTO, String> colData;
    @FXML private TableColumn<ClasamentDTO, Integer> colPuncte; // Coloană nouă pentru punctaj

    private IService service;
    private Joc jocCurent;
    private ObservableList<ClasamentDTO> modelClasament = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colAlias.setCellValueFactory(new PropertyValueFactory<>("alias"));
        colData.setCellValueFactory(new PropertyValueFactory<>("data"));
        colPuncte.setCellValueFactory(new PropertyValueFactory<>("nrPuncte"));

        tabelClasament.setItems(modelClasament);
    }

    public void setService(IService service) {
        this.service = service;
    }

    public void setJoc(Joc joc) {
        this.jocCurent = joc;
        deseneazaTabla();
    }

    private void deseneazaTabla() {
        tablaJoc.getChildren().clear();

        String multimi = jocCurent.getConfiguratie().getMultimi();
        String[] perechi = multimi.split(";");

        for (String pereche : perechi) {
            String[] elemente = pereche.split(",");
            String litera = elemente[0];
            String numar = elemente[1];

            Button btnLitera = new Button(litera + " (" + numar + ")");
            btnLitera.setPrefSize(120, 100);
            btnLitera.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-cursor: hand;");

            btnLitera.setOnAction(event -> handleMutare(pereche, btnLitera));

            tablaJoc.getChildren().add(btnLitera);
        }
    }

    private void handleMutare(String pereche, Button btnApasat) {
        btnApasat.setDisable(true);

        new Thread(() -> {
            try {
                RezultatIncercareDTO rezultat = service.faIncercare(jocCurent.getId(), pereche);

                Platform.runLater(() -> {
                    String alegereServer = rezultat.getPereche();
                    String literaServer = alegereServer.split(",")[0];
                    String numarServer = alegereServer.split(",")[1];

                    if (rezultat.getStatus() == Status.Nefinalizat) {
                        btnApasat.setStyle("-fx-background-color: lightblue; -fx-font-size: 18px;");
                        arataMesaj("Runda completă", "Tu ai ales: " + pereche + "\nServerul a ales: " + literaServer + " (" + numarServer + ")");
                    }
                    else if (rezultat.getStatus() == Status.Finalizat) {
                        btnApasat.setStyle("-fx-background-color: lightgreen; -fx-font-size: 18px;");
                        optesteJocul();

                        service.finalizeazaJoc();

                        arataMesaj("Joc Finalizat", "Tu ai ales: " + pereche + "\nServerul a ales: " + literaServer + " (" + numarServer + ")\n\nJocul s-a încheiat! Verifică clasamentul pentru scorul final.");
                    }
                });

            } catch (Exception e) {
                logger.error("Eroare la trimiterea mutării", e);
                Platform.runLater(() -> {
                    arataMesaj("Eroare", "S-a pierdut conexiunea cu serverul!");
                    btnApasat.setDisable(false);
                });
            }
        }).start();
    }

    private void optesteJocul() {
        tablaJoc.getChildren().forEach(node -> node.setDisable(true));
    }

    private void arataMesaj(String titlu, String mesaj) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titlu);
        alert.setHeaderText(null);
        alert.setContentText(mesaj);
        alert.show();
    }

    @Override
    public void update(List<ClasamentDTO> noulClasament) {
        Platform.runLater(() -> {
            modelClasament.setAll(noulClasament);
            logger.info("Tabelul de clasament a fost actualizat pe UI.");
        });
    }
}