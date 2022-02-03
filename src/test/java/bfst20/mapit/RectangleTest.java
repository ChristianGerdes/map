package bfst20.mapit;

import org.junit.jupiter.api.Test;
import bfst20.mapit.datastructures.Tree.Rectangle;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RectangleTest {
    @Test
    void initializing_a_rectangle_from_coords_and_dimensions() {
        Rectangle rectangle = new Rectangle(10f, 15f, 4f, 2f);

        assertEquals(10f, rectangle.x1());
        assertEquals(15f, rectangle.y1());
        assertEquals(14f, rectangle.x2());
        assertEquals(17f, rectangle.y2());
        assertEquals(4f, rectangle.width());
        assertEquals(2f, rectangle.height());
        assertEquals(12f, rectangle.getCenterX());
        assertEquals(16f, rectangle.getCenterY());
    }

    @Test
    void calculating_the_area_of_rectangle() {
        Rectangle rectangle = new Rectangle(10f, 10f, 5f, 5f);

        assertEquals(10f, rectangle.x1());
        assertEquals(10f, rectangle.y1());
        assertEquals(25f, rectangle.area());
    }

    @Test
    void calculating_the_perimeter_of_rectangle() {
        Rectangle rectangle = new Rectangle(0f, 0f, 5f, 5f);

        assertEquals(0f, rectangle.x1());
        assertEquals(0f, rectangle.y1());
        assertEquals(20f, rectangle.perimeter());
    }

    @Test
    void merge_two_rectangles_into_a_new_rectangle() {
        Rectangle rectangle1 = new Rectangle(0f, 0f, 10f, 10f);
        Rectangle rectangle2 = new Rectangle(5f, 5f, 10f, 10f);
        Rectangle mergedRectangle = rectangle1.add(rectangle2);

        assertEquals(0f, mergedRectangle.x1());
        assertEquals(0f, mergedRectangle.y1());
        assertEquals(15f, mergedRectangle.x2());
        assertEquals(15f, mergedRectangle.y2());
        assertEquals(225f, mergedRectangle.area());
    }

    @Test
    void if_rectangle_contains_another_rectangle()
    {
        Rectangle rectangle1 = new Rectangle(0f, 0f, 50f, 50f);
        Rectangle rectangle2 = new Rectangle(10f, 10f, 30f, 30f);

        assertTrue(rectangle1.contains(rectangle2));
        assertFalse(rectangle2.contains(rectangle1));
    }
}