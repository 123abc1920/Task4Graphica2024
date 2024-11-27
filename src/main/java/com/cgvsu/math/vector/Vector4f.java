package com.cgvsu.math.vector;

import com.cgvsu.math.Global;

public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float get(int index) {
        switch (index) {
            case 0: return x;
            case 1: return y;
            case 2: return z;
            case 3: return w;
            default: throw new IndexOutOfBoundsException("Index must be 0, 1, 2, or 3 for a 4D vector.");
        }
    }

    public void set(int index, float value) {
        switch (index) {
            case 0: this.x = value; break;
            case 1: this.y = value; break;
            case 2: this.z = value; break;
            case 3: this.w = value; break;
            default: throw new IndexOutOfBoundsException("Index must be 0, 1, 2, or 3 for a 4D vector.");
        }
    }


    public boolean equals(Vector4f other) {
        return Math.abs(x - other.getX()) < Global.eps &&
                Math.abs(y - other.getY()) < Global.eps &&
                Math.abs(z - other.getZ()) < Global.eps &&
                Math.abs(w - other.getW()) < Global.eps;
    }

    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }


    public Vector4f normalize() {
        float len = this.length();
        if (len > Global.eps) {
            return new Vector4f(x / len, y / len, z / len, w / len);
        } else {
            throw new IllegalArgumentException("Cannot normalize a zero-length vector.");
        }
    }

    public Vector4f add(Vector4f other) {
        return new Vector4f(this.x + other.x, this.y + other.y, this.z + other.z, this.w + other.w);
    }

    public Vector4f sub(Vector4f other) {
        return new Vector4f(this.x - other.x, this.y - other.y, this.z - other.z, this.w - other.w);
    }

    public Vector4f mul(float scalar) {
        return new Vector4f(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }

    public Vector4f div(float scalar) {
        if (scalar < Global.eps) {
            throw new IllegalArgumentException("Division by zero is not allowed.");
        } else {
            return new Vector4f(this.x / scalar, this.y / scalar, this.z / scalar, this.w / scalar);
        }
    }

    public float dot(Vector4f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
    }
}
