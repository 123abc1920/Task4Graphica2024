package com.cgvsu.light;

import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class Light {
	public static Vector3f position = new Vector3f(0, 0, 100);
	public static Vector3f target = new Vector3f(0, 0, 0);
	public static Vector3f ray;

	private static double k = 0.3;
	private static double ks = 0.4;
	private static double highLightShine = 31;

	static {
		newRay();
	}

	private static void newRay() {
		ray = target.sub(position);
		ray = ray.normalize();
	}

	public static void setPosition(Vector3f v) {
		Light.position = v;
		newRay();
	}

	public static void setTarget(Vector3f v) {
		Light.position = v;
		newRay();
	}

	public static void setK(double k) {
		Light.k = k;
	}

	public static void setKs(double ks) {
		Light.ks = ks;
	}

	public static void setHighLightShine(double highLightShine) {
		Light.highLightShine = highLightShine;
	}

	public static Color newRgb(Color start, Vector3f n, Vector3f cameraPos) {
		double l = ray.dotProduct(n.multiplyScalar(-1));

		if (l < 0) {
			l = 0;
		}

		cameraPos = cameraPos.normalize();
		Vector3f reflectedRay = ray.sub(n.multiplyScalar(2 * ray.dotProduct(n)));
		reflectedRay = reflectedRay.normalize();

		double r = reflectedRay.dotProduct(cameraPos);
		double highlight = ks * Math.pow(r, highLightShine);
		if (r < 0 || l == 0) {
			highlight = 0;
		}

		double red = Math.max(0, Math.min(1, start.getRed() * (1 - k) + start.getRed() * k * l + highlight));
		double green = Math.max(0, Math.min(1, start.getGreen() * (1 - k) + start.getGreen() * k * l + highlight));
		double blue = Math.max(0, Math.min(1, start.getBlue() * (1 - k) + start.getBlue() * k * l + highlight));

		return new Color(red, green, blue, 1);
	}
}
