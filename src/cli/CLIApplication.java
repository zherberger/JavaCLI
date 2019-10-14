package cli;

import java.util.Vector;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CLIApplication extends Application implements ProcessListener {
	ScrollPane root;
	VBox outputBox;
	TextField inputField;
	Scene scene;
	EventHandler<KeyEvent> commandHandler;
	ProcessThread currentProcess;
	Vector<ProcessThread> backgroundProcesses;
	Vector<String> history;
	int historyIndex;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) {
		backgroundProcesses = new Vector<ProcessThread>();
		outputBox = new VBox();
		history = new Vector<String>();
		historyIndex = 0;
		
		inputField = new TextField();
		inputField.setStyle("-fx-background-color: rgba(0, 0, 0, 0);"); // transparent background. Makes it not look like an obvious TextField.
		inputField.requestFocus();
		
		// The big one! This'll determine what happens based on the input to the inputField.
		commandHandler = new EventHandler<KeyEvent>() {
			public void handle(KeyEvent e) {
				
				// Was a command entered?
				if(e.getCode().equals(KeyCode.ENTER)) { 
					appendLastInput();
					runCommand(inputField.getText());
					inputField.setText("");
				}
				
				// Maybe the up arrow was pressed to request a recent command?
				else if(e.getCode().equals(KeyCode.UP) && historyIndex != -1) {
					inputField.setText(history.get(historyIndex--));
				}
				
				// Pressing the down arrow should revert to a "less previous" command
				else if(e.getCode().equals(KeyCode.DOWN)) {
					if(historyIndex == history.size())
						inputField.setText("");
					
					else inputField.setText(history.get(++historyIndex));
				}
				
				// Ctrl+C to cancel the current process
				else if(e.getCode().equals(KeyCode.C) && e.isControlDown() && currentProcess != null) {
					currentProcess.kill();
				}
			}
		};
		
		inputField.setOnKeyPressed(commandHandler);
		
		Label promptLabel = new Label("$");
		promptLabel.setLabelFor(inputField);
		HBox.setMargin(promptLabel, new Insets(5, 0, 0, 0));
		
		HBox inputBox = new HBox();
		inputBox.getChildren().addAll(promptLabel, inputField);
		inputField.setPrefWidth(400);
		System.out.println(inputField.getWidth());
		
		BorderPane cli = new BorderPane();
		BorderPane.setMargin(inputBox, new Insets(0, 0, 0, 8));
		cli.setCenter(outputBox);
		cli.setBottom(inputBox);
		BorderPane.setMargin(outputBox, new Insets(0, 8, 0, 8));
		
		root = new ScrollPane();
		root.setPrefWidth(600);
		root.setPrefHeight(400);
		root.setContent(cli);
		
		scene = new Scene(root);
		
		scene.setOnMouseClicked(e -> {
			inputField.requestFocus(); // Clicked anywhere else? Too bad, you gotta keep typing
		});
		
		// Might be good to have a "history" feature
		scene.setOnKeyPressed(e -> {
			
		});
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void runCommand(String command) {
		history.add(command);
		historyIndex = history.size() - 1;
		
		String[] parts = command.split(" ");
		ProcessThread processThread;
		
		switch(parts[0]) {
			case "killall": {
				for(ProcessThread pt : backgroundProcesses)
					pt.kill();
				
				break;
			}
			
			case "history": {
				for(int i = 0; i < history.size(); i++) {
					appendOutput(i + ": " + history.get(i));
				}
				
				break;
			}
			
			default: {
				if(!parts[parts.length - 1].equals("&")) { // emulate "run in background" feature of modern shells
					currentProcess = new ProcessThread(this, command);
					
//					inputField.setOnKeyPressed(e -> {
//						if(e.getCode().equals(KeyCode.C)) {
//							if(e.isControlDown()) {
//								processThread.kill();
//								inputField.setOnKeyPressed(commandHandler);
//							}
//						}
//					});
				}
				
				else {
					String commandWithoutAmpersand = "";
					
					for(int i = 0; i < parts.length - 1; i++)
						commandWithoutAmpersand += parts[i] + " ";
					
					backgroundProcesses.add(new ProcessThread(this, commandWithoutAmpersand));
				}
			}
		}
	}
	
	private void scrollToBottom() {
		root.setVvalue(1.5);
	}
	
	private void appendLastInput() {
		Label dollarSign = new Label("$");
		TextField lastInput = new TextField(inputField.getText());
		lastInput.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
		lastInput.setEditable(false);
		HBox.setMargin(dollarSign, new Insets(5, 0, 0, 0));
		HBox line = new HBox();
		line.getChildren().addAll(dollarSign, lastInput);
		outputBox.getChildren().add(line);
		scrollToBottom();
	}
	
	@Override
	public void appendOutput(String output) {
		Label outputLabel = new Label(output);
		outputBox.getChildren().add(outputLabel);
		scrollToBottom();
	}
	
	@Override
	public void appendError(String error) {
		Label errorLabel = new Label(error);
		errorLabel.setTextFill(Color.RED);
		outputBox.getChildren().add(errorLabel);
		scrollToBottom();
	}
}
