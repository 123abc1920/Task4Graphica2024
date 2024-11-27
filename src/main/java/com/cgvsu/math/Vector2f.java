package com.cgvsu.math;

import com.cgvsu.math.Global;

public class Vector2f {
    private float x;
    private float y;

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

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float get(int index) {
        if (index == 0) {
            return x;
        } else if (index == 1) {
            return y;
        } else {
            throw new IndexOutOfBoundsException("Index must be 0 or 1 for a 2D vector.");
        }
    }

    public void set(int index, float value) {
        if (index == 0) {
            this.x = value;
        } else if (index == 1) {
            this.y = value;
        } else {
            throw new IndexOutOfBoundsException("Index must be 0 or 1 for a 2D vector.");
        }
    }

    public boolean equals(Vector2f other) {
        return Math.abs(x - other.getX()) < Global.eps && Math.abs(y - other.getY()) < Global.eps;
    }

    public Vector2f add(Vector2f other) {
        return new Vector2f(this.x + other.x, this.y + other.y);
    }

    public Vector2f sub(Vector2f other) {
        return new Vector2f(this.x - other.x, this.y - other.y);
    }

    public Vector2f mul(float scalar) {
        return new Vector2f(this.x * scalar, this.y * scalar);
    }

    public Vector2f div(float scalar) {
        if (scalar > Global.eps) {
            return new Vector2f(this.x / scalar, this.y / scalar);
        } else {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        }
    }

    public float length() {
        return (float) Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2f normalize() {
        float len = this.length();
        if (len > Global.eps) {
            return new Vector2f(this.x / len, this.y / len);
        } else {
            throw new IllegalArgumentException("Cannot normalize a zero-length vector.");
        }
    }

    public float dot(Vector2f other) {
        return this.x * other.x + this.y * other.y;
    }
}