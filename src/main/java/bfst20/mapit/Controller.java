package bfst20.mapit;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import javax.xml.stream.XMLStreamException;

import bfst20.mapit.components.TreeVisibilityToggles;
import bfst20.mapit.components.TypeColors;
import bfst20.mapit.controller.DAWAAddressController;
import bfst20.mapit.controller.DebugController;
import bfst20.mapit.controller.DirectionsController;
import bfst20.mapit.controller.POIController;
import bfst20.mapit.controller.Search;
import bfst20.mapit.controller.SidebarAnimationController;
import bfst20.mapit.controller.StylesheetController;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.model.CursorType;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.directions.RouteType;
import bfst20.mapit.model.directions.VehicleType;
import bfst20.mapit.model.drawables.debug.IntersectionNode;
import bfst20.mapit.model.drawables.points.NavPoint;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.io.IconsLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Controller {
    private DebugController debugController;

    private Model model;

    private Point lastMouse;

    private boolean isDragging = false;

    private DirectionsController directionsController;

    private Search search;

    private POIController poiController;

    private StylesheetController stylesheetController;

    private SidebarAnimationController sidebarAnimationController;

    private CursorType cursorType;

    @FXML
    protected MapCanvas mapCanvas;

    @FXML
    protected TypeColors typeColors;

    @FXML
    protected TreeVisibilityToggles treeVisibilityToggles;

    @FXML
    protected Label measurement_label, roadName, navigationErrorLabel, directionTotalDistanceLabel;

    @FXML
    protected HBox routeOptions;

    @FXML
    protected VBox scale_ruler_container, points, directions, settingsMenu, directionsBox;

    @FXML
    protected Button loadButton, saveButton, toggleSidebarButton, addPOIButton, navigateButton,
    toggleColorMenuButton, toggleDrawableMenuButton, startPointButton, endPointButton,
    colorModeButton, resetNavigationButton, toggleSettingsButton, swapOriginDestinationButton,
    carVehicleButton, bikeVehicleButton, walkVehicleButton;

    @FXML
    protected RadioButton fastestRouteButton, shortestRouteButton;

    @FXML
    protected TextField searchInput;

    @FXML
    protected AnchorPane sidebar_container;

    @FXML
    protected Pane debugBox;

    @FXML
    protected TextField navInputFrom, navInputTo;

    @FXML
    public CheckBox poiToggleCheckbox;

    public void initialize(final Model model, final Scene scene) {
        this.model = model;

        this.mapCanvas.initialize(model, this.debugBox);
        this.typeColors.initialize(model);
        this.treeVisibilityToggles.initialize(model);

        this.search = new Search(model, navInputFrom, navInputTo);

        new DAWAAddressController(searchInput, navInputFrom, navInputTo, model, search, mapCanvas);

        this.debugController = new DebugController(debugBox, model, scene);
        this.directionsController = new DirectionsController(model, directions, directionTotalDistanceLabel, mapCanvas);
        this.poiController = new POIController(model, mapCanvas, points, measurement_label);
        this.stylesheetController = new StylesheetController(model, scene);
        this.sidebarAnimationController = new SidebarAnimationController(toggleSidebarButton, sidebar_container, debugBox, scale_ruler_container);

        model.getMeasurementLabelModel().measureDistance(mapCanvas, measurement_label);

        // Hiding settings menu
        settingsMenu.setVisible(false);
        settingsMenu.managedProperty().bind(settingsMenu.visibleProperty());

        // Determine if route types should show
        this.routeOptions.setVisible(this.model.getSearchModel().getVehicleType() == VehicleType.CAR);
        this.routeOptions.managedProperty().bind(this.routeOptions.visibleProperty());
        this.selectRouteType();

        // Hide navigation error label
        this.navigationErrorLabel.setVisible(false);
        this.navigationErrorLabel.managedProperty().bind(this.navigationErrorLabel.visibleProperty());

        // Hide directions
        directionsBox.setVisible(false);
        directionsBox.managedProperty().bind(directionsBox.visibleProperty());

        // Register events and listeners
        mapCanvas.setOnMouseMoved(event -> this.setOnMouseMoved(event));
        mapCanvas.setOnMouseDragged(event -> this.setMouseDragged(event));
        mapCanvas.setOnMouseClicked(event -> this.canvasClicked(event));
        mapCanvas.setOnMousePressed(event -> this.setMousePressed(event));
        mapCanvas.setOnMouseReleased(event -> this.setMouseReleased(event));
        mapCanvas.setOnScroll(event -> this.setScrolled(event));

        loadButton.setOnAction(event -> this.openFileLoadDialog());
        saveButton.setOnAction(event -> this.openFileSaveDialog());

        startPointButton.setOnMouseClicked(event -> this.changeCursorType(event, CursorType.STARTPOINT));
        endPointButton.setOnMouseClicked(event -> this.changeCursorType(event, CursorType.ENDPOINT));
        swapOriginDestinationButton.setOnAction(event -> this.swapOriginDestination());

        toggleSettingsButton.setOnAction(event -> this.toggleSettingsButtonClicked());
        colorModeButton.setOnAction(event -> this.toggleColorMode(event));
        toggleSidebarButton.setOnAction(event -> this.toggleSidebar(event));
        toggleColorMenuButton.setOnAction(event -> this.toggleTypeColors());
        toggleDrawableMenuButton.setOnAction(event -> this.toggleTreeVisibilityToggles());

        navigateButton.setOnAction(event -> this.navigationButtonClicked());
        resetNavigationButton.setOnAction(event -> this.resetNavigation());

        carVehicleButton.setOnAction(event -> this.setVehicleType(VehicleType.CAR));
        bikeVehicleButton.setOnAction(event -> this.setVehicleType(VehicleType.BIKE));
        walkVehicleButton.setOnAction(event -> this.setVehicleType(VehicleType.PEDESTRIAN));

        fastestRouteButton.setOnAction(event -> this.search.setRouteType(RouteType.FASTEST));
        shortestRouteButton.setOnAction(event -> this.search.setRouteType(RouteType.SHORTEST));

        // Cursors
        addPOIButton.setOnMouseClicked(event -> this.changeCursorType(event, CursorType.POI));

        // POI
        poiToggleCheckbox.setOnAction(event -> {
            model.getPOIModel().setVisibility(poiToggleCheckbox.isSelected());
			model.notifyObservers();
        });
    }

    private void changeCursorType(final MouseEvent event, final CursorType cursorType) {
        this.cursorType = cursorType;

        switch (cursorType) {
            case POI:
                model.getPOIModel().invertPlacingPOI();
                break;
            case STARTPOINT:
                search.swapStartPoint();
                break;
            case ENDPOINT:
                search.swapEndPoint();
                break;
            default:
                break;
        }

        final Image cursorImg = IconsLoader.getInstance().getCursorIcon(cursorType.icon, "UI");
        final Point mc = mapCanvas.toModelCoords(event.getX(), event.getY());

        this.mapCanvas.setCursor(new ImageCursor(cursorImg, mc.getX() + (cursorImg.getWidth() / 2), cursorImg.getHeight()));
    }

    private void navigationButtonClicked() {
        this.navigationErrorLabel.setVisible(false);

        try {
            this.model.navigate();

            this.directionsController.printDirections();
            this.directionsBox.setVisible(true);

            this.model.notifyObservers();
		} catch (final Exception e) {
            this.navigationErrorLabel.setText(e.getMessage());
            this.navigationErrorLabel.setVisible(true);
		}
    }

    private void selectRouteType() {
        final RadioButton button = this.model.getSearchModel().getRouteType() == RouteType.FASTEST
            ? this.fastestRouteButton : this.shortestRouteButton;

        button.setSelected(true);
    }

    private void setMouseReleased(final MouseEvent mouse) {
		if(isDragging) {
			isDragging=false;
        } else if(!model.getPOIModel().isPlacingPOI() && !model.getSearchModel().settingEndPoint
                    && !model.getSearchModel().settingStartPoint)  {
            final Point mc = mapCanvas.toModelCoords(mouse.getX(), mouse.getY());
            final Image icon = IconsLoader.getInstance().getIcon("pin", "UI", "svg");
            this.model.getPOIModel().setPoiPoint(new NavPoint(mc.getX(), mc.getY(), icon));
            this.mapCanvas.repaint();
        }
    }

    private void setVehicleType(final VehicleType type) {
        this.search.setVehicleType(type);
        this.selectRouteType();

        this.carVehicleButton.getStyleClass().remove("btn-active");
        this.bikeVehicleButton.getStyleClass().remove("btn-active");
        this.walkVehicleButton.getStyleClass().remove("btn-active");

        switch (type) {
            case CAR:
                this.carVehicleButton.getStyleClass().add("btn-active");
                break;
            case BIKE:
                this.bikeVehicleButton.getStyleClass().add("btn-active");
                break;
            default:
                this.walkVehicleButton.getStyleClass().add("btn-active");
                break;
        }

        routeOptions.setVisible(type == VehicleType.CAR);
    }

    private void canvasClicked(final MouseEvent mouse) {
        if (this.cursorType == CursorType.NORMAL) {
            return;
        }

        final Point mc = mapCanvas.toModelCoords(mouse.getX(), mouse.getY());

        if (this.cursorType == CursorType.POI) {
            this.poiController.createNewPOI(mc);

            this.model.notifyObservers();

            this.mapCanvas.setCursor(Cursor.DEFAULT);
        }

        if (this.cursorType == CursorType.STARTPOINT) {
            Drawable intersection = model.nearest(mc, Type.INTERSECTIONNODE);

            // Perform nearest neighbor
            search.setFrom(((IntersectionNode) intersection).id, mc);
            search.setStartPoint(new NavPoint(mc.getX(), mc.getY(), IconsLoader.getInstance().getIcon("START_POINT", "UI","svg")));
            model.notifyObservers();

            mapCanvas.setCursor(Cursor.DEFAULT);
            DecimalFormat df = new DecimalFormat("#.####");
            String xCoord = String.valueOf(df.format((double)mc.getX()));
            String yCoord = String.valueOf(df.format((double)mc.getY()));
            navInputFrom.setText(yCoord + ", " + xCoord);
        }

        if (this.cursorType == CursorType.ENDPOINT) {
            Drawable intersection = model.nearest(mc, Type.INTERSECTIONNODE);

            // Perform nearest neighbor
            search.setTo(((IntersectionNode) intersection).id, mc);
            search.setEndPoint(new NavPoint(mc.getX(), mc.getY(), IconsLoader.getInstance().getIcon("END_POINT", "UI","svg")));
            mapCanvas.repaint();

            mapCanvas.setCursor(Cursor.DEFAULT);
            DecimalFormat df = new DecimalFormat("#.####");
            String xCoord = String.valueOf(df.format((double)mc.getX()));
            String yCoord = String.valueOf(df.format((double)mc.getY()));
            navInputTo.setText(yCoord + ", " + xCoord);
        }

        this.cursorType = CursorType.NORMAL;
    }

    private void openFileLoadDialog() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        try {
            this.model.io.load(file);
            this.model.notifyObservers();
        } catch (IOException | XMLStreamException err) {
            err.printStackTrace();
        }
    }

    private void openFileSaveDialog() {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(null);

        try {
            this.model.io.save(file);
            this.model.notifyObservers();
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    private void setMousePressed(MouseEvent e) {
        lastMouse = new Point(e.getX(), e.getY());

        model.notifyObservers();
    }

    private void setOnMouseMoved(MouseEvent e) {
        Point point = mapCanvas.toModelCoords(e.getX(), e.getY());

        Road nearestRoad = (Road) this.model.nearestAmount(
            point,
            new Type[]{ Type.SMALLROADS, Type.NORMALROADS, Type.BIGROADS }
        );

        this.model.closestRoadName = nearestRoad;

        this.roadName.setText(nearestRoad.getName());
    }

    private void setMouseDragged(MouseEvent e) {
        isDragging=true;
        mapCanvas.pan(e.getX() - lastMouse.getX(), e.getY() - lastMouse.getY());
        lastMouse = new Point(e.getX(), e.getY());
    }

    private void setScrolled(ScrollEvent event) {
        double factor = Math.pow(1.001, event.getDeltaY());

        if (event.getDeltaY() > 0 && this.mapCanvas.getPixelWidth() <= 7.310674624219874E-7) {
            return;
        }

        mapCanvas.zoom(factor, event.getX(), event.getY());

        if (this.model.getDebugModel().isDebugging()) {
            debugController.setDebugInfo("Zoom factor", Double.toString(factor));
        }

        model.getMeasurementLabelModel().measureDistance(mapCanvas, measurement_label);
    }

    private void toggleColorMode(ActionEvent actionEvent) {
        stylesheetController.changeColorMode(actionEvent);
    }

    private void toggleSidebar(ActionEvent event) {
        this.sidebarAnimationController.slideAnimation();
        model.sideMenuOpen = !model.sideMenuOpen;
        model.notifyObservers();
    }

    private void toggleTypeColors() {
        this.typeColors.toggle();

        this.treeVisibilityToggles.hide();
    }

    private void toggleTreeVisibilityToggles() {
        this.treeVisibilityToggles.toggle();

        this.typeColors.hide();
    }

    private void swapOriginDestination() {
        search.swapNavInput();
    }

    private void resetNavigation() {
        this.navigationErrorLabel.setVisible(false);

        this.model.getPOIModel().setPoiPoint(null);

        this.search.resetRoute(directionsController);

        this.directionsBox.setVisible(false);
    }

    private void toggleSettingsButtonClicked() {
        settingsMenu.setVisible(settingsMenu.isVisible() == false);
        settingsMenu.managedProperty().bind(settingsMenu.visibleProperty());
    }
}
