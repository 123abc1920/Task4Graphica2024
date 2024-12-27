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
		light = new Light();
		light.setPosition(new Vector3f(0, -100, 0));
		lights.add(light);
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

	public static Color addLight(Color start, Vector3f n, Vector3f vectorToCamera, Vector3f dot) {
		Color result = start;
		for (Light l : lights) {
			result = l.newRgb(result, n, vectorToCamera, dot);
		}

		return result;
	}

}
