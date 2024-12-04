package com.cgvsu.light;

import com.cgvsu.math.Global;
import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class Light {
	public static Vector3f position = new Vector3f(0, 0, 100);
	public static Vector3f target = new Vector3f(0, 0, 0);
	public static Vector3f ray = target.sub(position);

	public static double k = 0.3;

	static {
		ray.normalize();
	}

	public static Color newRgb(Color start, Vector3f n, Vector3f cameraPos) {
		double l = ray.dotProduct(n.multiplyScalar(-1));

		if (l < 0)
			l = 0;

		Vector3f R = ray.sub(n.multiplyScalar(2 * ray.dotProduct(n)));
		R = R.normalize();
		ray = ray.normalize();
		cameraPos = cameraPos.normalize();

		double ks = 0.4;
		double shine = 31;
		double r = R.dotProduct(cameraPos);
		double I_spec = ks * Math.pow(r, shine);
		if (r < 0) {
			I_spec = 0;
		}

		double red = Math.max(0, Math.min(1, start.getRed() * (1 - k) + start.getRed() * k * l + I_spec));
		double green = Math.max(0, Math.min(1, start.getGreen() * (1 - k) + start.getGreen() * k * l + I_spec));
		double blue = Math.max(0, Math.min(1, start.getBlue() * (1 - k) + start.getBlue() * k * l + I_spec));

		return new Color(red, green, blue, 1);
	}
}
