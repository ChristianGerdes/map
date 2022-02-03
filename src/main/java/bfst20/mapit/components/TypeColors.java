package bfst20.mapit.components;

import javafx.fxml.FXML;
import javafx.scene.Node;
import bfst20.mapit.Model;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import bfst20.mapit.model.Type;
import javafx.scene.layout.HBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;

public class TypeColors extends HBox {
    @FXML
    protected ComboBox<Type> typeSelector;

    @FXML
    protected ColorPicker colorPicker;

    @FXML
    protected Button resetButton;

    private Model model;

    public TypeColors() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/TypeColors.fxml"));

        loader.setController(this);
        Node node = loader.load();

        this.getChildren().add(node);

        this.setVisible(false);
        this.managedProperty().bind(this.visibleProperty());
    }

    public void initialize(Model model) {
        this.model = model;

        for (Type type : Type.values()) {
            this.typeSelector.getItems().add(type);
        }

        this.typeSelector.setOnAction(event -> this.changedType());
        this.colorPicker.setOnAction(event -> this.changedColor());
        this.resetButton.setOnAction(event -> this.resetColor());
    }

    private void changedType() {
        Type type = this.typeSelector.getValue();

        if (model.typeSettings.getChangedColor(type) != null) {
            this.colorPicker.setValue(this.model.typeSettings.getChangedColor(type));
        } else {
            this.colorPicker.setValue(type.lightFillColor);
        }
    }

    private void changedColor() {
        Type type = this.typeSelector.getValue();

        if (type != null) {
            this.model.typeSettings.putChangedColor(type, this.colorPicker.getValue());

            this.model.notifyObservers();
        }
    }

    private void resetColor() {
        this.model.typeSettings.clearChangedColor();

        this.model.notifyObservers();
    }

    public void toggle() {
        this.setVisible( ! this.isVisible());
    }

    public void hide() {
        this.setVisible(false);
    }
}