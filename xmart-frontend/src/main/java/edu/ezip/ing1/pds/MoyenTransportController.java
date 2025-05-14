package edu.ezip.ing1.pds;

import javafx.scene.control.cell.PropertyValueFactory;
import edu.ezip.ing1.pds.business.dto.MoyenTransport;
import edu.ezip.ing1.pds.business.dto.MoyenTransports;
import edu.ezip.ing1.pds.client.commons.ConfigLoader;
import edu.ezip.ing1.pds.client.commons.NetworkConfig;
import edu.ezip.ing1.pds.services.MoyenTransportService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

public class MoyenTransportController {

    @FXML private TableView<TransportSelection> tableMoyenTransport;
    @FXML private TableColumn<TransportSelection, Boolean> colSelect;
    @FXML private TableColumn<TransportSelection, String> colId;
    @FXML private TableColumn<TransportSelection, String> colType;
    @FXML private TableColumn<TransportSelection, Double> colFacteurEmission;
    @FXML private TableColumn<TransportSelection, String> colDistance;

    private ObservableList<TransportSelection> selectionList = FXCollections.observableArrayList();
    private MoyenTransportService moyenTransportService;
    private EmpreinteCarboneController empreinteCarboneController;

    public void setEmpreinteCarboneController(EmpreinteCarboneController controller) {
        this.empreinteCarboneController = controller;
    }

    @FXML
    public void initialize() {
        try {
            NetworkConfig config = ConfigLoader.loadConfig(NetworkConfig.class, "network.yaml");
            moyenTransportService = new MoyenTransportService(config);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de configuration", e.getMessage());
            return;
        }

        initTableColumns();
        loadMoyensTransports();
    }

    private void initTableColumns() {
        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransport().getIdMoyenDestination()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransport().getTypeTransports()));
        colFacteurEmission.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTransport().getFacteurEmission()));

        colDistance.setCellValueFactory(cellData -> cellData.getValue().distanceProperty());
        colDistance.setCellFactory(TextFieldTableCell.forTableColumn());


        colDistance.setOnEditCommit(event -> {
            TransportSelection ts = event.getRowValue();
            if (!ts.selectedProperty().get()) { // Si la case n'est pas cochée
                event.consume(); // Empêche l'édition de la distance
                showAlert(Alert.AlertType.WARNING, "Sélection non cochée", "Veuillez cocher la case avant de saisir une distance.");
            }
        });

        tableMoyenTransport.setItems(selectionList);
    }

    private void loadMoyensTransports() {
        selectionList.clear();
        try {
            MoyenTransports response = moyenTransportService.selectTransport();
            if (response != null && response.getMoyenTransports() != null) {
                for (MoyenTransport m : response.getMoyenTransports()) {
                    selectionList.add(new TransportSelection(m));
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur chargement", e.getMessage());
        }
    }

    @FXML
    private void onValider() {
        float total = 0;
        for (TransportSelection ts : selectionList) {
            if (ts.selectedProperty().get()) {
                try {
                    float km = Float.parseFloat(ts.distanceProperty().get());
                    total += ts.getTransport().getFacteurEmission() * km;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Distance invalide pour " + ts.getTransport().getTypeTransports());
                    return;
                }
            }
        }

        if (empreinteCarboneController != null) {
            MoyenTransport synthese = new MoyenTransport("multiple", "Transports multiples", 1.0f); // Dummy
            empreinteCarboneController.setTransportEtDistance(synthese, total);
        }

        ((Stage) tableMoyenTransport.getScene().getWindow()).close();
    }

    private void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public class TransportSelection {
        private MoyenTransport transport;
        private BooleanProperty selected = new SimpleBooleanProperty(false);
        private StringProperty distance = new SimpleStringProperty("");

        public TransportSelection(MoyenTransport transport) {
            this.transport = transport;
        }

        public MoyenTransport getTransport() {
            return transport;
        }

        public StringProperty idMoyenDestinationProperty() {
            return new SimpleStringProperty(transport.getIdMoyenDestination());
        }

        public StringProperty typeTransportsProperty() {
            return new SimpleStringProperty(transport.getTypeTransports());
        }

        public DoubleProperty facteurEmissionProperty() {
            return new SimpleDoubleProperty(transport.getFacteurEmission());
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public StringProperty distanceProperty() {
            return distance;
        }
    }
}
