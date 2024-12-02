package com.cgvsu.rasterization;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.AQUA, Color.CRIMSON, Color.BLUEVIOLET };
	public static ZBuffer zBuffer;

	public static boolean drawLine = false;
	public static boolean drawTexture = true;

	public static void setColor(Color color) {
		colors[0] = color;
	}

}
