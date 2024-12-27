package com.cgvsu;

import com.cgvsu.light.Light;
import com.cgvsu.light.LightManager;
import com.cgvsu.objwriter.ObjWriter;
import com.cgvsu.rasterization.RasterizationParams;
import com.cgvsu.render_engine.RenderEngine;
import com.cgvsu.triangulation.Triangulation;

import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.cgvsu.camera.Camera;
import com.cgvsu.camera.CameraManager;
import com.cgvsu.math.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.normals.FindNormals;

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
	private VBox vBoxLight;

	@FXML
	private TextField textFieldPosX;

	@FXML
	private TextField textFieldPosY;

	@FXML
	private TextField textFieldPosZ;

	@FXML
	private TextField diskTranslateModelX;
	@FXML
	private TextField diskTranslateModelY;
	@FXML
	private TextField diskTranslateModelZ;
	@FXML
	private TextField diskRotateModelX;
	@FXML
	private TextField diskRotateModelY;
	@FXML
	private TextField diskRotateModelZ;
	@FXML
	private TextField diskScaleModelX;
	@FXML
	private TextField diskScaleModelY;
	@FXML
	private TextField diskScaleModelZ;
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
	private ListView<String> listViewModels;

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
	@FXML
	private HBox hBoxTextures;

	@FXML
	private ChoiceBox<String> lightList;

	@FXML
	private Button addLightBtn;

	@FXML
	private Button deleteLightBtn;

	@FXML
	private CheckBox switchBtn;

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

	List<Model> modelList = new ArrayList<>();

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

