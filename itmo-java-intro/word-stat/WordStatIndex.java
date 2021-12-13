import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordStatIndex {

    private static boolean isGoodSymbol(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static void main(String[] args) throws IOException {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(args[0])),
                            StandardCharsets.UTF_8
                    )
            );
            try {
                try {
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(new File(args[1])),
                                    StandardCharsets.UTF_8
                            )
                    );
                    try {
                        Map<String, ArrayList<Integer>> words = new LinkedHashMap<>();
                        int numberOfWord = 0;
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
                                    numberOfWord++;
                                    String word = line.substring(startIndex, endIndex).toLowerCase();
                                    ArrayList<Integer> temp = words.get(word);
                                    if (temp == null) {
                                        temp = new ArrayList<>();
                                        temp.add(0);
                                    } 
                                    temp.set(0, temp.get(0) + 1);
                                    temp.add(numberOfWord);
                                    words.put(word, temp);
                                }
                            }
                            line = in.readLine();
                        }
                        for (Map.Entry<String, ArrayList<Integer>> elem : words.entrySet()) {
                            out.write(elem.getKey() + " ");
                            for (int i = 0; i  < elem.getValue().size() - 1; i++) {
                                out.write(elem.getValue().get(i) + " ");
                            }
                            out.write(elem.getValue().get(elem.getValue().size() - 1) + "\n");
                        }
                    } finally {
                        out.close();
                    }
                } catch (IOException e) {
                    System.out.println("I/O error: " + e.getMessage());
                }
            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException e) {
			System.out.println("Encoding error: " + e.getMessage());
		} catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } 
    }
}