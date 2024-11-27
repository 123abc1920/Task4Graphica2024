package com.cgvsu.math;

public class Vector3f {
    private final float x;
    private final float y;
    private final float z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }
    public Vector3f sub(Vector3f other) {
        return new Vector3f(this.x - other.x, this.y - other.y, this.z - other.z);
    }
    public Vector3f normalize() {
        float length = (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        if (length > Global.eps) {
            return new Vector3f(this.x / length, this.y / length, this.z / length);
        } else {
            throw new IllegalArgumentException("Cannot normalize a zero vector");
        }
    }
    public float dot(Vector3f other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
    }



    public static Vector3f addition(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ());
    }

    public static Vector3f subtraction(Vector3f v1, Vector3f v2) {
        return new Vector3f(v1.getX() - v2.getX(), v1.getY() - v2.getY(), v1.getZ() - v2.getZ());
    }

    public static Vector3f multiplication(Vector3f v1, float a) {
        return new Vector3f(v1.getX() * a, v1.getY() * a, v1.getZ() * a);
    }

    public static Vector3f division(Vector3f v1, float a) {
        if (Math.abs(a) < Global.eps) {
            throw new IllegalArgumentException("Division by zero or near-zero value");
        }
        return new Vector3f(v1.getX() / a, v1.getY() / a, v1.getZ() / a);
    }

    public static float length(Vector3f v) {
        return (float) Math.sqrt(v.getX() * v.getX() + v.getY() * v.getY() + v.getZ() * v.getZ());
    }

    public static Vector3f normalize(Vector3f v) {
        float l = length(v);
        if (l > Global.eps) {
            return new Vector3f(v.getX() / l, v.getY() / l, v.getZ() / l);
        } else {
            throw new IllegalArgumentException("Division by zero or near-zero value");
        }
    }

    public static Vector3f cross(Vector3f a, Vector3f b) {
        return new Vector3f(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX()
        );
    }


    public static float scalar(Vector3f vector1, Vector3f vector2) {
        return vector1.getX() * vector2.getX() + vector1.getY() * vector2.getY() + vector1.getZ() * vector2.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3f other = (Vector3f) obj;
        return Math.abs(x - other.getX()) < Global.eps
                && Math.abs(y - other.getY()) < Global.eps
                && Math.abs(z - other.getZ()) < Global.eps;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
