package com.cgvsu.objwriter;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ObjWriter {
    public static String write(List<Model> modelList, boolean[] isSelected) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < modelList.size(); i++) {
            if (isSelected[i]) {
                stringBuilder.append(writeVertices(modelList.get(i).getVertices()));
            }
        }

        for (int i = 0; i < modelList.size(); i++) {
            if (isSelected[i]) {
                stringBuilder.append(writeTextureVertices(modelList.get(i).getTextureVertices()));
            }
        }

        for (int i = 0; i < modelList.size(); i++) {
            if (isSelected[i]) {
                stringBuilder.append(writeNormals(modelList.get(i).getNormals()));
            }

        }
        for (int i = 0; i < modelList.size(); i++) {
            if (isSelected[i]) {
                Model model = modelList.get(i);
                stringBuilder.append(writePolygons(model.getPolygons(), modelList, i, isSelected));
            }

        }
        return stringBuilder.toString();
    }

    protected static String writeVertices(ArrayList<Vector3f> vertices) {
        StringBuilder stringVertices = new StringBuilder();
        for (Vector3f vertex : vertices) {
            stringVertices.append(String.format(Locale.US, "v %.4f %.4f %.4f\n", vertex.getX(), vertex.getY(), vertex.getZ()));
        }
        return stringVertices.toString();
    }

    protected static String writeTextureVertices(ArrayList<Vector2f> textureVertices) {
        StringBuilder stringTextureVertices = new StringBuilder();
        for (Vector2f textureVertex : textureVertices) {
            stringTextureVertices.append(String.format(Locale.US, "vt %.4f %.4f\n", textureVertex.getX(), textureVertex.getY()));
        }
        return stringTextureVertices.toString();
    }

    protected static String writeNormals(ArrayList<Vector3f> normals) {
        StringBuilder stringNormals = new StringBuilder();
        for (Vector3f normal : normals) {
            stringNormals.append(String.format(Locale.US, "vn %.4f %.4f %.4f\n", normal.getX(), normal.getY(), normal.getZ()));
        }
        return stringNormals.toString();
    }

    protected static String writePolygons(ArrayList<Polygon> polygons, List<Model> modelList, int index, boolean[] isSelected) {
        int verticesGeneralCount = 0;
        int textureVerticesGeneralCount = 0;
        int normalsGeneralCount = 0;
        for (int i = 0; i < index; i++) {
            if (isSelected[i]) {
                verticesGeneralCount += modelList.get(i).getVertices().size();
                textureVerticesGeneralCount += modelList.get(i).getTextureVertices().size();
                normalsGeneralCount += modelList.get(i).getNormals().size();
            }
        }
        StringBuilder stringPolygons = new StringBuilder();
        for (Polygon polygon : polygons) {
            List<Integer> vertexIndices = polygon.getVertexIndices();
            List<Integer> textureVertexIndices = polygon.getTextureVertexIndices();
            List<Integer> normalIndices = polygon.getNormalIndices();

            stringPolygons.append("f");

            for (int i = 0; i < vertexIndices.size(); i++) {
                int vertexIndex = vertexIndices.get(i) + 1 + verticesGeneralCount; // Переключение с 0-индекса на 1
                String faceElement = String.valueOf(vertexIndex);

                if (!textureVertexIndices.isEmpty()) {
                    int textureIndex = textureVertexIndices.get(i) + 1 + textureVerticesGeneralCount;
                    faceElement += "/" + textureIndex;
                }
                if (!normalIndices.isEmpty()) {
                    int normalIndex = normalIndices.get(i) + 1 + normalsGeneralCount;
                    if (textureVertexIndices.isEmpty()) {
                        faceElement += "/";
                    }
                    faceElement += "/" + normalIndex;
                }

                stringPolygons.append(" ").append(faceElement);
            }

            stringPolygons.append("\n");
        }
        return stringPolygons.toString();
    }
}
