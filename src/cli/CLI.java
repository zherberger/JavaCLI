package cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import javafx.scene.control.*;

public class CLI {
	public static void main(String[] args) {
		try {
			Runtime runtime = Runtime.getRuntime();
			Scanner in = new Scanner(System.in);
			
			String command = in.nextLine();
			System.out.println(command);
			Process process = runtime.exec(command);
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);
			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);
			
			int n1;
			char[] c1 = new char[1024];
			StringBuffer stdout = new StringBuffer();
			
			while((n1 = isr.read(c1)) > 0) { // While there are characters being read,
				stdout.append(c1, 0, n1); // append
				System.out.println(c1);
			}
			
			System.out.println("Standard output: " + stdout.toString());
			
			int n2;
			char[] c2 = new char[1024];
			StringBuffer stderr = new StringBuffer();
			
			while((n2 = esr.read(c2)) > 0) {
				stderr.append(c2, 0, n2);
			}
			
			System.out.println("Standard error: " + stderr.toString());
		}
		
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}