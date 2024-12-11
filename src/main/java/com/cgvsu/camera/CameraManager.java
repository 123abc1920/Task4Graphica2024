package com.cgvsu.camera;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;

public class CameraManager {
	private static List<Camera> cameras = new ArrayList<>();
	private static Camera activeCamera = null;

	static {
		CameraManager.addCamera(new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100));
		CameraManager.addCamera(new Camera(new Vector3f(10, 10, 50), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100));
		activeCamera = CameraManager.getCamera(0);
	}

	public static Camera getActive() {
		return activeCamera;
	}

	public static void setActiveCamera(Camera c) {
		CameraManager.activeCamera = c;
	}

	public static void setActiveCamera(int id) {
		CameraManager.activeCamera = CameraManager.getCamera(id);
	}

	public static Camera getCamera(int id) {
		return cameras.get(id);
	}

	public static void addCamera(Camera c) {
		cameras.add(c);
	}

	public static void deleteCamera(Camera c) {
		cameras.remove(c);
	}

	public static void deleteCamera(int id) {
		cameras.remove(id);
	}

	public static int getCameraId(Camera c) {
		return cameras.indexOf(c);
	}

	public static int getCamerasCount() {
		return cameras.size();
	}

	public static String printCamera(int id) {
		return "Камера " + id;
	}
}
