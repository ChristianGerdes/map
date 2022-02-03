package bfst20.mapit.controller;

import javafx.scene.Node;
import javafx.util.Duration;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import bfst20.mapit.model.io.IconsLoader;
import javafx.animation.TranslateTransition;

public class SidebarAnimationController {
    boolean sideMenuOpen;
    private Button hideButton;
    private AnchorPane sidebar_container;
    private Pane debugBox;
    private VBox scale_ruler_container;

    public SidebarAnimationController(Button hideButton, AnchorPane sidebar_container, Pane debugBox, VBox scale_ruler_container) {
        this.hideButton = hideButton;
        this.sidebar_container = sidebar_container;
        this.sideMenuOpen = true;
        this.debugBox = debugBox;
        this.scale_ruler_container = scale_ruler_container;
    }

    //Calls the element which needs to be translate on the x-axies when the sidebar is closed or expanded
    public void slideAnimation() {
        slideAnimation(sidebar_container);
        slideAnimation(hideButton);
        slideAnimation(debugBox);
        slideAnimation(scale_ruler_container);
        sideMenuOpen = !sideMenuOpen;
        setNavbarIcon();
    }

    private void slideAnimation(Node node) {
        TranslateTransition transition = new TranslateTransition();
        transition.setDuration(Duration.millis(350));
        transition.setNode(node);
        if (sideMenuOpen) {
            transition.setFromX(0);
            transition.setToX(-370);
        } else {
            transition.setFromX(-370);
            transition.setToX(0);
        }
        transition.play();
        transition = null;
    }

    //Sets the icon for hide navbar button
    private void setNavbarIcon() {
        ImageView imageView;
        if (sideMenuOpen)
            imageView = new ImageView(IconsLoader.getInstance().getIcon("navbar_icon_left", "UI", "svg"));
        else
            imageView = new ImageView(IconsLoader.getInstance().getIcon("navbar_icon_right", "UI", "svg"));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        hideButton.setGraphic(imageView);
    }
}