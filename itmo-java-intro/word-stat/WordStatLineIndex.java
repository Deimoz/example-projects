import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.NavigableMap;

public class WordStatLineIndex {

    public static void main(String[] args) throws IOException {
		NavigableMap<String, Stat> words = new TreeMap<>();
		try {
			Scanner in = new Scanner(new FileInputStream(new File(args[0])));
			try {
				String line = in.nextLine();
				
				int numberOfLine = 0;
				while (line != null) {
					numberOfLine++;
					Scanner str = new Scanner(line);
					int numberOfWord = 0;
					while (str.hasNextWord()) {
						numberOfWord++;
						String word = str.nextWord().toLowerCase();
						Stat temp = words.get(word);
						if (temp == null) {
							temp = new Stat();
							words.put(word, temp);
						}
						temp.addPosition(numberOfLine, numberOfWord);
					}
					line = in.nextLine();
				}
			} finally {
				in.close();
			}
		} catch (IOException e) {
			System.out.println("Input error: " + e.getMessage());
		}
		
        try {
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(new File(args[1])),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                for (NavigableMap.Entry<String, Stat> elem : words.entrySet()) {
                    out.write(elem.getKey() + " " + elem.getValue().size() + " ");
                    for (int i = 0; i < elem.getValue().size() - 1; i++) {
                        out.write(elem.getValue().getPosition(i) + " ");
                    }
                    out.write(elem.getValue().getPosition(elem.getValue().size() - 1));
					out.newLine();
                }
            } finally {
                out.close();
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Output error: " + e.getMessage());
        }
    }
}