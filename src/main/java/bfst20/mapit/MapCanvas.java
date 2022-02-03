package bfst20.mapit;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import bfst20.mapit.datastructures.Point;
import bfst20.mapit.datastructures.Tree.Rectangle;
import bfst20.mapit.model.Type;
import bfst20.mapit.model.drawables.points.POI;
import bfst20.mapit.model.drawables.roads.Road;
import bfst20.mapit.model.drawables.roads.RoadType;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;

public class MapCanvas extends Canvas {

    private Model model;
    private GraphicsContext gc;
    private Affine trans;
    float[] bounds;
    Button setPOI = new Button("POI");
    private Pane debugBox;

    public void initialize(Model model, Pane debugBox) {
        this.model = model;
        this.debugBox = debugBox;
        this.gc = getGraphicsContext2D();
        this.trans = new Affine();
        this.model.addObserver(this::repaint);
        this.model.setResetView(this::resetView);
        this.model.getDebugModel().addDebugObserver(this::redrawDebugger);

        this.resetView();

        widthProperty().bind(getScene().widthProperty());
        heightProperty().bind(getScene().heightProperty());

        widthProperty().addListener((a, b, c) -> {
            repaint();
        });

        heightProperty().addListener((a, b, c) -> {
            repaint();
        });
    }

    public void redrawDebugger() {
        this.debugBox.getChildren().clear();

        if (!this.model.getDebugModel().isDebugging()) {
            return;
        }

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        HashMap<String, String> debugInfo = this.model.getDebugModel().getDebugInfo();
        int i = 0;

        String[] keys = debugInfo.keySet().toArray(new String[debugInfo.keySet().size()]);
        Arrays.sort(keys);

        for (String key : keys) {
            gridPane.add(new Label(key), 0, i);
            gridPane.add(new Label(debugInfo.get(key)), 1, i);
            i++;
        }

        this.debugBox.getChildren().add(gridPane);
    }

    public void resetView() {
        /*
         * bounds[0]: minlon | bounds[1]: maxlat | bounds[2]: minlat | bounds[3]: maxlon
         */
        float[] bounds = model.getBounds();
        double zoomlvl = 1000;

        double centerX = getWidth() / 2, centerY = getHeight() / 2;
        trans = new Affine();
        zoom(zoomlvl, bounds[3], bounds[1]);
        Point p2 = new Point(trans.transform(bounds[0], bounds[2]));
        pan(centerX - p2.getX(), centerY - p2.getY());
        this.repaint();
    }

