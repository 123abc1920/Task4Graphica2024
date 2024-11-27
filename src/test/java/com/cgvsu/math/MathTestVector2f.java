package com.cgvsu.math;
import com.cgvsu.math.vector.Vector2f;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathTestVector2f {

    @Test
    public void testConstructorAndGetters() {
        Vector2f v = new Vector2f(3.0f, 4.0f);
        assertEquals(3.0f, v.getX());
        assertEquals(4.0f, v.getY());
    }

    @Test
    public void testSetters() {
        Vector2f v = new Vector2f(0.0f, 0.0f);
        v.setX(1.0f);
        v.setY(2.0f);
        assertEquals(1.0f, v.getX());
        assertEquals(2.0f, v.getY());
    }

    @Test
    public void testIndexAccess() {
        Vector2f v = new Vector2f(1.0f, 2.0f);
        assertEquals(1.0f, v.get(0));
        assertEquals(2.0f, v.get(1));

        v.set(0, 3.0f);
        v.set(1, 4.0f);
        assertEquals(3.0f, v.get(0));
        assertEquals(4.0f, v.get(1));
    }

    @Test
    public void testIndexOutOfBounds() {
        Vector2f v = new Vector2f(1.0f, 2.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(2));
        assertThrows(IndexOutOfBoundsException.class, () -> v.set(2, 3.0f));
    }

    @Test
    public void testAdd() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(3.0f, 4.0f);
        Vector2f result = v1.add(v2);

        assertEquals(4.0f, result.getX());
        assertEquals(6.0f, result.getY());
    }

    @Test
    public void testSub() {
        Vector2f v1 = new Vector2f(5.0f, 6.0f);
        Vector2f v2 = new Vector2f(3.0f, 4.0f);
        Vector2f result = v1.sub(v2);

        assertEquals(2.0f, result.getX());
        assertEquals(2.0f, result.getY());
    }

    @Test
    public void testMul() {
        Vector2f v = new Vector2f(1.0f, 2.0f);
        Vector2f result = v.mul(3.0f);

        assertEquals(3.0f, result.getX());
        assertEquals(6.0f, result.getY());
    }

    @Test
    public void testDiv() {
        Vector2f v = new Vector2f(6.0f, 9.0f);
        Vector2f result = v.div(3.0f);

        assertEquals(2.0f, result.getX());
        assertEquals(3.0f, result.getY());
    }

    @Test
    public void testDivByZero() {
        Vector2f v = new Vector2f(1.0f, 2.0f);
        assertThrows(IllegalArgumentException.class, () -> v.div(0.0f));
    }

    @Test
    public void testLength() {
        Vector2f v = new Vector2f(3.0f, 4.0f);
        assertEquals(5.0f, v.length(), 1e-6);
    }

    @Test
    public void testNormalize() {
        Vector2f v = new Vector2f(3.0f, 4.0f);
        Vector2f normalized = v.normalize();

        assertEquals(1.0f, normalized.length(), 1e-6);
        assertEquals(0.6f, normalized.getX(), 1e-6);
        assertEquals(0.8f, normalized.getY(), 1e-6);
    }

    @Test
    public void testNormalizeZeroVector() {
        Vector2f v = new Vector2f(0.0f, 0.0f);
        assertThrows(IllegalArgumentException.class, v::normalize);
    }

    @Test
    public void testDotProduct() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(3.0f, 4.0f);
        float dot = v1.dot(v2);

        assertEquals(11.0f, dot, 1e-6);
    }

    @Test
    public void testEquals() {
        Vector2f v1 = new Vector2f(1.0f, 2.0f);
        Vector2f v2 = new Vector2f(1.0f, 2.0f);
        Vector2f v3 = new Vector2f(2.0f, 3.0f);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }
}