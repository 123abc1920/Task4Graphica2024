package com.cgvsu.rasterization;

import com.cgvsu.model.Model;
import com.cgvsu.zbuffer.ZBuffer;

import javafx.scene.paint.Color;

public class RasterizationParams {

	public static Color[] colors = { Color.CRIMSON, Color.CRIMSON, Color.CRIMSON };
	public static ZBuffer zBuffer;

	private static boolean drawLine = false;
	private static boolean drawTexture = true;
	private static boolean isLight = true;

	public static void setColor(Color color) {
		colors[0] = colors[1] = colors[2] = color;
	}

	public static boolean getDrawTexture(Model m) {
		if (m.image == null) {
			return false;
		}
		return drawTexture;
	}

	public static boolean getIsLight() {
		return isLight;
	}

	public static boolean getDrawLine() {
		return drawLine;
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
