package com.cgvsu.math;


import com.cgvsu.math.vector.Vector3f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathTestVector3f {

    @Test
    public void testConstructorAndGetters() {
        Vector3f v = new Vector3f(1.0f, 2.0f, 3.0f);
        assertEquals(1.0f, v.getX());
        assertEquals(2.0f, v.getY());
        assertEquals(3.0f, v.getZ());
    }

    @Test
    public void testSetters() {
        Vector3f v = new Vector3f(0.0f, 0.0f, 0.0f);
        v.setX(1.0f);
        v.setY(2.0f);
        v.setZ(3.0f);
        assertEquals(1.0f, v.getX());
        assertEquals(2.0f, v.getY());
        assertEquals(3.0f, v.getZ());
    }

    @Test
    public void testIndexAccess() {
        Vector3f v = new Vector3f(1.0f, 2.0f, 3.0f);
        assertEquals(1.0f, v.get(0));
        assertEquals(2.0f, v.get(1));
        assertEquals(3.0f, v.get(2));

        v.set(0, 4.0f);
        v.set(1, 5.0f);
        v.set(2, 6.0f);
        assertEquals(4.0f, v.get(0));
        assertEquals(5.0f, v.get(1));
        assertEquals(6.0f, v.get(2));
    }

    @Test
    public void testIndexOutOfBounds() {
        Vector3f v = new Vector3f(1.0f, 2.0f, 3.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(3));
        assertThrows(IndexOutOfBoundsException.class, () -> v.set(3, 4.0f));
    }

    @Test
    public void testAdd() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(4.0f, 5.0f, 6.0f);
        Vector3f result = v1.add(v2);

        assertEquals(5.0f, result.getX());
        assertEquals(7.0f, result.getY());
        assertEquals(9.0f, result.getZ());
    }

    @Test
    public void testSub() {
        Vector3f v1 = new Vector3f(4.0f, 5.0f, 6.0f);
        Vector3f v2 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f result = v1.sub(v2);

        assertEquals(3.0f, result.getX());
        assertEquals(3.0f, result.getY());
        assertEquals(3.0f, result.getZ());
    }

    @Test
    public void testMul() {
        Vector3f v = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f result = v.mul(2.0f);

        assertEquals(2.0f, result.getX());
        assertEquals(4.0f, result.getY());
        assertEquals(6.0f, result.getZ());
    }

    @Test
    public void testDiv() {
        Vector3f v = new Vector3f(4.0f, 6.0f, 8.0f);
        Vector3f result = v.div(2.0f);

        assertEquals(2.0f, result.getX());
        assertEquals(3.0f, result.getY());
        assertEquals(4.0f, result.getZ());
    }

    @Test
    public void testDivByZero() {
        Vector3f v = new Vector3f(1.0f, 2.0f, 3.0f);
        assertThrows(IllegalArgumentException.class, () -> v.div(0.0f));
    }

    @Test
    public void testLength() {
        Vector3f v = new Vector3f(3.0f, 4.0f, 0.0f);
        assertEquals(5.0f, v.length(), 1e-6);
    }

    @Test
    public void testNormalize() {
        Vector3f v = new Vector3f(3.0f, 4.0f, 0.0f);
        Vector3f normalized = v.normalize();

        assertEquals(1.0f, normalized.length(), 1e-6);
        assertEquals(0.6f, normalized.getX(), 1e-6);
        assertEquals(0.8f, normalized.getY(), 1e-6);
        assertEquals(0.0f, normalized.getZ(), 1e-6);
    }

    @Test
    public void testNormalizeZeroVector() {
        Vector3f v = new Vector3f(0.0f, 0.0f, 0.0f);
        assertThrows(IllegalArgumentException.class, v::normalize);
    }

    @Test
    public void testDotProduct() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(4.0f, 5.0f, 6.0f);
        float dot = v1.dot(v2);

        assertEquals(32.0f, dot, 1e-6);
    }

    @Test
    public void testCrossProduct() {
        Vector3f v1 = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f v2 = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f cross = v1.cross(v2);

        assertEquals(0.0f, cross.getX());
        assertEquals(0.0f, cross.getY());
        assertEquals(1.0f, cross.getZ());
    }

    @Test
    public void testEquals() {
        Vector3f v1 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v2 = new Vector3f(1.0f, 2.0f, 3.0f);
        Vector3f v3 = new Vector3f(4.0f, 5.0f, 6.0f);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }
}