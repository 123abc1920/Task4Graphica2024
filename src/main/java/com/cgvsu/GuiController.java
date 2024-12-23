package com.cgvsu;

import com.cgvsu.light.Light;
import com.cgvsu.rasterization.RasterizationParams;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.triangulation.Triangulation;

import javafx.fxml.FXML;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import com.cgvsu.camera.Camera;
import com.cgvsu.camera.CameraManager;
import com.cgvsu.math.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.normals.FindNormals;
import org.w3c.dom.Text;

public class GuiController {

    final private float TRANSLATION = 0.5F; // 10.0F;
    private boolean isMoveModeEnabled = false;
    private boolean isRotateModeEnabled = false;
    private boolean isScalingModeEnabled = false;

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Canvas canvas;

    @FXML
    private VBox vBoxMain;

    @FXML
    private TextField textFieldPosX;

    @FXML
    private TextField textFieldPosY;

    @FXML
    private TextField textFieldPosZ;

    @FXML
    private TextField textFieldTarX;

    @FXML
    private TextField textFieldTarY;

    @FXML
    private TextField textFieldTarZ;

    @FXML
    private CheckBox checkBoxLines;

    @FXML
    private CheckBox checkBoxLight;

    @FXML
    private CheckBox checkBoxTexture;

    @FXML
    private ColorPicker colorPickerModel;

    @FXML
    private ColorPicker colorPickerLines;

    @FXML
    private Button deleteTextureBtn;

    @FXML
    private Button chooseFileBtn;

    @FXML
    private Button addCameraBtn;

    @FXML
    private Button deleteCameraBtn;

    @FXML
    private TextField textureText;

    @FXML
    private ChoiceBox<String> cameraList;

    @FXML
    private ScrollPane scrollPaneMain;

    @FXML
    private TextField textFieldPosLightX;

    @FXML
    private TextField textFieldPosLightY;

    @FXML
    private TextField textFieldPosLightZ;

    @FXML
    private TextField textFieldTarLightX;

    @FXML
    private TextField textFieldTarLightY;

    @FXML
    private TextField textFieldTarLightZ;

    @FXML
    private TextField textFieldLightSourceStrength;

    @FXML
    private TextField textFieldGlareBrightness;

    @FXML
    private TextField textFieldGlareArea;

    @FXML
    private TextField fovTextField;

    @FXML
    private TextField nearTextField;

    @FXML
    private TextField farTextField;

    @FXML
    private TextField scaleModelX;
    @FXML
    private TextField scaleModelY;
    @FXML
    private TextField scaleModelZ;
    @FXML
    private TextField rotateModelX;
    @FXML
    private TextField rotateModelY;
    @FXML
    private TextField rotateModelZ;
    @FXML
    private TextField translateModelX;
    @FXML
    private TextField translateModelY;
    @FXML
    private TextField translateModelZ;

    private boolean isInLightMode = true;

    private boolean isPanelRemoved = true;

    private Model mesh = null;

    private Camera camera = CameraManager.getActive();

    private Timeline timeline;

    private double startXLMB;

    private double startYLMB;

    private double endXLMB;

    private double endYLMB;

    private double startXRMB;

    private double startYRMB;

    private double endXRMB;

    private double endYRMB;

    private double width, height;

