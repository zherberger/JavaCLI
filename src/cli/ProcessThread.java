package cli;

import java.io.InputStream;
import java.io.InputStreamReader;

import javafx.application.Platform;

import java.io.IOException;

// Containerize, my friends
public class ProcessThread implements Runnable {

	private ProcessListener cli;
	private String command;
	private Runtime runtime;
	private StringBuffer stdout;
	private StringBuffer stderr;
	
	public ProcessThread(ProcessListener cli, String command) {
		this.cli = cli;
		this.command = command;
		runtime = Runtime.getRuntime();
		stdout = new StringBuffer();
		stderr = new StringBuffer();
		
		// This just feels so CLEAN. Thanks Dr. LaRue.
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		try {
			Process process = runtime.exec(command);
			
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
			
			// I got this next part from the tutorial page on the assignment: http://www.avajava.com/tutorials/lessons/how-do-i-run-another-application-from-java.html?page=2
			// Processes can have continuous output, so it makes sense that a buffer-type object is needed.
			
			int n1;
			char[] c1 = new char[1024];
			
			while((n1 = isr.read(c1)) > 0) {
				// This may seem intentionally obfuscated, but it's super concise.
				// Java's functional interface allows us to specify instances of an interface in this fashion, as long as that interface has only one method.
				// The empty parentheses () just mean there are no parameters, as run() doesn't have any for a Runnable.
				// Sure beats defining an inner class every time.
				Platform.runLater(() -> cli.appendOutput(new String(c1)));
			}
			
			int n2;
			char[] c2 = new char[1024];
			
			while((n2 = esr.read(c2)) > 0) {
				Platform.runLater(() -> cli.appendError(new String(c2)));
			}
			
			System.out.println("Exiting...");
		}
		
		catch(IOException e) {
			Platform.runLater(() -> cli.appendError("IOException encountered: " + e.getMessage()));
		}
	}

	public String getCommand() {
		return command;
	}
}
