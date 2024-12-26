package com.cgvsu.objreader;

import com.cgvsu.math.Vector2f;
import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.*;

public class ObjReader {

    private static final String OBJ_VERTEX_TOKEN = "v";
    private static final String OBJ_TEXTURE_TOKEN = "vt";
    private static final String OBJ_NORMAL_TOKEN = "vn";
    private static final String OBJ_FACE_TOKEN = "f";

    public static Model read(String fileContent) {
        Model result = new Model();
        Scanner scanner = new Scanner(fileContent);

        int lineInd = 0;

        while (scanner.hasNextLine()) {
            final String line = scanner.nextLine();
            ArrayList<String> wordsInLine = new ArrayList<String>(Arrays.asList(line.split("\\s+"))); // сплитим по пробелам
            if (wordsInLine.isEmpty()) {
                continue;
            }

            final String token = wordsInLine.get(0);
            wordsInLine.remove(0);

//            if (wordsInLine.get(0).equals("")) {
//                throw new ObjReaderException( ,lineInd);
//            }

            ++lineInd;
            switch (token) {
                // Для структур типа вершин методы написаны так, чтобы ничего не знать о внешней среде.
                // Они принимают только то, что им нужно для работы, а возвращают только то, что могут создать.
                // Исключение - индекс строки. Он прокидывается, чтобы выводить сообщение об ошибке.
                // Могло быть иначе. Например, метод parseVertex мог вместо возвращения вершины принимать вектор вершин
                // модели или сам класс модели, работать с ним.
                // Но такой подход может привести к большему количеству ошибок в коде. Например, в нем что-то может
                // тайно сделаться с классом модели.
                // А еще это портит читаемость
                // И не стоит забывать про тесты. Чем проще вам задать данные для теста, проверить, что метод рабочий,
                // тем лучше.
                /*case OBJ_VERTEX_TOKEN -> result.vertices.add(parseVertex(wordsInLine, lineInd));
				case OBJ_TEXTURE_TOKEN -> result.textureVertices.add(parseTextureVertex(wordsInLine, lineInd));
				case OBJ_NORMAL_TOKEN -> result.normals.add(parseNormal(wordsInLine, lineInd));
				case OBJ_FACE_TOKEN -> result.polygons.add(parseFace(wordsInLine, lineInd));*/
                case OBJ_VERTEX_TOKEN -> result.getVertices().add(parseVertex(wordsInLine, lineInd));
                case OBJ_TEXTURE_TOKEN -> result.getTextureVertices().add(parseTextureVertex(wordsInLine, lineInd));
                case OBJ_NORMAL_TOKEN -> result.getNormals().add(parseNormal(wordsInLine, lineInd));
                case OBJ_FACE_TOKEN -> result.getPolygons().add(parseFace(wordsInLine, lineInd, result.getVertices().size()));
                default -> {
                }
            }
        }
        checkModelAfterReading(result);
        return result;
    }

    // Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
    protected static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            if (!wordsInLineWithoutToken.get(3).equals("")) {
                throw new ObjReaderException("More than three coordinates are specified for the vertex.", lineInd);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            return new Vector3f(
                    Double.parseDouble(wordsInLineWithoutToken.get(0)),
                    Double.parseDouble(wordsInLineWithoutToken.get(1)),
                    Double.parseDouble(wordsInLineWithoutToken.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        } catch (IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few vertex arguments.", lineInd);
        }
    }

    protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            if (!wordsInLineWithoutToken.get(3).equals("")) {
                throw new ObjReaderException("More than two coordinates are specified for the texture vertex.", lineInd);
            }
        } catch (IndexOutOfBoundsException ignored) {
        }
        try {
            return new Vector2f(
                    Double.parseDouble(wordsInLineWithoutToken.get(0)),
                    Double.parseDouble(wordsInLineWithoutToken.get(1)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        } catch (IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few texture vertex arguments.", lineInd);
        }
    }

    protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
        try {
            if (!wordsInLineWithoutToken.get(3).equals("")) {
                throw new ObjReaderException("More than three coordinates are specified for the normal.", lineInd);
            }
        } catch (IndexOutOfBoundsException exception) {
        }
        try {
            return new Vector3f(
                    Double.parseDouble(wordsInLineWithoutToken.get(0)),
                    Double.parseDouble(wordsInLineWithoutToken.get(1)),
                    Double.parseDouble(wordsInLineWithoutToken.get(2)));
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse float value.", lineInd);
        } catch (IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few normal arguments.", lineInd);
        }
    }

