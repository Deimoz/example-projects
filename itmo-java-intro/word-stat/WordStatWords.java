import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.NavigableMap;
import java.util.TreeMap;

public class WordStatWords {

    private static boolean isGoodSymbol(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) throws IOException {
		NavigableMap<String, Integer> words = new TreeMap<>();
		
		try {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(new File(args[0])),
							StandardCharsets.UTF_8
					)
			);
			try {
				String line = in.readLine();
				while (line != null) {
					int startIndex = 0;
					int endIndex = 0;
					boolean isWord = false;
					for (int i = 0; i < line.length(); i++) {
						char symbol = line.charAt(i);
						if (isGoodSymbol(symbol) && !isWord) {
							startIndex = i;
							endIndex = i;
							isWord = true;
						}
						if ((!isGoodSymbol(symbol) || i + 1 == line.length()) && isWord) {
							endIndex = i;
							if (i + 1 == line.length() && isGoodSymbol(symbol)) {
								endIndex++;
							}
							isWord = false;
							String word = line.substring(startIndex, endIndex).toLowerCase();
							words.put(word, words.getOrDefault(word, 0) + 1);
						}
					}
					line = in.readLine();
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
				
				for (NavigableMap.Entry<String, Integer> elem : words.entrySet()) {
					out.write(elem.getKey() + " " + elem.getValue() + "\n");
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