    @FXML
    private void initialize() {

        anchorPane.prefWidthProperty().addListener(
                (ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue() - scrollPaneMain.getWidth()));
        anchorPane.prefHeightProperty()
                .addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

        timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
            width = canvas.getWidth();
            height = canvas.getHeight();

            camera = CameraManager.getActive();

            canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
            camera.setAspectRatio((float) (width / height));

            canvas.setWidth(anchorPane.getWidth() - scrollPaneMain.getWidth());

            if (mesh != null) {
                RasterizationParams.drawBuffer.draw();
            }
        });


        timeline.getKeyFrames().add(frame);
        timeline.play();

        anchorPane.getStylesheets().add(getClass().getResource("styles/light_mode.css").toExternalForm());
        prepareGUI();

        for (int i = 0; i < CameraManager.getCamerasCount(); i++) {
            cameraList.getItems().add(CameraManager.printCamera(i));
        }

        cameraList.setValue(cameraList.getItems().get(0));

        cameraList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if (!number2.equals(-1)) {
                    CameraManager.setActiveCamera(CameraManager.getCamera((Integer) number2));
                    camera = CameraManager.getActive();
                    uploadInfoInTextFields();
                }
            }
        });
    }

    private void prepareGUI() {
        uploadInfoInTextFields();
        checkBoxLight.setSelected(true);

        colorPickerLines.setDisable(true);
        colorPickerModel.setDisable(true);

        checkBoxLines.setDisable(true);
        checkBoxLight.setDisable(true);
        checkBoxTexture.setDisable(true);

        textureText.setDisable(true);
        chooseFileBtn.setDisable(true);
        deleteTextureBtn.setDisable(true);

        textFieldLightSourceStrength.setText(Light.getK() + "");
        textFieldGlareArea.setText(Light.getHighLightShine() + "");
        textFieldGlareBrightness.setText(Light.getKs() + "");

        fovTextField.setText(camera.getFov() + "");
        nearTextField.setText(camera.getNearPlane() + "");
        farTextField.setText(camera.getFarPlane() + "");

        scrollPaneMain.setVisible(true); // изменить
    }

    private void showModelInfo() {
        colorPickerLines.setValue(RasterizationParams.getLineColor());
        colorPickerModel.setValue(mesh.getColor());
        colorPickerLines.setDisable(false);
        colorPickerModel.setDisable(false);

        checkBoxLines.setDisable(false);
        checkBoxLight.setDisable(false);
        checkBoxTexture.setDisable(false);

        chooseFileBtn.setDisable(false);
        deleteTextureBtn.setDisable(false);
    }

    private void uploadInfoInTextFields() {
        textFieldPosX.setText(Double.toString((double) Math.round((camera.getPosition().getX() * 100)) / 100));
        textFieldPosY.setText(Double.toString((double) (Math.round(camera.getPosition().getY() * 100)) / 100));
        textFieldPosZ.setText(Double.toString((double) (Math.round(camera.getPosition().getZ() * 100)) / 100));

        textFieldTarX.setText(Double.toString((double) (Math.round(camera.getTarget().getX() * 100)) / 100));
        textFieldTarY.setText(Double.toString((double) (Math.round(camera.getTarget().getY() * 100)) / 100));
        textFieldTarZ.setText(Double.toString((double) (Math.round(camera.getTarget().getZ() * 100)) / 100));

        textFieldPosLightX.setText(Double.toString((double) Math.round((Light.position.getX() * 100)) / 100));
        textFieldPosLightY.setText(Double.toString((double) Math.round((Light.position.getY() * 100)) / 100));
        textFieldPosLightZ.setText(Double.toString((double) Math.round((Light.position.getZ() * 100)) / 100));

        textFieldTarLightX.setText(Double.toString((double) Math.round((Light.target.getX() * 100)) / 100));
        textFieldTarLightY.setText(Double.toString((double) Math.round((Light.target.getY() * 100)) / 100));
        textFieldTarLightZ.setText(Double.toString((double) Math.round((Light.target.getZ() * 100)) / 100));

        fovTextField.setText(camera.getFov() + "");
        nearTextField.setText(camera.getNearPlane() + "");
        farTextField.setText(camera.getFarPlane() + "");

//        textFieldLightSourceStrength.setText(Double.toString((double) Math.round((Light.getK * 100)) / 100));
//        textFieldGlareBrightness.setText(Double.toString((double) Math.round((Light.getKs * 100)) / 100));
//        textFieldGlareArea.setText(Double.toString((double) Math.round((Light.getHighLightShine * 100)) / 100));
    }

    private Alert createAlertWithError(String valueName, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setContentText(valueName + "\n" + errorMessage);
        return alert;
    }

    @FXML
    private void handleTextFieldLightAction(ActionEvent actionEvent) {
        TextField source = (TextField) actionEvent.getSource();
        String textFieldText = source.getText();
        double newValue = 0d;
        try {
            newValue = Double.parseDouble(textFieldText);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (source.equals(textFieldGlareArea)) {
                newValue += 1;
            }
            source.setText(Double.toString(newValue));
            Alert alert = createAlertWithError("Не удалось запарсить значение double.", errorMessage);
            alert.showAndWait();
        }
        if (source.equals(textFieldLightSourceStrength)) {
            if (newValue >= 1) {
                newValue = 1d;
            } else if (newValue <= 0) {
                newValue = 0d;
            }
            Light.setK(newValue);
        } else if (source.equals(textFieldGlareBrightness)) {
            if (newValue >= 1) {
                newValue = 1d;
            } else if (newValue <= 0) {
                newValue = 0d;
            }
            Light.setKs(newValue);
        } else if (source.equals(textFieldGlareArea)) {
            if (newValue >= 50) {
                newValue = 50d;
            } else if (newValue <= 1) {
                newValue = 1d;
            }
            Light.setHighLightShine(newValue);
        }
        source.setText(Double.toString(newValue).trim());
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void handleTextFieldCoordinatesAction(ActionEvent actionEvent) {
        TextField source = (TextField) actionEvent.getSource();
        String textFieldText = source.getText();
        double coordinate = 0d;
        try {
            coordinate = Double.parseDouble(textFieldText);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            source.setText(Double.toString(coordinate));
            Alert alert = createAlertWithError("Не удалось запарсить значение double.", errorMessage);
            alert.showAndWait();
        }
        if (source.equals(textFieldPosX)) {
            camera.setPosition(new Vector3f(coordinate, camera.getPosition().getY(), camera.getPosition().getZ()));
        } else if (source.equals(textFieldPosY)) {
            camera.setPosition(new Vector3f(camera.getPosition().getX(), coordinate, camera.getPosition().getZ()));
        } else if (source.equals(textFieldPosZ)) {
            camera.setPosition(new Vector3f(camera.getPosition().getX(), camera.getPosition().getY(), coordinate));
        } else if (source.equals(textFieldTarX)) {
            camera.setTarget(new Vector3f(coordinate, camera.getTarget().getY(), camera.getTarget().getZ()));
        } else if (source.equals(textFieldTarY)) {
            camera.setTarget(new Vector3f(camera.getTarget().getX(), coordinate, camera.getTarget().getZ()));
        } else if (source.equals(textFieldTarZ)) {
            camera.setTarget(new Vector3f(camera.getTarget().getX(), camera.getTarget().getY(), coordinate));
        } else if (source.equals(textFieldPosLightX)) {
            Light.setPosition(new Vector3f(coordinate, Light.position.getY(), Light.position.getZ()));
        } else if (source.equals(textFieldPosLightY)) {
            Light.setPosition(new Vector3f(Light.position.getX(), coordinate, Light.position.getZ()));
        } else if (source.equals(textFieldPosLightZ)) {
            Light.setPosition(new Vector3f(Light.position.getX(), Light.position.getY(), coordinate));
        } else if (source.equals(textFieldTarLightX)) {
            Light.setTarget(new Vector3f(coordinate, Light.target.getY(), Light.target.getZ()));
        } else if (source.equals(textFieldTarLightY)) {
            Light.setTarget(new Vector3f(Light.target.getX(), coordinate, Light.target.getZ()));
        } else if (source.equals(textFieldTarLightZ)) {
            Light.setTarget(new Vector3f(Light.target.getX(), Light.target.getY(), coordinate));
        }
        source.setText(Double.toString(coordinate).trim());
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void handleAddCameraBtnAction(ActionEvent actionEvent) {
        CameraManager.addCamera(new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100));
        cameraList.getItems().add(CameraManager.printCamera(CameraManager.getCamerasCount() - 1));
    }

    @FXML
    private void handleChooseFileBtnAction(ActionEvent actionEvent) {
        if (mesh != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter("Images (*.png, *.jpg)", "*.png", "*.jpg"));
            fileChooser.setTitle("Choose texture image");
            File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
            if (file == null) {
                return;
            }

            Path fileName = Path.of(file.getAbsolutePath());
            mesh.setTexture(fileName.toString());
        }
        remakeModel();
    }

    @FXML
    private void handleDeleteCameraBtnAction(ActionEvent actionEvent) {
        CameraManager.deleteCamera(CameraManager.getActive());
        CameraManager.setActiveCamera(0);
        cameraList.getItems().clear();
        for (int i = 0; i < CameraManager.getCamerasCount(); i++) {
            cameraList.getItems().add(CameraManager.printCamera(i));
        }
        cameraList.setValue(cameraList.getItems().get(0));
        remakeModel();
    }

    @FXML
    private void handleAddTextureBtnAction(ActionEvent actionEvent) {
        if (mesh != null) {
            mesh.setTexture(textureText.getText());
        }
        remakeModel();
    }

    @FXML
    private void handleDeleteTextureBtnAction(ActionEvent actionEvent) {
        if (mesh != null) {
            mesh.deleteTexture();
        }
        remakeModel();
    }

    @FXML
    private void handleColorPickerLinesAction(ActionEvent actionEvent) {
        Color newLinesColor = colorPickerLines.getValue();
        RasterizationParams.setLineColor(newLinesColor);
        remakeModel();
    }

    @FXML
    private void handleColorPickerModelAction(ActionEvent actionEvent) {
        Color newModelColor = colorPickerModel.getValue();
        mesh.setColor(newModelColor);
        remakeModel();
    }

    @FXML
    private void handleCheckBoxLinesAction(ActionEvent actionEvent) {
        if (checkBoxLines.isSelected()) {
            RasterizationParams.setLine(true);
        } else {
            RasterizationParams.setLine(false);
        }
        remakeModel();
    }

    @FXML
    private void handleCheckBoxLightAction(ActionEvent actionEvent) {
        if (checkBoxLight.isSelected()) {
            RasterizationParams.setLight(true);
        } else {
            RasterizationParams.setLight(false);
        }
        remakeModel();
    }

    @FXML
    private void setFovAction() {
        String newFov = fovTextField.getText();
        Double newFovDouble = 1.0d;

        try {
            newFovDouble = Double.parseDouble(newFov);
        } catch (Exception e) {
            fovTextField.setText(camera.getFov() + "");
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение угла.", errorMessage);
            alert.showAndWait();
        }

        camera.setFov(newFovDouble);
        fovTextField.setText(camera.getFov() + "");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void setNearAction() {
        String newNear = nearTextField.getText();
        Double newNearDouble = 0.01d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            nearTextField.setText(camera.getNearPlane() + "");
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }

        camera.setNearPlane(newNearDouble);
        nearTextField.setText(camera.getNearPlane() + "");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void setFarAction() {
        String newFar = farTextField.getText();
        Double newFarDouble = 100d;

        try {
            newFarDouble = Double.parseDouble(newFar);
        } catch (Exception e) {
            farTextField.setText(camera.getFarPlane() + "");
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение дальнего плана.", errorMessage);
            alert.showAndWait();
        }

        camera.setFarPlane(newFarDouble);
        farTextField.setText(camera.getFarPlane() + "");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void handleCheckBoxTextureAction(ActionEvent actionEvent) {
        if (checkBoxTexture.isSelected()) {
            RasterizationParams.setTexture(true);
        } else {
            RasterizationParams.setTexture(false);
        }
    }

    @FXML
    private void handleMousePressed(MouseEvent mouseEvent) {
        anchorPane.requestFocus();
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            startXLMB = mouseEvent.getSceneX();
            startYLMB = mouseEvent.getSceneY();
        } /*
         * else if (mouseEvent.getButton() == MouseButton.SECONDARY) { startXRMB =
         * mouseEvent.getSceneX(); startYRMB = mouseEvent.getSceneY(); }
         */
    }

    @FXML
    private void handleMouseClickedVBoxMain(MouseEvent mouseEvent) {
        vBoxMain.requestFocus();
    }

    @FXML
    private void handleMouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.getButton() == MouseButton.PRIMARY) {
            endXLMB = mouseEvent.getSceneX();
            endYLMB = mouseEvent.getSceneY();
            double shiftXLBM = endXLMB - startXLMB;
            double shiftYLMB = endYLMB - startYLMB;

            camera.movePosition(new Vector3f((float) shiftXLBM * 0.05f, (float) shiftYLMB * 0.05f, 0));
            camera.moveTarget(new Vector3f((float) shiftXLBM * 0.05f, (float) shiftYLMB * 0.05f, 0));

            startXLMB = mouseEvent.getSceneX();
            startYLMB = mouseEvent.getSceneY();
            uploadInfoInTextFields();
            remakeModel();
        }
        /*
         * else if (mouseEvent.getButton() == MouseButton.SECONDARY) { endXRMB =
         * mouseEvent.getX(); endYRMB = mouseEvent.getY(); double shiftXRBM = endXRMB -
         * startXRMB; double shiftYRMB = endYRMB - startYRMB; }
         */
    }

    @FXML
    private void handleMouseScroll(ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() > 0) {
            camera.movePosition(new Vector3f(0, 0, -TRANSLATION * 10));
            uploadInfoInTextFields();
        } else {
            camera.movePosition(new Vector3f(0, 0, TRANSLATION * 10));
            uploadInfoInTextFields();
        }
        remakeModel();
    }

    //todo: доделать
    @FXML
    private void handleSidePanelRemove() {
        isPanelRemoved = !isPanelRemoved;
        if (isPanelRemoved) {
        } else {
        }
    }

    @FXML
    private void handleThemeChange() {
        isInLightMode = !isInLightMode;
        anchorPane.getStylesheets().clear();
        String filePath = isInLightMode ? "styles/light_mode.css" : "styles/dark_mode.css";
        anchorPane.getStylesheets().add(getClass().getResource(filePath).toExternalForm());
    }

    @FXML
    private void onOpenModelMenuItemClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Model (*.obj)", "*.obj"));
        fileChooser.setTitle("Load Model");

        File file = fileChooser.showOpenDialog((Stage) canvas.getScene().getWindow());
        if (file == null) {
            return;
        }

        Path fileName = Path.of(file.getAbsolutePath());

        try {
            String fileContent = Files.readString(fileName);
            mesh = ObjReader.read(fileContent);
        } catch (IOException exception) {
            Alert alert = createAlertWithError("Ошибка при открытии файла " + fileName, "");
            alert.showAndWait();
        }

        if (mesh != null) {
            Triangulation.triangulateModel(mesh);
            mesh.setNormals(FindNormals.findNormals(mesh));
            mesh.setTexture(null);
            remakeModel();
        }
    }

    @FXML
    public void handleCameraForward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
        remakeModel();
    }

    @FXML
    public void handleCameraBackward(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, 0, TRANSLATION));
        remakeModel();
    }

    @FXML
    public void handleCameraLeft(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
        remakeModel();
    }

    @FXML
    public void handleCameraRight(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
        remakeModel();
    }

    @FXML
    public void handleCameraUp(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, TRANSLATION, 0));
        remakeModel();
    }

    @FXML
    public void handleCameraDown(ActionEvent actionEvent) {
        camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
        remakeModel();
    }

    @FXML
    private void toggleMoveMode() {
        isMoveModeEnabled = !isMoveModeEnabled;
        isRotateModeEnabled = false;
        isScalingModeEnabled = false;
        updateModeStyles();
    }

    @FXML
    private void toggleRotateMode() {
        isRotateModeEnabled = !isRotateModeEnabled;
        isMoveModeEnabled = false;
        isScalingModeEnabled = false;
        updateModeStyles();
    }

    @FXML
    private void toggleScalingMode() {
        isScalingModeEnabled = !isScalingModeEnabled;
        isMoveModeEnabled = false;
        isRotateModeEnabled = false;
        updateModeStyles();
    }

    private void updateModeStyles() {
        if (isMoveModeEnabled) {
            System.out.println("Move mode enabled");
        } else if (isRotateModeEnabled) {
            System.out.println("Rotate mode enabled");
        } else if (isScalingModeEnabled) {
            System.out.println("Scaling mode enabled");
        } else {
            System.out.println("No mode enabled");
        }
    }

    private void remakeModel() {
        if (mesh != null) {
            textureText.setText(mesh.getTextureSource());
            RenderEngine.render(canvas.getGraphicsContext2D(), CameraManager.getActive(), mesh, (int) width,
                    (int) height);
            showModelInfo();
        }
    }

    @FXML
    private void scalingModelX() {
        String newNear = scaleModelX.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }

        Model.scaleModelOnXForward(newNearDouble, mesh);
        scaleModelX.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void translatingModelX() {
        String newNear = translateModelX.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
            return;
        }

        if (newNearDouble > 0) {
            Model.moveModelRight(newNearDouble, mesh);
        } else {
            Model.moveModelLeft(Math.abs(newNearDouble), mesh);
        }

        translateModelX.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void scalingModelY() {
        String newNear = scaleModelY.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }
        Model.scaleModelOnYUp(newNearDouble, mesh);
        scaleModelX.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void translatingModelY() {
        String newNear = translateModelY.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
            return;
        }

        if (newNearDouble > 0) {
            Model.moveModelUp(newNearDouble, mesh);
        } else {
            Model.moveModelDown(Math.abs(newNearDouble), mesh);
        }

        translateModelY.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }


    @FXML
    private void scalingModelZ() {
        String newNear = scaleModelZ.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
            return;
        }

        if (newNearDouble > 0) {
            Model.moveModelBackward(newNearDouble, mesh);
        } else {
            Model.moveModelForward(Math.abs(newNearDouble), mesh);
        }

        translateModelZ.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void translatingModelZ() {
        String newNear = translateModelZ.getText();
        Double newNearDouble = 0d;

        try {
            newNearDouble = Double.parseDouble(newNear);
        } catch (Exception e) {
            nearTextField.setText(camera.getNearPlane() + "");
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }

        camera.setNearPlane(newNearDouble);
        nearTextField.setText(camera.getNearPlane() + "");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void rotatingModelX() {
        String newNear = rotateModelX.getText();
        Integer newNearInt = 0;

        try {
            newNearInt = Integer.parseInt(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }
        if (newNearInt > 0) {
            Model.rotateModelOnXNotClockwise(newNearInt, mesh);
        } else {
            Model.rotateModelOnXClockwise(newNearInt, mesh);
        }
        rotateModelX.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void rotatingModelY() {
        String newNear = rotateModelY.getText();
        Integer newNearInt = 0;

        try {
            newNearInt = Integer.parseInt(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }
        if (newNearInt > 0) {
            Model.rotateModelOnYUp(newNearInt, mesh);
        } else {
            Model.rotateModelOnYDown(newNearInt, mesh);
        }
        rotateModelY.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

    @FXML
    private void rotatingModelZ() {
        String newNear = rotateModelZ.getText();
        Integer newNearInt = 0;

        try {
            newNearInt = Integer.parseInt(newNear);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            Alert alert = createAlertWithError("Неправильное значение ближнего плана.", errorMessage);
            alert.showAndWait();
        }
        if (newNearInt > 0) {
            Model.rotateModelOnZClockwise(newNearInt, mesh);
        } else {
            Model.rotateModelOnZNotClockwise(newNearInt, mesh);
        }
        rotateModelZ.setText("");
        anchorPane.requestFocus();
        remakeModel();
    }

}