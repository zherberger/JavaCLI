package cli;

// Interfaces are so good. Abstract the actual process of appending input/output, and you can do all sorts
// of fancy things with ProcessThread.
public interface ProcessListener {
	public void appendOutput(String output);
	public void appendError(String error);
}
