package com.cgvsu.light;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;

import javafx.scene.paint.Color;

public class LightManager {
	private static List<Light> lights = new ArrayList<>();
	private static Light current;

	static {
		Light light = new Light();
		lights.add(light);
		current = light;
	}

	public static Light getLight(int id) {
		return lights.get(id);
	}

	public static void addNewLight(Light c) {
		lights.add(c);
	}

	public static void deleteLight(Light c) {
		lights.remove(c);
	}

	public static void deleteLight(int id) {
		lights.remove(id);
	}

	public static int getLightId(Light c) {
		return lights.indexOf(c);
	}

	public static int getLightsCount() {
		return lights.size();
	}

	public static Light getCurrent() {
		return current;
	}

	public static void setCurrent(int current) {
		LightManager.current = LightManager.lights.get(current);
	}

	public static String printLight(int id) {
		return "Источник " + id;
	}

	public static Color addLight(Color start, Vector3f n, Vector3f vectorToCamera, Vector3f[] vs,
			double[] barycentricCoords) {
		Color result = start;
		for (Light l : lights) {
			Vector3f[] rays = new Vector3f[3];

			try {
				rays[0] = vs[0].sub(l.position).normalize();
				rays[1] = vs[1].sub(l.position).normalize();
				rays[2] = vs[2].sub(l.position).normalize();
			} catch (Exception e) {
				rays[0] = new Vector3f();
				rays[1] = new Vector3f();
				rays[2] = new Vector3f();
			}

			Vector3f dot = new Vector3f();
			dot.setX(rays[0].getX() * barycentricCoords[0] + rays[1].getX() * barycentricCoords[1]
					+ rays[2].getX() * barycentricCoords[2]);
			dot.setY(rays[0].getY() * barycentricCoords[0] + rays[1].getY() * barycentricCoords[1]
					+ rays[2].getY() * barycentricCoords[2]);
			dot.setZ(rays[0].getZ() * barycentricCoords[0] + rays[1].getZ() * barycentricCoords[1]
					+ rays[2].getZ() * barycentricCoords[2]);

			result = l.newRgb(result, n, vectorToCamera, dot);
		}

		return result;
	}

	public static Light get(int i) {
		return lights.get(i);
	}

}
