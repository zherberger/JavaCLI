package cli;

import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;

import java.io.IOException;

// Containerize, my friends
public class ProcessThread implements Runnable {

	private ProcessListener cli;
	private Process process;
	private String command;
	private Runtime runtime;
	private boolean killed;
	
	public ProcessThread(ProcessListener cli, String command) {
		this.cli = cli;
		this.command = command;
		runtime = Runtime.getRuntime();
		
		// This just feels so CLEAN. Thanks Dr. LaRue.
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			process = runtime.exec(command);
			
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
			
			// I got the basics for this next part from the tutorial page on the assignment: http://www.avajava.com/tutorials/lessons/how-do-i-run-another-application-from-java.html?page=2
			// If a process had continuous output, it would get stuck appending to a StringBuffer. I went with a solution that just involves appending each line encountered to the CLI.
			char[] c1 = new char[1024];
			
			while(isr.read(c1) > 0 && !killed) {
				// This next line may seem intentionally obfuscated, but it's super concise.
				// Java's functional interface allows us to specify instances of an interface in this fashion, as long as that interface has only one method.
				// The empty parentheses () just mean there are no parameters, as run() doesn't have any for a Runnable.
				// Sure beats defining an inner class every time.
				
				// This is analogous to SwingUtilities.invokeLater from Network Programming.
				// The action taking place in the Application needs to be in the same thread as the Application. Makes sense.
				Platform.runLater(() -> cli.appendOutput(new String(c1)));
			}
			
			char[] c2 = new char[1024];
			
			while(esr.read(c2) > 0 & !killed) {
				Platform.runLater(() -> cli.appendError(new String(c2)));
			}
		}
		
		catch(IOException e) {
			Platform.runLater(() -> cli.appendError("IOException encountered: " + e.getMessage()));
		}
	}
	
	public void kill() {
		killed = true;
	}

	public String getCommand() {
		return command;
	}
}
