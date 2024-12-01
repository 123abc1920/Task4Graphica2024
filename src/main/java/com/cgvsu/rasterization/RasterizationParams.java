package com.cgvsu.rasterization;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.AQUA, Color.BLACK, Color.BLANCHEDALMOND };
	public static ZBuffer zBuffer;

	public static boolean drawLine = false;
	public static boolean drawTexture = false;

	public static void setColor(Color color) {
		colors[0] = color;
	}

}
