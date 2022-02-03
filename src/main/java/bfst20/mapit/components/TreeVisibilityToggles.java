package bfst20.mapit.components;

import javafx.fxml.FXML;
import javafx.scene.Node;
import bfst20.mapit.Model;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import bfst20.mapit.model.TreeType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.CheckBox;

public class TreeVisibilityToggles extends HBox {
    @FXML
    private ComboBox<TreeType> treesSelector;

    @FXML
    private CheckBox toggleCheckbox;

    @FXML
    private Button resetButton;

    private Model model;

    public TreeVisibilityToggles() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/TreeVisibilityToggles.fxml"));

        loader.setController(this);
        Node node = loader.load();

        this.getChildren().add(node);

        this.setVisible(false);
        this.managedProperty().bind(this.visibleProperty());
    }

    public void initialize(Model model) {
        this.model = model;

        for (TreeType treeType : TreeType.values()) {
            this.treesSelector.getItems().add(treeType);
        }

        this.treesSelector.setValue(TreeType.NORMALROADS);
        this.treesSelector.setOnAction(event -> this.changedTreeType());
        this.toggleCheckbox.setOnAction(event -> this.changedVisibility());
        this.resetButton.setOnAction(event -> this.resetVisibility());
    }

    private void changedTreeType() {
        TreeType treeType = this.treesSelector.getValue();

        this.toggleCheckbox.setSelected(
            this.model.typeSettings.isVisible(treeType)
        );
    }

    private void changedVisibility() {
        TreeType treeType = this.treesSelector.getValue();

        if (treeType != null) {
            this.model.typeSettings.changeVisibility(
                treeType, this.toggleCheckbox.isSelected()
            );

            this.model.notifyObservers();
        }
    }

    private void resetVisibility() {
        this.model.typeSettings.resetVisibility();

        this.model.notifyObservers();
    }

    public void toggle() {
        this.setVisible( ! this.isVisible());
    }

    public void hide() {
        this.setVisible(false);
    }
}