    public void repaint() {
        gc.setTransform(new Affine());
        gc.setFill(this.model.isLightColorMode() ? Color.web("#B0DAE8") : Color.web("#191A1A"));
        gc.fillRect(0, 0, getWidth(), getHeight());
        gc.setTransform(this.trans);
        gc.setFillRule(FillRule.EVEN_ODD);

        double pixelwidth = this.getPixelWidth();

        if (this.model.getDebugModel().isDebugging()) {
            this.model.getDebugModel().setDebugInfo("Pixel width", Double.toString(pixelwidth));
        }

        // width of sidebar is 483
        double canvasWidth = this.model.sideMenuOpen ? this.getWidth() : this.getWidth();
        double canvasHeight = this.getHeight();

        int width = 350;
        int height = 350;

        // If sidebar is expanded set x to the sidebar width
        double x1 = this.model.getDebugModel().isLimitingRange() ? canvasWidth / 2 - width / 2
                : model.getSideBarWidth();
        double y1 = this.model.getDebugModel().isLimitingRange() ? canvasHeight / 2 - height / 2 : 0;
        double x2 = this.model.getDebugModel().isLimitingRange() ? canvasWidth / 2 + width / 2 : canvasWidth;
        double y2 = this.model.getDebugModel().isLimitingRange() ? canvasHeight / 2 + height / 2 : canvasHeight;

        Point p1 = this.toModelCoords(x1, y1);
        Point p2 = this.toModelCoords(x2, y2);

        Rectangle range = new Rectangle(new float[] { (float) p1.getX(), (float) p1.getY() },
                new float[] { (float) (p2.getX() - p1.getX()), (float) (p2.getY() - p1.getY()) });

        List<Drawable> results = model.search(range, pixelwidth);

        if (this.model.getDebugModel().isDebugging()) {
            this.model.getDebugModel().setDebugInfo("Objects", Integer.toString(results.size()));
        }

        for (Drawable drawable : results) {
            Type type = drawable.getType();

            if (model.getTypeSettings().getChangedColor(type) != null && !type.fill) {
                gc.setStroke(model.getTypeSettings().getChangedColor(type));
                gc.setLineWidth(type.lineWidth == -1 ? pixelwidth : type.lineWidth);
            } else if (type == Type.ROADNAMES) {

            } else if (Type.isRoad(type)) {
                RoadType roadType = ((Road) drawable).getRoadType();
                gc.setStroke(
                        model.isLightColorMode() ? RoadType.getLightColor(roadType) : RoadType.getDarkColor(roadType));
                gc.setLineWidth(RoadType.getRoadWidth(roadType, pixelwidth));
            } else {
                gc.setStroke(model.isLightColorMode() ? type.lightStrokeColor : type.darkStrokeColor);
                gc.setLineWidth(type.lineWidth == -1 ? pixelwidth : type.lineWidth);
            }

            drawable.draw(gc);

            if (this.model.getDebugModel().isLimitingRange()) {
                drawable.drawRect(gc, pixelwidth);
            }

            if (type.fill) {
                if (model.getTypeSettings().getChangedColor(type) != null) {
                    gc.setFill(model.getTypeSettings().getChangedColor(type));
                    gc.fill();
                } else {
                    gc.setFill(this.model.isLightColorMode() ? type.lightFillColor : type.darkFillColor);
                    gc.fill();
                }
            }
        }

        if (model.getPOIModel().isVisible()) {
            for (POI poi : model.getPOIModel().getPOI()) {
                poi.setLightmode(this.model.isLightColorMode());
                poi.draw(gc);
            }
            if (model.getPOIModel().getPoiPoint() != null) {
                model.getPOIModel().getPoiPoint().draw(gc);
            }
        }

        // drawing the psuedo accurate start segment and end segment of the route
        if (model.getStartSegment() != null && model.getEndSegment() != null) {
            gc.setLineWidth(RoadType.getRoadWidth(RoadType.ROUTE, pixelwidth));
            if (model.getDebugModel().isRouteDebugging()) {
                gc.setStroke(Color.DARKGREEN);
            } else {
                gc.setStroke(model.isLightColorMode() ? RoadType.getLightColor(RoadType.ROUTE)
                        : RoadType.getDarkColor(RoadType.ROUTE));
            }
            model.getStartSegment().forEach(e -> e.draw(gc));
            model.getEndSegment().forEach(e -> e.draw(gc));
        }

        // drawing the points found by ClosestPoint.java if debugging
        if (model.getDebugModel().isRouteDebugging() && model.startSegmentPoints != null
                && model.endSegmentPoints != null) {
            model.startSegmentPoints.forEach(e -> e.drawDEBUG(gc));
            model.endSegmentPoints.forEach(e -> e.drawDEBUG(gc));
        }

        if (model.getSearchModel().startPoint != null) {
            model.getSearchModel().startPoint.draw(gc);
        }

        if (model.getSearchModel().endPoint != null) {
            model.getSearchModel().endPoint.draw(gc);
        }

        if (model.getSearchModel().getSearchPoint() != null) {
            model.getSearchModel().getSearchPoint().draw(gc);
        }

        if (this.model.getDebugModel().isLimitingRange()) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(4 * pixelwidth);
            gc.strokeRect(p1.getX(), p1.getY(), p2.getX() - p1.getX(), p2.getY() - p1.getY());
        }

        if (this.model.getDebugModel().isDebugging() && model.closestRoadName != null) {
            model.closestRoadName.lightUp(gc);
        }
    }

    public double getPixelWidth() {
        return 1 / Math.sqrt(Math.abs(trans.determinant()));
    }

    public Point toModelCoords(double x, double y) {
        try {
            return new Point(trans.inverseTransform(x, y));
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void zoom(double factor, double x, double y) {
        trans.prependScale(factor, factor, x, y);
        repaint();
    }

    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
        repaint();
    }

    public void panToPoint(Point point, double zoomlvl) {
        double centerX = getWidth() / 2;
        double centerY = getHeight() / 2;

        this.trans = new Affine();

        this.zoom(zoomlvl, point.getX(), point.getY());

        Point p2 = new Point(trans.transform((double) point.getX(), (double) point.getY()));

        this.pan(centerX - p2.getX(), centerY - p2.getY());

        this.repaint();
    }
}
