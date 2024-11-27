package com.cgvsu.math;


import com.cgvsu.math.vector.Vector4f;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MathTestVector4f {

    @Test
    public void testConstructorAndGetters() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        assertEquals(1.0f, v.getX());
        assertEquals(2.0f, v.getY());
        assertEquals(3.0f, v.getZ());
        assertEquals(4.0f, v.getW());
    }

    @Test
    public void testSetters() {
        Vector4f v = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        v.setX(1.0f);
        v.setY(2.0f);
        v.setZ(3.0f);
        v.setW(4.0f);
        assertEquals(1.0f, v.getX());
        assertEquals(2.0f, v.getY());
        assertEquals(3.0f, v.getZ());
        assertEquals(4.0f, v.getW());
    }

    @Test
    public void testIndexAccess() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        assertEquals(1.0f, v.get(0));
        assertEquals(2.0f, v.get(1));
        assertEquals(3.0f, v.get(2));
        assertEquals(4.0f, v.get(3));

        v.set(0, 5.0f);
        v.set(1, 6.0f);
        v.set(2, 7.0f);
        v.set(3, 8.0f);
        assertEquals(5.0f, v.get(0));
        assertEquals(6.0f, v.get(1));
        assertEquals(7.0f, v.get(2));
        assertEquals(8.0f, v.get(3));
    }

    @Test
    public void testIndexOutOfBounds() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        assertThrows(IndexOutOfBoundsException.class, () -> v.get(4));
        assertThrows(IndexOutOfBoundsException.class, () -> v.set(4, 5.0f));
    }

    @Test
    public void testAdd() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(5.0f, 6.0f, 7.0f, 8.0f);
        Vector4f result = v1.add(v2);

        assertEquals(6.0f, result.getX());
        assertEquals(8.0f, result.getY());
        assertEquals(10.0f, result.getZ());
        assertEquals(12.0f, result.getW());
    }

    @Test
    public void testSub() {
        Vector4f v1 = new Vector4f(5.0f, 6.0f, 7.0f, 8.0f);
        Vector4f v2 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f result = v1.sub(v2);

        assertEquals(4.0f, result.getX());
        assertEquals(4.0f, result.getY());
        assertEquals(4.0f, result.getZ());
        assertEquals(4.0f, result.getW());
    }

    @Test
    public void testMul() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f result = v.mul(2.0f);

        assertEquals(2.0f, result.getX());
        assertEquals(4.0f, result.getY());
        assertEquals(6.0f, result.getZ());
        assertEquals(8.0f, result.getW());
    }

    @Test
    public void testDiv() {
        Vector4f v = new Vector4f(4.0f, 8.0f, 12.0f, 16.0f);
        Vector4f result = v.div(4.0f);

        assertEquals(1.0f, result.getX());
        assertEquals(2.0f, result.getY());
        assertEquals(3.0f, result.getZ());
        assertEquals(4.0f, result.getW());
    }

    @Test
    public void testDivByZero() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        assertThrows(IllegalArgumentException.class, () -> v.div(0.0f));
    }

    @Test
    public void testLength() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 2.0f, 1.0f);
        // Ожидаемая длина: sqrt(1^2 + 2^2 + 2^2 + 1^2) = sqrt(10)
        assertEquals(Math.sqrt(10.0f), v.length(), 1e-6);
    }

    @Test

    public void testNormalize() {
        Vector4f v = new Vector4f(1.0f, 2.0f, 2.0f, 1.0f);
        Vector4f normalized = v.normalize();


        assertEquals(1.0f, normalized.length(), 1e-6);


        float len = (float) Math.sqrt(10.0f);
        assertEquals(1.0f / len, normalized.getX(), 1e-6);
        assertEquals(2.0f / len, normalized.getY(), 1e-6);
        assertEquals(2.0f / len, normalized.getZ(), 1e-6);
        assertEquals(1.0f / len, normalized.getW(), 1e-6);
    }

    @Test
    public void testNormalizeZeroVector() {
        Vector4f v = new Vector4f(0.0f, 0.0f, 0.0f, 0.0f);
        assertThrows(IllegalArgumentException.class, v::normalize);
    }

    @Test
    public void testDotProduct() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(5.0f, 6.0f, 7.0f, 8.0f);
        float dot = v1.dot(v2);

        assertEquals(70.0f, dot, 1e-6);
    }

    @Test
    public void testEquals() {
        Vector4f v1 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v2 = new Vector4f(1.0f, 2.0f, 3.0f, 4.0f);
        Vector4f v3 = new Vector4f(5.0f, 6.0f, 7.0f, 8.0f);

        assertTrue(v1.equals(v2));
        assertFalse(v1.equals(v3));
    }
}
