package cli;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CLI extends BorderPane implements ProcessListener {
	VBox outputBox;
	
	public CLI() {
		outputBox = new VBox();
		setCenter(outputBox);
		
		TextField inputField = new TextField();
		inputField.setPrefHeight(20);
		inputField.setBackground(new Background(new BackgroundFill(Color.WHITE, null, Insets.EMPTY)));
		setBottom(inputField);
		inputField.requestFocus();
		
		inputField.setOnKeyPressed(e -> {
			if(e.getCode().equals(KeyCode.ENTER)) { 
				appendOutput(inputField.getText());
				runCommand(inputField.getText());
			}
		});
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
	
	// Appending outputBox/error is gonna be done one line at a time, for simplicity's sake.

	@Override
	public void appendOutput(String output) {
		Label outputLabel = new Label(output);
		outputBox.getChildren().add(outputLabel);
	}
	
	@Override
	public void appendError(String error) {
		Label errorLabel = new Label(error);
		outputBox.getChildren().add(errorLabel);
	}
}