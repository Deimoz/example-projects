import java.io.*;
import java.nio.charset.StandardCharsets;

public class Scanner {
    private BufferedReader input;
    private String unreadedString;
    private boolean hasUnreadedString;
    private int indexInLine;

    public Scanner(InputStream object) {
        input = new BufferedReader(
                new InputStreamReader(
                        object,
                        StandardCharsets.UTF_8
                )
        );
    }
    public Scanner (File object) throws FileNotFoundException {
        input = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(object),
                        StandardCharsets.UTF_8
                )
        );
    }
	
    public Scanner(String str) {
        unreadedString = str;
        hasUnreadedString = true;
    }

    private void takeLine() throws IOException {
        if (!hasUnreadedString) {
            unreadedString = input.readLine();
            hasUnreadedString = true;
            indexInLine = 0;
        }
    }
	
	private boolean isGoodSymbol(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }
	
    public boolean hasNextLine() throws IOException {
        if (hasUnreadedString) {
            return true;
        }
		
        unreadedString = input.readLine();
        if (unreadedString != null) {
            hasUnreadedString = true;
            indexInLine = 0;
            return hasUnreadedString;
        }
        return false;
    }

    public String nextLine() throws IOException {
        if (hasUnreadedString) {
            hasUnreadedString = false;
            int index = indexInLine;
            indexInLine = 0;
            return unreadedString.substring(index, unreadedString.length());
        }
        return input.readLine();
    }
	
    public boolean hasNextInt() throws IOException {
        takeLine();
        boolean isNumber = false;
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            char c = unreadedString.charAt(i);


            if (Character.isDigit(c) && !isNumber && (
				i == indexInLine
					|| (i == indexInLine + 1 && unreadedString.charAt(i - 1) == '-')
					|| (i > indexInLine && Character.isWhitespace(unreadedString.charAt(i - 1)))
					|| (i > indexInLine + 1 && unreadedString.charAt(i - 1) == '-' && Character.isWhitespace(unreadedString.charAt(i - 2))))) {
				isNumber = true;
            } else if (!Character.isDigit(c) && !Character.isWhitespace(c) && isNumber) {
                isNumber = false;
                break;
            }
            if ((Character.isWhitespace(c) || i + 1 == unreadedString.length())  && isNumber)  {
                break;
            }
        }
        return isNumber;
    }

    public int nextInt() throws IOException {
        takeLine();
        boolean isNumber = false;
        int startIndex = indexInLine;
        int endIndex = indexInLine;
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            if (!Character.isWhitespace(unreadedString.charAt(i)) && !isNumber) {
                startIndex = i;
                endIndex = i;
                isNumber = true;
            }
            if ((Character.isWhitespace(unreadedString.charAt(i)) || i + 1 == unreadedString.length()) && isNumber) {
                endIndex = i;
                if (i + 1 == unreadedString.length() && !Character.isWhitespace(unreadedString.charAt(i))) {
                    endIndex++;
                }
                break;
            }
        }
        indexInLine = endIndex;
        return Integer.parseInt(unreadedString.substring(startIndex, endIndex));
    }
	
	public boolean hasNextWord() throws IOException {
        takeLine();
        boolean isWord = false;
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            char c = unreadedString.charAt(i);
            if (isGoodSymbol(c) && !isWord) {
                isWord = true;
            }
            if ((!isGoodSymbol(c) || i + 1 == unreadedString.length()) && isWord) {
                break;
            }
        }
        return isWord;
    }
	
    public boolean hasNext() throws IOException {
        takeLine();
        boolean isSomething = false;
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            char c = unreadedString.charAt(i);
            if (!Character.isWhitespace(c)) {
                isSomething = true;
                break;
            }
        }
        return isSomething;
    }
	
    public String next() throws IOException {
        takeLine();
        int startIndex = indexInLine;
        int endIndex = indexInLine;
        boolean isSomething = false;
        String something = "";
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            char c = unreadedString.charAt(i);
            if (!Character.isWhitespace(c) && !isSomething) {
                startIndex = i;
                endIndex = i;
                isSomething = true;
            }
            if ((Character.isWhitespace(c) || i + 1 == unreadedString.length()) && isSomething) {
                endIndex = i;
                if (i + 1 == unreadedString.length() && !Character.isWhitespace(c)) {
                    endIndex++;
                }
                something = unreadedString.substring(startIndex, endIndex);
                break;
            }
        }
        indexInLine = endIndex;
        return something;
    }
	
    public String nextWord() throws IOException {
        takeLine();
        int startIndex = indexInLine;
        int endIndex = indexInLine;
        boolean isWord = false;
        String word = "";
        for (int i = indexInLine; i < unreadedString.length(); i++) {
            char c = unreadedString.charAt(i);
            if (isGoodSymbol(c) && !isWord) {
                startIndex = i;
                endIndex = i;
                isWord = true;
            }
            if ((!isGoodSymbol(c) || i + 1 == unreadedString.length()) && isWord) {
                endIndex = i;
                if (i + 1 == unreadedString.length() && isGoodSymbol(c)) {
                    endIndex++;
                }
                word = unreadedString.substring(startIndex, endIndex);
                break;
            }
        }
        indexInLine = endIndex;
        return word;
    }

    public void close() throws IOException {
        input.close();
    }
}