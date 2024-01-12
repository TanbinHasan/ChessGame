package com.chess.application;

import java.awt.Desktop;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.chess.model.Setting;
import com.chess.root.Game;
import com.chess.root.moves.Move;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public abstract class SceneController implements Initializable {
	
	protected Chess chess;
	protected Game game;
	protected Stage stage;
	protected Setting settings;
	protected String header = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><title>Chess: moves of this game</title><style>body { font-family: \"Courier New\"; padding: 20px; }li { padding: 0.1em; }</style></head><body><h1>Following moves were done:</h1><ul>";
	protected String footer = "</ul></body></html>";
	protected String movesString = "";
	protected String out = header + movesString + footer;
	protected File movesFile;
	protected Path movesPath;
	protected File pgnFile;
	protected Path pgnPath;
	protected File pngUploadFile;
	protected String pgnString = "";
	protected Label fen;
	protected static final Logger LOG = Logger.getLogger(String.class.getName());
	
	@FXML
	protected MenuItem exitItem;
	
	@FXML
	protected MenuItem manualItem;
	
	@FXML
	protected MenuItem rulesItem;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}
	
	// ---------------------------------- START NEW GAME ----------------------------------	

	protected void startNewGame(SettingsController init, Stage stage, Setting settings) {
		try {   	
			FXMLLoader loadFrame = new FXMLLoader(GameController.class.getClassLoader().getResource("com/chess/resources/GameFrame.fxml"));
			Parent mainRoot = loadFrame.load();
			Scene scene = new Scene(mainRoot, 550, 630);
			scene.getStylesheets().add(GameController.class.getClassLoader().getResource("com/chess/resources/application.css").toExternalForm());
			stage.setScene(scene);	
			GameController controller = loadFrame.getController();
			controller.initializeProxy(init, settings);
			if (!settings.getColor()) {
				controller.handleRotate();
			}
			stage.show();  
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}
	
	// ---------------------------------- MENU HANDLING ----------------------------------

	@FXML 
	protected void handleExit(ActionEvent event) {
		Platform.exit();
		System.exit(0);
	}
	
	@FXML
	protected void handleManualExport() {
		try { 
			processTempFile("com/chess/resources/manual.html", "chess_manual.html");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}

	@FXML
	protected void handleRulesExport() {
		try { 
			processTempFile("com/chess/resources/rules.html", "chess_rules.html");
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}
	
	// ---------------------------------- EXPORT METHODS ----------------------------------
	
	private void processTempFile(String resource, String fileName) {
		try {
			InputStream htmlFile = this.getClass().getClassLoader().getResourceAsStream(resource);
			File tempFile = new File(System.getProperty("java.io.tmpdir"), fileName);
			Path tempPath = tempFile.toPath();
			Files.copy(htmlFile, tempPath, StandardCopyOption.REPLACE_EXISTING);
			tempFile.deleteOnExit();
			URI url = tempFile.toURI();
			Desktop.getDesktop().browse(url);  
		} catch (Exception e) {
			
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}

	public void updateTempFile(List<Move> moves) {
		updateOut(moves);
		try {
			if (movesPath != null) {
				InputStream htmlFile = new ByteArrayInputStream(out.getBytes());
				Files.copy(htmlFile, movesPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE, Arrays.deepToString(e.getStackTrace()));
		}
	}

	// ---------------------------------- GENERIC METHODS ----------------------------------

	protected void setStage(Stage stage) {
 		this.stage = stage;
 	}

	protected void setMainAccess(Chess chess) {
 		this.chess = chess;
 	}

	protected Chess getMainAccess() {
 		return chess;
 	}

	public Stage getStage() {
 		return stage;
 	}
	
	public Scene getScene() {
 		return stage.getScene();
 	}
	
	public void addMoveString (String s) {
		this.movesString += s;
	}
	
	public void updateOut(List<Move> strings) {
		StringBuilder bld = new StringBuilder();
		for (int i = 0; i < strings.size(); i++) {
			bld.append("<li>" + strings.get(i).getNotation() + "</li>");
			 if (i == strings.size()-1) {
				 bld.append("<li style='color:red;'>" + strings.get(i).getResult() + "</li>");
			}
			
		}
		out = header + bld.toString() + footer;
	}
	
	public abstract void getSliderValue(Number newValue);
}