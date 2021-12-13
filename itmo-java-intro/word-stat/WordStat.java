import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordStatInput {

    private static boolean isGoodSymbol(char c) {
        return Character.isLetter(c) || c == '\'' || c == '-';
    }

    public static void main(String[] args) throws IOException {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(args[0]))
                    )
            );
            Map<String, Integer> words = new LinkedHashMap<>();
            String line = in.readLine();
            while (line != null) {
                int startIndex = 0;
                int endIndex = 0;
                boolean flag = false;
                for (int i = 0; i < line.length(); i++) {
                    if (Character.isLetter(line.charAt(i)) && !flag) {
                        startIndex = i;
                        endIndex = i;
                        flag = true;
                    } else if ((!isGoodSymbol(line.charAt(i)) && flag) || (i + 1 == line.length())) {
                        endIndex = i;
                        if ((i + 1 == line.length()) && (flag) && isGoodSymbol(line.charAt(i))) {
                            endIndex++;
                        }
                        flag = false;
                        String word = line.substring(startIndex, endIndex).toLowerCase();
                        if (words.containsKey(word)) {
                            words.put(word, words.get(word) + 1);
                        } else {
                            words.put(word, 1);
                        }
                    }
                }
                line = in.readLine();
            }
            in.close();
            try {
                BufferedWriter out = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(new File(args[1]))
                        )
                );
                for (Map.Entry<String, Integer> elem : words.entrySet()) {
                    out.write(elem.getKey() + " " + elem.getValue() + "\n");
                }
                out.close();
            } catch (FileNotFoundException e) {
                System.out.println("File not found: " + e.getMessage());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
    }
}