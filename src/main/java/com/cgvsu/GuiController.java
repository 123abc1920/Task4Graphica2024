package com.cgvsu;

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
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.io.File;

import com.cgvsu.camera.Camera;
import com.cgvsu.camera.CameraManager;
import com.cgvsu.math.Vector3f;

import com.cgvsu.model.Model;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.normals.FindNormals;

public class GuiController {

	final private float TRANSLATION = 0.5F; // 10.0F;

	@FXML
	AnchorPane anchorPane;

	@FXML
	private Canvas canvas;

	@FXML
	private VBox vBoxMain;

	@FXML
	private VBox VBoxCamera;

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
	private Button addTextureBtn;

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

	private boolean isInLightMode = true;

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

	@FXML
	private void initialize() {
		anchorPane.prefWidthProperty()
				.addListener((ov, oldValue, newValue) -> canvas.setWidth(newValue.doubleValue() - vBoxMain.getWidth()));
		anchorPane.prefHeightProperty()
				.addListener((ov, oldValue, newValue) -> canvas.setHeight(newValue.doubleValue()));

		timeline = new Timeline();
		timeline.setCycleCount(Animation.INDEFINITE);

		KeyFrame frame = new KeyFrame(Duration.millis(15), event -> {
			double width = canvas.getWidth();
			double height = canvas.getHeight();

			camera = CameraManager.getActive();

			canvas.getGraphicsContext2D().clearRect(0, 0, width, height);
			camera.setAspectRatio((float) (width / height));

			if (mesh != null) {
				textureText.setText(mesh.getTextureSource());

				RenderEngine.render(canvas.getGraphicsContext2D(), CameraManager.getActive(), mesh, (int) width,
						(int) height);
//				VBoxMain.setVisible(true);
			}
		});

		anchorPane.getStylesheets().add(getClass().getResource("styles/light_mode.css").toExternalForm());
		showStartInfo();

		timeline.getKeyFrames().add(frame);
		timeline.play();
	}

	private void setTextInTextFields() {
		textFieldPosX.setText(Double.toString((double) Math.round((camera.getPosition().getX() * 100)) / 100));
		textFieldPosY.setText(Double.toString((double) (Math.round(camera.getPosition().getY() * 100)) / 100));
		textFieldPosZ.setText(Double.toString((double) (Math.round(camera.getPosition().getZ() * 100)) / 100));
		textFieldTarX.setText(Double.toString((double) (Math.round(camera.getTarget().getX() * 100)) / 100));
		textFieldTarY.setText(Double.toString((double) (Math.round(camera.getTarget().getY() * 100)) / 100));
		textFieldTarZ.setText(Double.toString((double) (Math.round(camera.getTarget().getZ() * 100)) / 100));
//		textFieldPosX.setText(Double.toString(camera().getPosition().getX()));
//		textFieldPosY.setText(Double.toString(camera.getPosition().getY()));
//		textFieldPosZ.setText(Double.toString(camera.getPosition().getZ()));
//		textFieldTarX.setText(Double.toString(camera.getTarget().getX()));
//		textFieldTarY.setText(Double.toString(camera.getTarget().getY()));
//		textFieldTarZ.setText(Double.toString(camera.getTarget().getZ()));
	}

	private void showStartInfo() {
		setTextInTextFields();
		checkBoxLight.setSelected(true);
		checkBoxTexture.setSelected(false);
		colorPickerLines.setValue(RasterizationParams.getLineColor());
		colorPickerModel.setValue(RasterizationParams.getColor());

		addTextureBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (mesh != null) {
					mesh.setTexture(textureText.getText());
				}
			}
		});

		deleteTextureBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (mesh != null) {
					mesh.deleteTexture();
				}
			}
		});

		chooseFileBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
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
			}
		});

		addCameraBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				CameraManager
						.addCamera(new Camera(new Vector3f(0, 0, 100), new Vector3f(0, 0, 0), 1.0F, 1, 0.01F, 100));
				cameraList.getItems().add(CameraManager.printCamera(CameraManager.getCamerasCount() - 1));
			}
		});

		deleteCameraBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				CameraManager.deleteCamera(CameraManager.getActive());
				CameraManager.setActiveCamera(0);
				cameraList.getItems().clear();
				for (int i = 0; i < CameraManager.getCamerasCount(); i++) {
					cameraList.getItems().add(CameraManager.printCamera(i));
				}
				cameraList.setValue(cameraList.getItems().get(0));
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
					setTextInTextFields();
				}
			}
		});
	}

	@FXML
	private void handleColorPickerLinesOnAction() {
		Color newLinesColor = colorPickerLines.getValue();
		RasterizationParams.setLineColor(newLinesColor);
	}

	@FXML
	private void handleColorPickerModelOnAction() {
		Color newModelColor = colorPickerModel.getValue();
		RasterizationParams.setColor(newModelColor);
	}

	@FXML
	private void handleCheckBoxLinesOnAction() {
		if (checkBoxLines.isSelected()) {
			RasterizationParams.setLine(true);
		} else {
			RasterizationParams.setLine(false);
		}
	}

	@FXML
	private void handleCheckBoxLightOnAction() {
		if (checkBoxLight.isSelected()) {
			RasterizationParams.setLight(true);
		} else {
			RasterizationParams.setLight(false);
		}
	}

	@FXML
	private void handleCheckBoxTextureOnAction() {
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
			setTextInTextFields();
		} /*
			 * else if (mouseEvent.getButton() == MouseButton.SECONDARY) { endXRMB =
			 * mouseEvent.getX(); endYRMB = mouseEvent.getY(); double shiftXRBM = endXRMB -
			 * startXRMB; double shiftYRMB = endYRMB - startYRMB; }
			 */
	}

	@FXML
	private void handleMouseScroll(ScrollEvent scrollEvent) {
		if (scrollEvent.getDeltaY() > 0) {
			camera.movePosition(new Vector3f(0, 0, -TRANSLATION * 10));
			setTextInTextFields();
		} else {
			camera.movePosition(new Vector3f(0, 0, TRANSLATION * 10));
			setTextInTextFields();
		}
	}

	@FXML
	private void handleThemeChange() {
		isInLightMode = !isInLightMode;
		anchorPane.getStylesheets().clear();
		String filePath = isInLightMode ? "styles/light_mode.css" : "styles/dark_mode.css";
		anchorPane.getStylesheets().add(getClass().getResource(filePath).toExternalForm());
	}

	/**
	 * Добавить обработчик событий дял всех textField по нажатии кнопки Enter
	 */

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
			// todo: обработка ошибок
		} catch (IOException exception) {

		}

		Triangulation.triangulateModel(mesh);
		mesh.normals = FindNormals.findNormals(mesh);
		mesh.setTexture(null);
	}

	@FXML
	public void handleCameraForward(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(0, 0, -TRANSLATION));
	}

	@FXML
	public void handleCameraBackward(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(0, 0, TRANSLATION));
	}

	@FXML
	public void handleCameraLeft(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(TRANSLATION, 0, 0));
	}

	@FXML
	public void handleCameraRight(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(-TRANSLATION, 0, 0));
	}

	@FXML
	public void handleCameraUp(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(0, TRANSLATION, 0));
	}

	@FXML
	public void handleCameraDown(ActionEvent actionEvent) {
		camera.movePosition(new Vector3f(0, -TRANSLATION, 0));
	}
}