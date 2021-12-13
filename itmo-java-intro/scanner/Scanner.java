import java.io.*;
import java.nio.charset.StandardCharsets;

public class Scanner {
    private BufferedReader input;
    private String unreadedString;
    private boolean hasUnreadedString = false;
    private int indexInLine;
    private int endOfLine;
    private boolean stringConstructor = false;

    
	public Scanner(InputStream object) {
        input = new BufferedReader(
                new InputStreamReader(
                        object,
                        StandardCharsets.UTF_8
                )
        );
    }
	
    public Scanner(File object) throws FileNotFoundException {
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
        indexInLine = 0;
        if (str != null) {
            endOfLine = str.length();
        }
        stringConstructor = true;
    }
	
    private void takeLine() throws IOException {
        if (!hasUnreadedString && !stringConstructor) {
            unreadedString = input.readLine();
            hasUnreadedString = true;
            indexInLine = 0;
            endOfLine = unreadedString.length() ;
        }
    }

    private boolean isNotEndOfLine(char c) {
        return !(c == '\n' || c == '\r');
    }

    private boolean isGoodSymbol(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public boolean hasNextLine() throws IOException {
        if (hasUnreadedString && indexInLine < endOfLine) {
            return true;
        }
        if (!stringConstructor) {
            unreadedString = input.readLine();
            if (unreadedString != null) {
                hasUnreadedString = true;
                indexInLine = 0;
                endOfLine = unreadedString.length();
                return true;
            }
        }
        return false;
    }

    public String nextLine() throws IOException {
        if (hasUnreadedString && indexInLine != endOfLine) {
            int endIndex = indexInLine + 1;
            while (endIndex != endOfLine && isNotEndOfLine(unreadedString.charAt(endIndex))) {
                endIndex++;
            }
            int index = indexInLine;
            if (unreadedString.charAt(index) == '\n') {
                index++;
            }
            indexInLine = endIndex;
            return unreadedString.substring(index, endIndex);
        }
        String s = input.readLine();
        indexInLine = 0;
        return s;
    }

    public boolean hasNextInt() throws IOException {
        takeLine();
        int index = indexInLine;
        while (index < endOfLine && isNotEndOfLine(unreadedString.charAt(index))) {
            char c = unreadedString.charAt(index);
            if (!Character.isWhitespace(c) && (Character.isDigit(c)
                    || (c == '-' && index + 1 < endOfLine && Character.isDigit(unreadedString.charAt(index + 1))))) {
                return true;
            } else if (!Character.isWhitespace(c)) {
                break;
            }
            index++;
        }
        return false;
    }

    public boolean hasNextWord() throws IOException {
        takeLine();
        int index = indexInLine;
        while (index < endOfLine && isNotEndOfLine(unreadedString.charAt(index))) {
            char c = unreadedString.charAt(index);
            if (!Character.isWhitespace(c) && isGoodSymbol(c)) {
                return true;
            }
            index++;
        }
        return false;
    }

    public int nextInt() throws IOException {
        takeLine();
        int startIndex = indexInLine;
        String something = "";
        while (startIndex < endOfLine && isNotEndOfLine(unreadedString.charAt(startIndex))) {
            char c = unreadedString.charAt(startIndex);
            if (Character.isDigit(c) || c == '-') {
                int endIndex = startIndex + 1;
                while (endIndex < endOfLine && isNotEndOfLine(unreadedString.charAt(endIndex)) &&
                        Character.isDigit(unreadedString.charAt(endIndex))) {
                    endIndex++;
                }
                something = unreadedString.substring(startIndex, endIndex);
                indexInLine = endIndex;
                break;
            }
            startIndex++;
        }
        return Integer.parseInt(something);
    }

    public String nextWord() throws IOException {
        takeLine();
        int startIndex = indexInLine;
        String something = "";
        while (startIndex < endOfLine && isNotEndOfLine(unreadedString.charAt(startIndex))) {
            char c = unreadedString.charAt(startIndex);
            if (isGoodSymbol(c)) {
                int endIndex = startIndex;
                while (endIndex < endOfLine && isNotEndOfLine(unreadedString.charAt(endIndex)) &&
                        (isGoodSymbol(unreadedString.charAt(endIndex)))) {
                    endIndex++;
                }
                something = unreadedString.substring(startIndex, endIndex);
                indexInLine = endIndex;
                break;
            }
            startIndex++;
        }
        return something;
    }

    public void close() throws IOException {
		if (stringConstructor) {
			hasUnreadedString = false;
			unreadedString = null;
		} else {
			input.close();
		}
    }
}
