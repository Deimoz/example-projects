import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class WordStatistic {
	private int number = 0;
	private List<Integer> lines = new ArrayList<>();
	private List<Integer> numInLines = new ArrayList<>();
	
	public void incNumber() {
		number++;
	}
	
	public void addPosition(int line, int number) {
		lines.add(line);
		numInLines.add(number);
		number++;
	}
	
	private int size() {
		return number;
	}
	
	public String getPosition(int i) {
		return (lines.get(i) + ":" + numInLines(i));
	}
}