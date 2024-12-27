package com.cgvsu.light;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
	private static List<Light> lights = new ArrayList<>();

	public static Light getLight(int id) {
		return lights.get(id);
	}

	public static void addLight(Light c) {
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

	public static int getlightsCount() {
		return lights.size();
	}
}
