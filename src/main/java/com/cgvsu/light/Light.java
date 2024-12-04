package com.cgvsu.light;

import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class Light {
	public static Vector3f position = new Vector3f(0, 0, 100);
	public static Vector3f target = new Vector3f(0, 0, 0);
	public static Vector3f ray = target.sub(position);

	public static double k = 0.05;

	static {
		ray.normalize();
	}

	public static Color newRgb(Color start, Vector3f n) {
		double l = ray.dotProduct(n.multiplyScalar(-1));

		if (l < 0)
			l = 0;

		double red = Math.max(0, Math.min(1, start.getRed() * (1 - k) + start.getRed() * k * l));
		double green = Math.max(0, Math.min(1, start.getGreen() * (1 - k) + start.getGreen() * k * l));
		double blue = Math.max(0, Math.min(1, start.getBlue() * (1 - k) + start.getBlue() * k * l));

		return new Color(red, green, blue, 1);
	}

	// Вспомогательная функция для вычисления вектора отражения
	private static Vector3f reflect(Vector3f v, Vector3f n) {
		return v.sub(n.multiplyScalar(2 * v.dotProduct(n)));
	}
}
