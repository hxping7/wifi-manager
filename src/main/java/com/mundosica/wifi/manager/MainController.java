/*
 * Licencia MIT
 *
 * Copyright (c) 2017 @Fitorec <chanerec at gmail.com>.
 *
 * Se concede permiso, de forma gratuita, a cualquier persona que obtenga una
 * copia de este software y de los archivos de documentación asociados
 * (el "Software"), para utilizar el Software sin restricción, incluyendo sin
 * limitación los derechos a usar, copiar, modificar, fusionar, publicar,
 * distribuir, sublicenciar, y/o vender copias del Software, y a permitir a las
 * personas a las que se les proporcione el Software a hacer lo mismo, sujeto a
 * las siguientes condiciones:
 *
 * El aviso de copyright anterior y este aviso de permiso se incluirán en todas
 * las copias o partes sustanciales del Software.
 *
 * EL SOFTWARE SE PROPORCIONA "TAL CUAL", SIN GARANTÍA DE NINGÚN TIPO, EXPRESA O
 * IMPLÍCITA, INCLUYENDO PERO NO LIMITADO A GARANTÍAS DE COMERCIALIZACIÓN,
 * IDONEIDAD PARA UN PROPÓSITO PARTICULAR Y NO INFRACCIÓN. EN NINGÚN CASO LOS
 * AUTORES O TITULARES DEL COPYRIGHT SERÁN RESPONSABLES DE NINGUNA RECLAMACIÓN,
 * DAÑOS U OTRAS RESPONSABILIDADES, YA SEA EN UNA ACCIÓN DE CONTRATO, AGRAVIO O
 * CUALQUIER OTRO MOTIVO, QUE SURJA DE O EN CONEXIÓN CON EL SOFTWARE O EL USO U
 * OTRO TIPO DE ACCIONES EN EL SOFTWARE.
 *
 */
package main.java.com.mundosica.wifi.manager;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import main.java.com.mundosica.wifi.manager.Model.Config;
import main.java.com.mundosica.wifi.manager.Model.Profile;

/**
 *
 * @author @Fitorec <chanerec at gmail.com>
 */
public class MainController implements Initializable {

    @FXML
    private TableView tableProfiles;
    @FXML
    private TableColumn columnSttus;
    @FXML
    private TableColumn columnName;
    @FXML
    private TableColumn columnMode;
    @FXML
    private TableColumn columnAuth;
    @FXML
    private TableColumn columnKey;
    @FXML
    private MenuButton buscarType;
    @FXML
    private TextField buscarField;

    private static final ExtensionFilter filterXml = new ExtensionFilter("Archivo de Configuración", "*.xml");

    private Profile currentProfile() {
        return (Profile) tableProfiles.getSelectionModel().getSelectedItem();
    }

    /**
     *
     * @param e
     */
    @FXML
    public void importar(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar Configuración");
        fileChooser.getExtensionFilters().add(filterXml);
        //fileChooser.setInitialFileName(p.);
        File selectedFile = fileChooser.showOpenDialog(WifiManager.stage);
    }

    public void exportar() {
        Profile p = currentProfile();
        FileChooser fileChooser = new FileChooser();
        System.out.println("Directorio: " + Config.getSavePath());
        fileChooser.setTitle("Exportar red " + p.getName());
        fileChooser.getExtensionFilters().add(filterXml);
        fileChooser.setInitialDirectory(new File(Config.getSavePath()));
        fileChooser.setInitialFileName(p.getFileName());
        File selectedFile = fileChooser.showSaveDialog(WifiManager.stage);
        if (selectedFile != null) {
            Profile.export(p, selectedFile.getAbsolutePath());
        }
    }

    /**
     *
     * @param ke
     */
    @FXML
    public void buscar(KeyEvent ke) {
        String search = buscarField.getText();
        String type = "";
        if (search.length() == 0) {
            this.tableProfiles.getItems().clear();
            this.tableProfiles.setItems(Profile.list());
        }
        this.tableProfiles.getItems().clear();
        this.tableProfiles.setItems(Profile.search(search, type));
        System.out.println(search);
    }
    /**
     *
     * @param ke
     */
    @FXML
    public void keyOnTable(KeyEvent ke) {
        KeyCode code = ke.getCode();
        if (code.toString() == "DELETE") {
            Alert alert = new Alert(AlertType.WARNING, "¿Estas seguro de borrar?", ButtonType.YES, ButtonType.CANCEL);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                Profile p = (Profile) tableProfiles.getSelectionModel().getSelectedItem();
                NetshWlan.delete(p);
                this.tableProfiles.getItems().clear();
                this.tableProfiles.setItems(Profile.remove(p));
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tableProfiles.setEditable(true);
        columnName.setCellValueFactory(
            new PropertyValueFactory<Profile, String>("name")
        );
        columnMode.setCellValueFactory(
            new PropertyValueFactory<Profile, String>("connectionMode")
        );
        columnAuth.setCellValueFactory(
            new PropertyValueFactory<Profile, String>("authentication")
        );
        columnKey.setCellFactory(TextFieldTableCell.forTableColumn());
        columnKey.setOnEditCommit(
            new EventHandler<CellEditEvent<Profile, String>>() {
                @Override
                public void handle(CellEditEvent<Profile, String> t) {
                    /*t.getOldValue();
                    t.getNewValue();*/
                }
            }
        );
        columnKey.setCellValueFactory(
            new PropertyValueFactory<Profile, String>("keyMaterial")
        );
        Profile.loadList();
        this.tableProfiles.getItems().clear();
        this.tableProfiles.setItems(Profile.list());

        /// Menu
        ContextMenu cm = new ContextMenu();
        MenuItem mi1 = new MenuItem("Exportar");
        mi1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                exportar();
            }
        });
        cm.getItems().add(mi1);
        MenuItem mi2 = new MenuItem("Compartir");
        cm.getItems().add(mi2);
        MenuItem mi3 = new MenuItem("Eliminar");
        cm.getItems().add(mi3);
        tableProfiles.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            if (t.getButton() == MouseButton.SECONDARY) {
                cm.show(tableProfiles , t.getScreenX() , t.getScreenY());
                Profile prof = (Profile) tableProfiles.getSelectionModel().getSelectedItem();
                System.out.println("Profile: " + prof.getName());
            }
        }
        // buscar
    });
        //buscarType.se
    }

}