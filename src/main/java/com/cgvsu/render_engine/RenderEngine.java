package com.cgvsu.render_engine;

import java.util.ArrayList;
import java.util.List;

import com.cgvsu.math.Vector3f;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import com.cgvsu.camera.Camera;
import com.cgvsu.math.Matrix4f;
import com.cgvsu.math.Vector2f;
import com.cgvsu.model.Model;
import com.cgvsu.normals.FindNormals;
import com.cgvsu.rasterization.Line;
import com.cgvsu.rasterization.RasterizationParams;
import com.cgvsu.rasterization.TextureRasterization;
import com.cgvsu.rasterization.TriangleRasterization;
import com.cgvsu.zbuffer.ZBuffer;

import static com.cgvsu.render_engine.GraphicConveyor.*;

public class RenderEngine {

	public static void render(final GraphicsContext graphicsContext, final Camera camera, final Model mesh,
			final int width, final int height) {
		Matrix4f modelMatrix = translateRotateScale();
		Matrix4f viewMatrix = camera.getViewMatrix();
		Matrix4f projectionMatrix = camera.getProjectionMatrix();

		Matrix4f projectionViewMatrix = projectionMatrix.multiplyMatrix(viewMatrix);
		Matrix4f projectionViewModelMatrix = projectionViewMatrix.multiplyMatrix(modelMatrix);

		RasterizationParams.zBuffer = new ZBuffer(width, height);

		final int nPolygons = mesh.polygons.size();
		for (int polygonInd = 0; polygonInd < nPolygons; ++polygonInd) {
			final int nVerticesInPolygon = mesh.polygons.get(polygonInd).getVertexIndices().size();
			ArrayList<Vector3f> resultPoints = new ArrayList<>();
			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				Vector3f vertex = mesh.vertices
						.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));

				Vector3f vertexVecmath = new Vector3f(vertex.getX(), vertex.getY(), vertex.getZ());
				Vector3f resultPoint = vertexToPoint(projectionViewModelMatrix.multiplyVectorDivW(vertexVecmath), width,
						height);
				resultPoints.add(resultPoint);
			}

			for (int i = 0; i < resultPoints.size(); i++) {
				resultPoints.get(i).setX(Math.max(0, resultPoints.get(i).getX()));
				resultPoints.get(i).setY(Math.max(0, resultPoints.get(i).getY()));
				resultPoints.get(i).setZ(Math.max(0, resultPoints.get(i).getZ()));
			}

			Vector3f[] normals = new Vector3f[3];

			double[] arrX = new double[3];
			double[] arrY = new double[3];
			double[] arrZ = new double[3];
			for (int vertexInPolygonInd = 0; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
				arrX[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getX();
				arrY[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getY();
				arrZ[vertexInPolygonInd] = resultPoints.get(vertexInPolygonInd).getZ();
				normals[vertexInPolygonInd] = mesh.normals
						.get(mesh.polygons.get(polygonInd).getVertexIndices().get(vertexInPolygonInd));
			}

			boolean drawnPolygon = false;

			Vector3f polyNormal = FindNormals.findPolygonsNormals(
					mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(0)),
					mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(1)),
					mesh.vertices.get(mesh.polygons.get(polygonInd).getVertexIndices().get(2)));

			if (!RasterizationParams.getDrawTexture(mesh)) {
				drawnPolygon = drawSolidColor(graphicsContext, arrX, arrY, arrZ, normals, camera.getPosition(),
						polyNormal, RasterizationParams.getIsLight());
			} else {
				Vector2f[] texturePoints = new Vector2f[3];
				texturePoints[0] = mesh.textureVertices
						.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(0));
				texturePoints[1] = mesh.textureVertices
						.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(1));
				texturePoints[2] = mesh.textureVertices
						.get(mesh.polygons.get(polygonInd).getTextureVertexIndices().get(2));
				drawnPolygon = drawTexture(graphicsContext, arrX, arrY, arrZ, texturePoints, mesh.image);
			}

			if (RasterizationParams.getDrawLine()) {
				if (drawnPolygon) {
					drawLines(graphicsContext, nVerticesInPolygon, resultPoints, RasterizationParams.zBuffer);
				}
			}

		}
	}

	private static boolean drawTexture(GraphicsContext graphicsContext, double[] arrX, double[] arrY, double[] arrZ,
			Vector2f[] texturePoints, Image image) {
		return TextureRasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, texturePoints,
				RasterizationParams.zBuffer, image);

	}

	private static boolean drawSolidColor(GraphicsContext graphicsContext, double[] arrX, double[] arrY, double[] arrZ,
			Vector3f[] normals, Vector3f cameraPos, Vector3f polyNormal, boolean isLight) {
		return TriangleRasterization.fillTriangle(graphicsContext, arrX, arrY, arrZ, RasterizationParams.colors,
				normals, polyNormal, RasterizationParams.zBuffer, cameraPos, isLight);
	}

	private static void drawLines(GraphicsContext graphicsContext, int nVerticesInPolygon, List<Vector3f> resultPoints,
			ZBuffer zbuffer) {

		for (int vertexInPolygonInd = 1; vertexInPolygonInd < nVerticesInPolygon; ++vertexInPolygonInd) {
			Line.drawLine(graphicsContext, resultPoints.get(vertexInPolygonInd - 1).getX(),
					resultPoints.get(vertexInPolygonInd - 1).getY(), resultPoints.get(vertexInPolygonInd).getX(),
					resultPoints.get(vertexInPolygonInd).getY(), resultPoints.get(vertexInPolygonInd - 1).getZ(),
					resultPoints.get(vertexInPolygonInd).getZ(), zbuffer);
		}

		Line.drawLine(graphicsContext, resultPoints.get(nVerticesInPolygon - 1).getX(),
				resultPoints.get(nVerticesInPolygon - 1).getY(), resultPoints.get(0).getX(), resultPoints.get(0).getY(),
				resultPoints.get(nVerticesInPolygon - 1).getZ(), resultPoints.get(0).getZ(), zbuffer);
	}
}