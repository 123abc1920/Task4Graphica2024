package com.cgvsu.rasterization;

import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.CRIMSON, Color.CRIMSON, Color.CRIMSON };
	public static ZBuffer zBuffer;

	public static boolean drawLine = false;
	public static boolean drawTexture = false;
	public static boolean isLight = true;

	public static void setColor(Color color) {
		colors[0] = colors[1] = colors[2] = color;
	}

	public static void setLine(boolean drawLine) {
		RasterizationParams.drawLine = drawLine;
	}

	public static void setTexture(boolean drawTexture) {
		RasterizationParams.drawTexture = drawTexture;
	}

	public static void setLight(boolean isLight) {
		RasterizationParams.isLight = isLight;
	}

}
