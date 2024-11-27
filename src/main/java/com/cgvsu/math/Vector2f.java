package com.cgvsu.math;
public class Vector2f {
    private final float x;
    private final float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean equals(Vector2f other) {
        return Math.abs(x - other.getX()) < Global.eps && Math.abs(y - other.getY()) < Global.eps;
    }

    public static Vector2f addition(Vector2f v1, Vector2f v2) {
        return new Vector2f(v1.getX() + v2.getX(), v1.getY() + v2.getY());
    }

    public static Vector2f subtraction(Vector2f v1, Vector2f v2) {
        return new Vector2f(v1.getX() - v2.getX(), v1.getY() - v2.getY());
    }

    public static Vector2f multiplication(Vector2f v, float a) {
        return new Vector2f(v.getX() * a, v.getY() * a);
    }

    public static Vector2f division(Vector2f v, float a) {
        if (a > Global.eps) {
            return new Vector2f((float) v.getX() / a, (float) v.getY() / a);
        } else {
            throw new IllegalArgumentException("Делить на 0 нельзя");
        }
    }

    public static float length(Vector2f v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY());
    }

    public static Vector2f normalize(Vector2f v) {
        float length = length(v);
        if (length > Global.eps) {
            return new Vector2f((float) v.getX() / length, (float) v.getY() / length);
        } else {
            throw new IllegalArgumentException("Делить на 0 нельзя");
        }
    }

    public static float dotProduct(Vector2f v1, Vector2f v2) {
        return v1.getX() * v2.getX() + v1.getY() * v2.getY();
    }

}