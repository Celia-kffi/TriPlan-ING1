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
        tableMoyenTransport.setEditable(true);
        colDistance.setEditable(true);

        colSelect.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        colSelect.setCellFactory(CheckBoxTableCell.forTableColumn(colSelect));

        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransport().getIdMoyenDestination()));
        colType.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTransport().getTypeTransports()));
        colFacteurEmission.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTransport().getFacteurEmission()));

        colDistance.setCellValueFactory(cellData -> cellData.getValue().distanceProperty());
        colDistance.setCellFactory(TextFieldTableCell.forTableColumn());

        colDistance.setOnEditCommit(event -> {
            TransportSelection ts = event.getRowValue();
            if (ts.selectedProperty().get()) {
                ts.distanceProperty().set(event.getNewValue());
            } else {
                showAlert(Alert.AlertType.WARNING, "Sélection non cochée", "Veuillez cocher la case avant de saisir une distance.");
                tableMoyenTransport.refresh();
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
        StringBuilder erreurs = new StringBuilder();

        for (TransportSelection ts : selectionList) {
            if (ts.selectedProperty().get()) {
                String distanceStr = ts.distanceProperty().get();
                if (distanceStr == null || distanceStr.trim().isEmpty()) {
                    erreurs.append("- ").append(ts.getTransport().getTypeTransports())
                            .append(" : distance manquante\n");
                    continue;
                }

                try {
                    float km = Float.parseFloat(distanceStr.trim());
                    total += ts.getTransport().getFacteurEmission() * km;
                } catch (NumberFormatException e) {
                    erreurs.append("- ").append(ts.getTransport().getTypeTransports())
                            .append(" : distance invalide\n");
                }
            }
        }

        if (erreurs.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Certains champs sont invalides", erreurs.toString());
        }

        if (total == 0) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune empreinte calculée", "Aucun moyen de transport valide sélectionné.");
            return;
        }

        if (empreinteCarboneController != null) {
            MoyenTransport synthese = new MoyenTransport("multiple", "Transports multiples", 1.0f);
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

        public BooleanProperty selectedProperty() {
            return selected;
        }

        public StringProperty distanceProperty() {
            return distance;
        }
    }
}
