package cli;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CLIApplication extends Application implements ProcessListener {
	ScrollPane root;
	VBox outputBox;
	TextField inputField;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		outputBox = new VBox();
		
		TextField inputField = new TextField();
		inputField.setStyle("-fx-background-color: rgba(0, 0, 0, 0);"); // transparent background. Makes it not look like an obvious TextField.
		inputField.requestFocus();
		
		inputField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ENTER)) { 
				appendOutput("$" + inputField.getText());
				runCommand(inputField.getText());
				inputField.setText("");
			}
		});
		
		Label promptLabel = new Label("$");
		promptLabel.setLabelFor(inputField);
		HBox.setMargin(promptLabel, new Insets(5, 0, 0, 0));
		
		HBox inputBox = new HBox();
		inputBox.getChildren().addAll(promptLabel, inputField);
		
		BorderPane cli = new BorderPane();
		cli.setCenter(outputBox);
		cli.setBottom(inputBox);
		BorderPane.setMargin(outputBox, new Insets(0, 8, 0, 8));
		
		root = new ScrollPane();
		root.setPrefWidth(600);
		root.setPrefHeight(400);
		root.setContent(cli);
		
		Scene scene = new Scene(root);
		primaryStage.sizeToScene();
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void runCommand(String command) {
		String[] parts = command.split(" ");
		
		switch(parts[0]) {
			// Builtins, then:
			default: {
				ProcessThread processThread = new ProcessThread(this, command);
				
				if(!parts[parts.length - 1].equals("&")) { // emulate "run in background" feature of modern shells

				}
			}	 
		}
	}
	
	private void scrollToBottom() {
		System.out.println(root.getVvalue());
		
		if(root.getVvalue() == 1.0)
			root.setVvalue(1.0);
	}
	
	// Appending outputBox/error is gonna be done one line at a time, for simplicity's sake.

	@Override
	public void appendOutput(String output) {
		Label outputLabel = new Label(output);
		outputBox.getChildren().add(outputLabel);
		scrollToBottom();
	}
	
	@Override
	public void appendError(String error) {
		Label errorLabel = new Label(error);
		outputBox.getChildren().add(errorLabel);
		scrollToBottom();
	}
}