//            if (isPanelRemoved) {
//                canvas.setWidth(anchorPane.getWidth());
//            } else {
//                canvas.setWidth(anchorPane.getWidth()-scrollPaneMain.getWidth());
//            }

			canvas.setWidth(anchorPane.getWidth() - scrollPaneMain.getWidth());

			if (mesh != null) {
				RasterizationParams.drawBuffer.draw();
			}
		});

		timeline.getKeyFrames().add(frame);
		timeline.play();

		anchorPane.getStylesheets().add(getClass().getResource("styles/light_mode.css").toExternalForm());
		prepareGUI();

		for (int i = 0; i < LightManager.getLightsCount(); i++) {
			lightList.getItems().add(LightManager.printLight(i));
		}
		lightList.setValue(lightList.getItems().get(0));
		lightList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
				if (!number2.equals(-1)) {
					LightManager.setCurrent((Integer) number2);
					uploadInfoInTextFields();
					switchBtn.setSelected(LightManager.getCurrent().getOn());
				}
			}
		});

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

		switchBtn.setSelected(true);
		switchBtn.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				LightManager.getCurrent().setOn(newValue);
				remakeModel();
			}
		});
	}

	private void uploadInfoInTextFields() {
		textFieldPosX.setText(Double.toString((double) Math.round((camera.getPosition().getX() * 100)) / 100));
		textFieldPosY.setText(Double.toString((double) (Math.round(camera.getPosition().getY() * 100)) / 100));
		textFieldPosZ.setText(Double.toString((double) (Math.round(camera.getPosition().getZ() * 100)) / 100));

		textFieldTarX.setText(Double.toString((double) (Math.round(camera.getTarget().getX() * 100)) / 100));
		textFieldTarY.setText(Double.toString((double) (Math.round(camera.getTarget().getY() * 100)) / 100));
		textFieldTarZ.setText(Double.toString((double) (Math.round(camera.getTarget().getZ() * 100)) / 100));

		textFieldPosLightX
				.setText(Double.toString((double) Math.round((LightManager.getCurrent().position.getX() * 100)) / 100));
		textFieldPosLightY
				.setText(Double.toString((double) Math.round((LightManager.getCurrent().position.getY() * 100)) / 100));
		textFieldPosLightZ
				.setText(Double.toString((double) Math.round((LightManager.getCurrent().position.getZ() * 100)) / 100));

		textFieldLightSourceStrength.setText(LightManager.getCurrent().getK() + "");
		textFieldGlareArea.setText(LightManager.getCurrent().getHighLightShine() + "");
		textFieldGlareBrightness.setText(LightManager.getCurrent().getKs() + "");

		fovTextField.setText(camera.getFov() + "");
		nearTextField.setText(camera.getNearPlane() + "");
		farTextField.setText(camera.getFarPlane() + "");

		fovTextField.setText(camera.getFov() + "");
		nearTextField.setText(camera.getNearPlane() + "");
		farTextField.setText(camera.getFarPlane() + "");
	}

	private void prepareGUI() {
		uploadInfoInTextFields();

		checkBoxLines.setSelected(false);
		checkBoxLight.setSelected(true);
		checkBoxTexture.setSelected(false);

		scrollPaneMain.setDisable(true);
		colorPickerLines.setDisable(true);
		hBoxTextures.setDisable(true);

		// todo: изменить
		scrollPaneMain.setVisible(true);

	}

	private void showModelInfo() {
		colorPickerLines.setValue(RasterizationParams.getLineColor());
		colorPickerModel.setValue(mesh.getColor());
	}

	private Alert createAlert(Alert.AlertType alertType, String valueName, String errorMessage) {
		Alert alert = new Alert(alertType);
		alert.setTitle("Ошибка");
		alert.setHeaderText(valueName);
		alert.setContentText(errorMessage);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Не удалось запарсить значение double.", errorMessage);
			alert.showAndWait();
		}
		if (source.equals(textFieldLightSourceStrength)) {
			if (newValue >= 1) {
				newValue = 1d;
			} else if (newValue <= 0) {
				newValue = 0d;
			}
			LightManager.getCurrent().setK(newValue);
		} else if (source.equals(textFieldGlareBrightness)) {
			if (newValue >= 1) {
				newValue = 1d;
			} else if (newValue <= 0) {
				newValue = 0d;
			}
			LightManager.getCurrent().setKs(newValue);
		} else if (source.equals(textFieldGlareArea)) {
			if (newValue >= 50) {
				newValue = 50d;
			} else if (newValue <= 1) {
				newValue = 1d;
			}
			LightManager.getCurrent().setHighLightShine(newValue);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Не удалось запарсить значение double.", errorMessage);
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
			LightManager.getCurrent().setPosition(new Vector3f(coordinate, LightManager.getCurrent().position.getY(),
					LightManager.getCurrent().position.getZ()));
		} else if (source.equals(textFieldPosLightY)) {
			LightManager.getCurrent().setPosition(new Vector3f(LightManager.getCurrent().position.getX(), coordinate,
					LightManager.getCurrent().position.getZ()));
		} else if (source.equals(textFieldPosLightZ)) {
			LightManager.getCurrent().setPosition(new Vector3f(LightManager.getCurrent().position.getX(),
					LightManager.getCurrent().position.getY(), coordinate));
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
			colorPickerLines.setDisable(false);
		} else {
			RasterizationParams.setLine(false);
			colorPickerLines.setDisable(true);
		}
		remakeModel();
	}

	@FXML
	private void handleCheckBoxLightAction(ActionEvent actionEvent) {
		if (checkBoxLight.isSelected()) {
			RasterizationParams.setLight(true);
			vBoxLight.setDisable(false);
		} else {
			RasterizationParams.setLight(false);
			vBoxLight.setDisable(true);
		}
		remakeModel();
	}

	@FXML
	private void handleCheckBoxTextureAction(ActionEvent actionEvent) {
		if (checkBoxTexture.isSelected()) {
			RasterizationParams.setTexture(true);
			hBoxTextures.setDisable(false);
		} else {
			RasterizationParams.setTexture(false);
			hBoxTextures.setDisable(true);
		}
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение угла.", errorMessage);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение дальнего плана.", errorMessage);
			alert.showAndWait();
		}

		camera.setFarPlane(newFarDouble);
		farTextField.setText(camera.getFarPlane() + "");
		anchorPane.requestFocus();
		remakeModel();
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

	@FXML
	private void handleSidePanelRemove() {
//        isPanelRemoved = !isPanelRemoved;
//        if (isPanelRemoved) {
//            scrollPaneMain.setManaged(false);
//            scrollPaneMain.setVisible(false);
//        } else {
//            scrollPaneMain.setManaged(true);
//            scrollPaneMain.setVisible(true);
//        }
	}

	@FXML
	private void handleThemeChange() {
		isInLightMode = !isInLightMode;
		anchorPane.getStylesheets().clear();
		String filePath = isInLightMode ? "styles/light_mode.css" : "styles/dark_mode.css";
		anchorPane.getStylesheets().add(getClass().getResource(filePath).toExternalForm());
	}

	@FXML
	private void onListViewModelsClick(MouseEvent mouseEvent) {
		if (mouseEvent.getButton() == MouseButton.PRIMARY) {
			int itemIndex = listViewModels.getSelectionModel().getSelectedIndex();
			listViewModels.getSelectionModel().select(itemIndex);
			mesh = modelList.get(itemIndex);
			remakeModel();
		}
	}

	@FXML
	private void onOpenModelMenuItemClick() throws IOException {
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
		} catch (Exception exception) {
			Alert alert = createAlert(Alert.AlertType.ERROR, "Ошибка при открытии файла " + fileName,
					exception.getMessage());
			alert.showAndWait();
		}

		if (mesh != null) {
			modelList.add(mesh);
			listViewModels.getItems().add(file.getName());
			listViewModels.getSelectionModel().clearSelection();
			listViewModels.getSelectionModel().selectLast();
			try {
				Triangulation.triangulateModel(mesh);
			} catch (Exception exception) {
				Alert alert = createAlert(Alert.AlertType.ERROR, exception.getMessage(), "");
				alert.showAndWait();
			}
			mesh.setNormals(FindNormals.findNormals(mesh));
			mesh.setTexture(null);
			scrollPaneMain.setDisable(false);
			remakeModel();
		}
	}

	@FXML
	private void onSaveModelAsMenuItemClick() {
		if (mesh == null) {
			Alert alert1 = createAlert(Alert.AlertType.WARNING, "Ошибка при сохранении файла",
					"Model is null. Download any model before saving it");
			alert1.showAndWait();
		} else {
			FileChooser saveChooser = new FileChooser();
			saveChooser.setTitle("Save file as");
			saveChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("OBJ file", "*.obj"));
			File file = saveChooser.showSaveDialog(canvas.getScene().getWindow());
			String modelInfo = ObjWriter.write(mesh);
			if (file != null) {
				try {
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(modelInfo);
					fileWriter.close();
				} catch (Exception e) {
					Alert alert2 = createAlert(Alert.AlertType.ERROR, e.getMessage(), "");
					alert2.showAndWait();
				}
			}
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}

		Model.scaleModelOnXForward(newNearDouble, mesh);
		scaleModelX.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
			return;
		}

		if (newNearDouble > 0) {
			Model.moveModelRight(newNearDouble, mesh);
		} else {
			Model.moveModelLeft(Math.abs(newNearDouble), mesh);
		}

		translateModelX.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}
		Model.scaleModelOnYDown(newNearDouble, mesh);
		scaleModelY.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
			return;
		}

		if (newNearDouble > 0) {
			Model.moveModelUp(newNearDouble, mesh);
		} else {
			Model.moveModelDown(Math.abs(newNearDouble), mesh);
		}

		translateModelY.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
			return;
		}
		Model.scaleModelOnZRight(newNearDouble, mesh);

		scaleModelZ.setText(newNear);
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
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}
		if (newNearDouble > 0) {
			Model.moveModelBackward(newNearDouble, mesh);
		} else {
			Model.moveModelForward(Math.abs(newNearDouble), mesh);
		}
		translateModelZ.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}
		if (newNearInt > 0) {
			Model.rotateModelOnXNotClockwise(newNearInt, mesh);
		} else {
			Model.rotateModelOnXClockwise(newNearInt, mesh);
		}
		rotateModelX.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}
		if (newNearInt > 0) {
			Model.rotateModelOnYUp(newNearInt, mesh);
		} else {
			Model.rotateModelOnYDown(newNearInt, mesh);
		}
		rotateModelY.setText(newNear);
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
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение ближнего плана.", errorMessage);
			alert.showAndWait();
		}
		if (newNearInt > 0) {
			Model.rotateModelOnZClockwise(newNearInt, mesh);
		} else {
			Model.rotateModelOnZNotClockwise(newNearInt, mesh);
		}
		rotateModelZ.setText(newNear);
		anchorPane.requestFocus();
		remakeModel();
	}

	private ScheduledExecutorService translateXExecutor;
	private ScheduledExecutorService translateYExecutor;
	private ScheduledExecutorService translateZExecutor;
	private ScheduledExecutorService rotateXExecutor;
	private ScheduledExecutorService rotateYExecutor;
	private ScheduledExecutorService rotateZExecutor;
	private ScheduledExecutorService scaleXExecutor;
	private ScheduledExecutorService scaleYExecutor;
	private ScheduledExecutorService scaleZExecutor;

	private volatile boolean isTranslatingX = false;
	private volatile boolean isTranslatingY = false;
	private volatile boolean isTranslatingZ = false;
	private volatile boolean isRotatingX = false;
	private volatile boolean isRotatingY = false;
	private volatile boolean isRotatingZ = false;
	private volatile boolean isScalingX = false;
	private volatile boolean isScalingY = false;
	private volatile boolean isScalingZ = false;

	@FXML
	private void diskTranslatingModelX() {
		if (isTranslatingX) {
			return;
		}
		isTranslatingX = true;
		String newLine = diskTranslateModelX.getText();
		Double translationValue = 0d;

		try {
			translationValue = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		double finalTranslationValue = translationValue;

		translateXExecutor = Executors.newSingleThreadScheduledExecutor();
		translateXExecutor.scheduleAtFixedRate(() -> {
			if (!isTranslatingX) {
				return;
			}
			if (finalTranslationValue > 0) {
				Model.moveModelRight(finalTranslationValue, mesh);
			} else {
				Model.moveModelLeft(Math.abs(finalTranslationValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskTranslateModelX.setText(String.valueOf(finalTranslationValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskTranslatingModelY() {
		if (isTranslatingY) {
			return;
		}
		isTranslatingY = true;
		String newLine = diskTranslateModelY.getText();
		Double translationValue = 0d;

		try {
			translationValue = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		double finalTranslationValue = translationValue;

		translateYExecutor = Executors.newSingleThreadScheduledExecutor();
		translateYExecutor.scheduleAtFixedRate(() -> {
			if (!isTranslatingY) {
				return;
			}
			if (finalTranslationValue > 0) {
				Model.moveModelUp(finalTranslationValue, mesh);
			} else {
				Model.moveModelDown(Math.abs(finalTranslationValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskTranslateModelY.setText(String.valueOf(finalTranslationValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskTranslatingModelZ() {
		if (isTranslatingZ) {
			return;
		}
		isTranslatingZ = true;
		String newLine = diskTranslateModelZ.getText();
		Double translationValue = 0d;

		try {
			translationValue = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		double finalTranslationValue = translationValue;

		translateZExecutor = Executors.newSingleThreadScheduledExecutor();
		translateZExecutor.scheduleAtFixedRate(() -> {
			if (!isTranslatingZ) {
				return;
			}
			if (finalTranslationValue > 0) {
				Model.moveModelBackward(finalTranslationValue, mesh);
			} else {
				Model.moveModelForward(Math.abs(finalTranslationValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskTranslateModelZ.setText(String.valueOf(finalTranslationValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void stopTranslatingDiskModel() {
		isTranslatingX = false;
		isTranslatingY = false;
		isTranslatingZ = false;

		List<ScheduledExecutorService> executors = Arrays.asList(translateXExecutor, translateYExecutor,
				translateZExecutor);

		for (ScheduledExecutorService executor : executors) {
			if (executor != null && !executor.isShutdown()) {
				executor.shutdown();
			}
		}
	}

	@FXML
	private void diskRotatingModelX() {
		if (isRotatingX) {
			return;
		}
		isRotatingX = true;
		String newLine = diskRotateModelX.getText();
		Integer value = 0;
		try {
			value = Integer.parseInt(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		int finalValue = value;
		rotateXExecutor = Executors.newSingleThreadScheduledExecutor();
		rotateXExecutor.scheduleAtFixedRate(() -> {
			if (!isRotatingX) {
				return;
			}
			if (finalValue > 0) {
				Model.rotateModelOnXNotClockwise(finalValue, mesh);
			} else {
				Model.rotateModelOnXClockwise(Math.abs(finalValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskRotateModelX.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskRotatingModelY() {
		if (isRotatingY) {
			return;
		}
		isRotatingY = true;
		String newLine = diskRotateModelY.getText();
		Integer value = 0;
		try {
			value = Integer.parseInt(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		int finalValue = value;
		rotateYExecutor = Executors.newSingleThreadScheduledExecutor();
		rotateYExecutor.scheduleAtFixedRate(() -> {
			if (!isRotatingY) {
				return;
			}
			if (finalValue > 0) {
				Model.rotateModelOnYUp(finalValue, mesh);
			} else {
				Model.rotateModelOnYDown(Math.abs(finalValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskRotateModelY.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskRotatingModelZ() {
		if (isRotatingZ) {
			return;
		}
		isRotatingZ = true;
		String newLine = diskRotateModelZ.getText();
		Integer value = 0;
		try {
			value = Integer.parseInt(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для перемещения.", errorMessage);
			alert.showAndWait();
			return;
		}
		int finalValue = value;
		rotateZExecutor = Executors.newSingleThreadScheduledExecutor();
		rotateZExecutor.scheduleAtFixedRate(() -> {
			if (!isRotatingZ) {
				return;
			}
			if (finalValue > 0) {
				Model.rotateModelOnZClockwise(finalValue, mesh);
			} else {
				Model.rotateModelOnZNotClockwise(Math.abs(finalValue), mesh);
			}
			Platform.runLater(() -> {
				remakeModel();
				diskRotateModelZ.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void stopRotatingDiskModel() {
		isRotatingX = false;
		isRotatingY = false;
		isRotatingZ = false;

		List<ScheduledExecutorService> executors = Arrays.asList(rotateXExecutor, rotateYExecutor, rotateZExecutor);

		for (ScheduledExecutorService executor : executors) {
			if (executor != null && !executor.isShutdown()) {
				executor.shutdown();
			}
		}
	}

	@FXML
	private void diskScalingModelX() {
		if (isScalingX)
			return;
		isScalingX = true;
		String newLine = diskScaleModelX.getText();
		Double value = 0d;
		try {
			value = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для масштабирования.",
					errorMessage);
			alert.showAndWait();
			return;
		}
		double finalValue = value;
		scaleXExecutor = Executors.newSingleThreadScheduledExecutor();
		scaleXExecutor.scheduleAtFixedRate(() -> {
			if (!isScalingX)
				return;
			Model.scaleModelOnXForward(finalValue, mesh);
			Platform.runLater(() -> {
				remakeModel();
				diskScaleModelX.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskScalingModelY() {
		if (isScalingY)
			return;
		isScalingY = true;
		String newLine = diskScaleModelY.getText();
		Double value = 0d;
		try {
			value = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для масштабирования.",
					errorMessage);
			alert.showAndWait();
			return;
		}
		double finalValue = value;
		scaleYExecutor = Executors.newSingleThreadScheduledExecutor();
		scaleYExecutor.scheduleAtFixedRate(() -> {
			if (!isScalingY)
				return;
			Model.scaleModelOnYDown(finalValue, mesh);
			Platform.runLater(() -> {
				remakeModel();
				diskScaleModelY.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void diskScalingModelZ() {
		if (isScalingZ)
			return;
		isScalingZ = true;
		String newLine = diskScaleModelZ.getText();
		Double value = 0d;
		try {
			value = Double.parseDouble(newLine);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			Alert alert = createAlert(Alert.AlertType.ERROR, "Неправильное значение для масштабирования.",
					errorMessage);
			alert.showAndWait();
			return;
		}
		double finalValue = value;
		scaleZExecutor = Executors.newSingleThreadScheduledExecutor();
		scaleZExecutor.scheduleAtFixedRate(() -> {
			if (!isScalingZ)
				return;
			Model.scaleModelOnZRight(finalValue, mesh);
			Platform.runLater(() -> {
				remakeModel();
				diskScaleModelZ.setText(String.valueOf(finalValue));
			});
		}, 0, 1, TimeUnit.SECONDS);
	}

	@FXML
	private void stopScalingDiskModel() {
		isScalingX = false;
		isScalingY = false;
		isScalingZ = false;

		List<ScheduledExecutorService> executors = Arrays.asList(scaleXExecutor, scaleYExecutor, scaleZExecutor);

		for (ScheduledExecutorService executor : executors) {
			if (executor != null && !executor.isShutdown()) {
				executor.shutdown();
			}
		}
	}

	@FXML
	private void handleAddLightBtnAction() {
		LightManager.addNewLight(new Light());
		lightList.getItems().add(LightManager.printLight(LightManager.getLightsCount() - 1));
		remakeModel();
	}

	@FXML
	private void handleDeleteLightBtnAction() {
		LightManager.deleteLight(LightManager.getCurrent());
		LightManager.setCurrent(0);
		lightList.getItems().clear();
		for (int i = 0; i < LightManager.getLightsCount(); i++) {
			lightList.getItems().add(LightManager.printLight(i));
		}
		lightList.setValue(lightList.getItems().get(0));
		remakeModel();
	}

}