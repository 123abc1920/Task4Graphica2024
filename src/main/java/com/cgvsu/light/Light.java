package com.cgvsu.light;

import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class Light {
	public static Vector3f position = new Vector3f(0, 0, 100);
	public static Vector3f target = new Vector3f(0, 0, 0);
	private static Vector3f ray = target.sub(position).normalize();

	private static double k = 0.3;
	private static double ks = 0.4;
	private static double highLightShine = 31;

	public static void setPosition(Vector3f v) {
		Light.position = v;
		ray = target.sub(position).normalize();
	}

	public static void setTarget(Vector3f v) {
		Light.target = v;
		ray = target.sub(position).normalize();
	}

	public static void setK(double k) {
		k = Math.max(0, Math.min(1, k));
		Light.k = Math.round(k * 100.0) / 100.0;
	}

	public static void setKs(double ks) {
		ks = Math.max(0, Math.min(1, ks));
		Light.ks = Math.round(ks * 100.0) / 100.0;
	}

	public static void setHighLightShine(double highLightShine) {
		highLightShine = Math.max(1, Math.min(64, highLightShine));
		Light.highLightShine = Math.round(highLightShine);
	}

	public static double getK() {
		return k;
	}

	public static double getKs() {
		return ks;
	}

	public static double getHighLightShine() {
		return highLightShine;
	}

	public static Color newRgb(Color start, Vector3f n, Vector3f cameraPos, Vector3f polyNormal) {
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

		if (ray.dotProduct(polyNormal.multiplyScalar(-1)) == 0) {
			l = 0.3;
		}

		double red = Math.max(0, Math.min(1, start.getRed() * (1 - k) + start.getRed() * k * l + highlight));
		double green = Math.max(0, Math.min(1, start.getGreen() * (1 - k) + start.getGreen() * k * l + highlight));
		double blue = Math.max(0, Math.min(1, start.getBlue() * (1 - k) + start.getBlue() * k * l + highlight));

		return new Color(red, green, blue, 1);
	}
}