    protected static Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int lineInd, int verticesAmount) {
        ArrayList<Integer> onePolygonVertexIndices = new ArrayList<Integer>();
        ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<Integer>();
        ArrayList<Integer> onePolygonNormalIndices = new ArrayList<Integer>();

        for (String s : wordsInLineWithoutToken) {
            parseFaceWord(s, onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd, verticesAmount);
        }

        Polygon result = new Polygon();
        result.setVertexIndices(onePolygonVertexIndices);
        result.setTextureVertexIndices(onePolygonTextureVertexIndices);
        result.setNormalIndices(onePolygonNormalIndices);
        return result;
    }

    // Обратите внимание, что для чтения полигонов я выделил еще один вспомогательный метод.
    // Это бывает очень полезно и с точки зрения структурирования алгоритма в голове, и с точки зрения тестирования.
    // В радикальных случаях не бойтесь выносить в отдельные методы и тестировать код из одной-двух строчек.
    protected static void parseFaceWord(
            String wordInLine,
            ArrayList<Integer> onePolygonVertexIndices,
            ArrayList<Integer> onePolygonTextureVertexIndices,
            ArrayList<Integer> onePolygonNormalIndices,
            int lineInd,
            int verticesAmount) {
        try {
            String[] wordIndices = wordInLine.split("/");
            // проверяем как у нас указаны вершины: используется обычная адресация или отрицательная, если обычная, то процесс обычный,
            // если отрицательная, то к отрицательному индексу прибавляем кол-во вершин (в obj-файле принято использоваться одинаковый тип индексации,
            // если вершины заданы отрицательной индексацией, то текстурные вершины и нормали, тоже должны быть заданы отрицательной адресацией, поэтому
            // достаточно проверить тип адресации только у вершин)
            boolean flag = Integer.parseInt(wordIndices[0]) < 0;
            switch (wordIndices.length) {
                case 1 -> {
                    if (flag) {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) + verticesAmount);
                    } else {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                    }
                }
                case 2 -> {
                    if (flag) {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) + verticesAmount);
                        onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) + verticesAmount);
                    } else {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                        onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                    }
                }
                case 3 -> {
                    if (flag) {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) + verticesAmount);
                        onePolygonNormalIndices.add(Integer.parseInt(wordIndices[2]) + verticesAmount);
                        if (!wordIndices[1].equals("")) {
                            onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) + verticesAmount);
                        }
                    } else {
                        onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
                        onePolygonNormalIndices.add(Integer.parseInt(wordIndices[2]) - 1);
                        if (!wordIndices[1].equals("")) {
                            onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
                        }
                    }
                }
                default -> {
                    throw new ObjReaderException("Invalid element size.", lineInd);
                }
            }
        } catch (NumberFormatException e) {
            throw new ObjReaderException("Failed to parse int value.", lineInd);
        } catch (IndexOutOfBoundsException e) {
            throw new ObjReaderException("Too few arguments.", lineInd);
        }
    }

    // номера вершин, текстурных вершин и нормалей начинаются с единицы
    protected static void checkModelAfterReading(Model model) {
        ArrayList<Vector3f> modelVertices = model.getVertices();
        ArrayList<Vector2f> modelTextureVertices = model.getTextureVertices();
        ArrayList<Vector3f> modelNormals = model.getNormals();
        ArrayList<Polygon> modelPolygons = model.getPolygons();
        int basedCounter = 0;

        if (modelVertices.size() < 3) {
            throw new RuntimeException("Model has 2 or fewer vertices."); // в моделе две вершины или меньше
        } else if (modelPolygons.size() == 0) {
            throw new RuntimeException("There is not a single polygon in the model."); // в моделе нет ни одного полигона
        }

        for (int i = 0; i < modelPolygons.size(); i++) {
            int counter = checkPolygon(model.getPolygons().get(i), i, modelVertices, modelTextureVertices, modelNormals);
            if (i == 0) {
                basedCounter = counter;
            } else {
                if (counter != basedCounter) {
                    throw new RuntimeException("Not all polygons in the model have a texture vertex or normal attached to them.");
                }
           }
        }
    }

    protected static int checkPolygon(Polygon polygon, int polygonIndex, ArrayList<Vector3f> modelVertices, ArrayList<Vector2f> modelTextureVertices, ArrayList<Vector3f> modelNormals) {
        int counter = 0;
        double[][] matrix3f = new double[3][3];
        matrix3f[0][0] = 1; matrix3f[0][1] = 1; matrix3f[0][2] = 1;

        ArrayList<Integer> polygonVertices = polygon.getVertexIndices();
        if (polygonVertices.size() < 3) {
            throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": it consists only of 2 or fewer vertices.");
        }

        ArrayList<Integer> polygonTextureVertices = polygon.getTextureVertexIndices();
        ArrayList<Integer> polygonNormals = polygon.getNormalIndices();
        if (polygonVertices.size() != polygonTextureVertices.size() && (polygonTextureVertices.size() != 0)) {
            throw new RuntimeException("Not all vertices of polygon number " + (polygonIndex + 1) + " have a texture vertex attached to them."); // не ко всем вершинам полигона номер i привязана текстурная вершина
        } else if (polygonVertices.size() != polygonNormals.size() && (polygonNormals.size() != 0)) {
            throw new RuntimeException("Not all vertices of polygon number " + (polygonIndex + 1) + " have a normals attached to them."); // не ко всем вершинам полигона номер i привяза нормаль
        }

        if (polygonTextureVertices.size() != 0) {
            counter+=2;
        }
        if (polygonNormals.size() != 0) {
            counter+=3;
        }

        for (int j = 0; j < polygonVertices.size() - 2; j++) {

            for (int k = j + 1; k < polygonVertices.size() - 1; k++) {
                Vector3f firstPoint3D;
                try {
                    firstPoint3D = modelVertices.get(polygonVertices.get(k));
                } catch (IndexOutOfBoundsException exception) {
                    throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no vertex with number " + (polygonVertices.get(k) + 1) + " in the file.");
                }

                Vector3f firstVector3f;
                try {
                    firstVector3f = new Vector3f(firstPoint3D.getX() - modelVertices.get(polygonVertices.get(0)).getX(), firstPoint3D.getY() - modelVertices.get(polygonVertices.get(0)).getY(), firstPoint3D.getZ() - modelVertices.get(polygonVertices.get(0)).getZ());
                } catch (IndexOutOfBoundsException exception) {
                    throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no vertex with number " + (polygonVertices.get(0) + 1) + " in the file.");
                }
                matrix3f[1][0] = firstVector3f.getX(); matrix3f[1][1] = firstVector3f.getY(); matrix3f[1][2] = firstVector3f.getZ();

                for (int l = k + 1; l < polygonVertices.size(); l++) {
                    Vector3f secondPoint3D;
                    try {
                        secondPoint3D = modelVertices.get(polygonVertices.get(l));
                    } catch (IndexOutOfBoundsException exception) {
                        throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no vertex with number " + (polygonVertices.get(l) + 1) + " in the file.");
                    }

                    Vector3f secondVector3f = new Vector3f(secondPoint3D.getX() - modelVertices.get(polygonVertices.get(0)).getX(), secondPoint3D.getY() - modelVertices.get(polygonVertices.get(0)).getY(), secondPoint3D.getZ() - modelVertices.get(polygonVertices.get(0)).getZ());
                    matrix3f[2][0] = secondVector3f.getX(); matrix3f[2][1] = secondVector3f.getY(); matrix3f[2][2] = secondVector3f.getZ();
                    Vector3f vectorProduct = new Vector3f(matrix3f[1][1] * matrix3f[2][2] - matrix3f[1][2] * matrix3f[2][1],
                            matrix3f[1][2] * matrix3f[2][0] - matrix3f[1][0] * matrix3f[2][2],
                            matrix3f[1][0] * matrix3f[2][1] - matrix3f[1][1] * matrix3f[2][0]);
                    if (vectorProduct.getX() == 0 && vectorProduct.getY() == 0 && vectorProduct.getZ() == 0) {
                        throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": some of its points lie on the same line or are identical.");
                    }

//                    if (l < polygonVertices.size() - 1) {
//                        for (int m = l + 1; m < polygonVertices.size(); m++) {
//                            Vector3f thirdPoint3D;
//                            try {
//                                thirdPoint3D = modelVertices.get(polygonVertices.get(m));
//                            } catch (IndexOutOfBoundsException exception) {
//                                throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no vertex with number " + (polygonVertices.get(m) + 1) + " in the file.");
//                            }
//
//                            Vector3f forthVector3f = new Vector3f(thirdPoint3D.getX() - modelVertices.get(polygonVertices.get(0)).getX(), thirdPoint3D.getY() - modelVertices.get(polygonVertices.get(0)).getY(), thirdPoint3D.getZ() - modelVertices.get(polygonVertices.get(0)).getZ());
//                            // для вещественных использовать эпсилон либо целое число большее нуля, так как точки могут лежать в одной плоскости с некоторым отклонением, из-за чего
//                            //значение скалярного произведения может быть даже > 1
//                            if (Math.abs(vectorProduct.dotProduct(forthVector3f)) != 0/*3 1e-6*/) { //если ноль, то лежит в одной плоскости
//                                throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": its points do not lie in the same plane.");
//                            }
//                        }
//                    }
                }
            }

            for (int k = 0; k < polygonTextureVertices.size(); k++) {
                if (polygonTextureVertices.get(k) >= modelTextureVertices.size() || polygonTextureVertices.get(k) == -1) {
                    throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no texture vertex with number " + (polygonTextureVertices.get(k) + 1) + " in the file."); // в файле нет текстурной вершины с номером k
                }
            }

            for (int l = 0; l < polygonNormals.size(); l++) {
                if (polygonNormals.get(l) >= modelNormals.size() || polygonNormals.get(l) == -1) {
                    throw new RuntimeException("For polygon #" + (polygonIndex + 1) + ": there is no normal with number " + (polygonNormals.get(l) + 1) + " in the file."); // в файле нет нормали с номером l
                }
            }
        }
        return counter;
    }
}