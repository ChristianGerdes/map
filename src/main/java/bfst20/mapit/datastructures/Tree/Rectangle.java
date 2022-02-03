package bfst20.mapit.datastructures.Tree;

import java.util.List;
import java.util.Arrays;
import java.io.Serializable;

/**
 * Rectangle
 */
public class Rectangle implements Serializable {
    private static final long serialVersionUID = -328416967393931333L;

    /**
     * Upper left x
     */
    private final float x;

    /**
     * Upper left y
     */
    private final float y;

    /**
     * Rectangle width
     */
    private final float width;

    /**
     * Rectangle height
     */
    private final float height;

    public Rectangle(float[] coords, float[] dimensions) {
        this(coords[0], coords[1], dimensions[0], dimensions[1]);
    }

    public Rectangle(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    /**
     * Create the rectangle from a list of Nodes
     *
     * @param items
     */
    public Rectangle(Node[] items) {
        float[] x = new float[items.length * 2];
        float[] y = new float[items.length * 2];

        for (int i = 0; i < items.length; ++i) {
            Node node = items[i];

            x[i * 2] = node.mbr.x1();
            y[i * 2] = node.mbr.y1();

            x[i * 2 + 1] = node.mbr.x2();
            y[i * 2 + 1] = node.mbr.y2();
        }

        Arrays.sort(x);
        Arrays.sort(y);

        this.x = x[0];
        this.y = y[0];
        this.width = Math.max(x[0], x[x.length - 1]) - Math.min(x[0], x[x.length - 1]);
        this.height = Math.abs(Math.max(y[0], y[y.length - 1]) - Math.min(y[0], y[y.length - 1]));
    }

    public Rectangle(List<Node> items) {
        float[] x = new float[items.size() * 2];
        float[] y = new float[items.size() * 2];

        for (int i = 0; i < items.size(); ++i) {
            Node node = items.get(i);

            x[i * 2] = node.mbr.x1();
            y[i * 2] = node.mbr.y1();

            x[i * 2 + 1] = node.mbr.x2();
            y[i * 2 + 1] = node.mbr.y2();
        }

        Arrays.sort(x);
        Arrays.sort(y);

        this.x = x[0];
        this.y = y[0];
        this.width = Math.max(x[0], x[x.length - 1]) - Math.min(x[0], x[x.length - 1]);
        this.height = Math.abs(Math.max(y[0], y[y.length - 1]) - Math.min(y[0], y[y.length - 1]));
    }

    public float x1() {
        return this.x;
    }

    public float y1() {
        return this.y;
    }

    public float x2() {
        return this.x + this.width;
    }

    public float y2() {
        return this.y + this.height;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    public float getCenterX() {
        return this.x + this.width / 2;
    }

    public float getCenterY() {
        return this.y + this.height / 2;
    }

    /**
     * Merge two rectangles together
     *
     * @param rectangle
     * @return A new rectangle
     */
    public Rectangle add(Rectangle rectangle) {
        float r1MinY = Math.min(this.y1(), this.y2());
        float r1MaxY = Math.max(this.y1(), this.y2());
        float r2MinY = Math.min(rectangle.y1(), rectangle.y2());
        float r2MaxY = Math.max(rectangle.y1(), rectangle.y2());

        return new Rectangle(
            Math.min(this.x1(), rectangle.x1()),
            Math.min(this.y1(), rectangle.y1()),
            Math.max(this.x2(), rectangle.x2()) - Math.min(this.x1(), rectangle.x1()),
            Math.abs(Math.max(Math.abs(r1MaxY), Math.abs(r2MaxY)) - Math.min(Math.abs(r1MinY), Math.abs(r2MinY)))
        );
    }

    /**
     * Find the area of the rectangle
     */
    public float area() {
        return this.width * this.height;
    }

    /**
     * Find the perimeter of the rectangle
     */
    public float perimeter() {
        return 2 * this.width + 2 * this.height;
    }

    /**
     * Check if a rectangle is inside rectangle
     *
     * @param rectangle     The rectangle to check if its inside
     * @return              Boolean if the passed rectangle is inside
     */
    public boolean contains(Rectangle rectangle) {
        float area = this.area();

        Rectangle mergedRectangle = this.add(rectangle);

        return (mergedRectangle.area() - area) == 0.0f;
    }

    /**
     * Find the intersection area between two rectangles
     */
    public float intersectionArea(Rectangle r) {
        float xOverlap = Math.max(0, Math.min(this.x2(), r.x2()) - Math.max(this.x1(), r.x1()));
        float yOverlap = Math.max(0, Math.min(Math.abs(this.y1()), Math.abs(r.y1())) - Math.max(Math.abs(this.y2()), Math.abs(r.y2())));

        return xOverlap * yOverlap;
    }

    /**
     * Determine if two rectangles intersect
     */
    public boolean intersects(Rectangle r) {
        return (Math.abs(this.getCenterX() - r.getCenterX()) <= (this.width() / 2 + r.width() / 2)) && (Math.abs(this.getCenterY() - r.getCenterY()) <= (this.height() / 2 + r.height() / 2));
    }
}