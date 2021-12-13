package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Md2Html {
    private final static Map<String, String> mdToHtml = new HashMap<>(Map.of(
            "*", "em",
            "_", "em",
            "**", "strong",
            "__", "strong",
            "`", "code",
            "--", "s",
            "++", "u"
    ));

    private final static Map<String, String> spToHtml = new HashMap<>(Map.of(
            "<", "&lt;",
            ">", "&gt;",
            "&", "&amp;"
    ));

    private final static int maxTagSize = 2;

    private static boolean isSpecialSymbol(String c) {
        return c.equals("<") || c.equals(">") || c.equals("&");
    }

    private static boolean isMarkupElement(String c) {
        return c.equals("**") || c.equals("__") || c.equals("--") || c.equals("*") || c.equals("_") || c.equals("`") || c.equals("++");
    }

    private static void toParagraphOrHeader(StringBuilder text) {
        if (text.charAt(0) == '#') {
            int index = 0, count = 0;
            while (index < text.length() && text.charAt(index) == '#') {
                index++;
                count++;
            }
            if (index != text.length() && Character.isWhitespace(text.charAt(index))) {
                String num = Integer.toString(count);
                text.delete(0, index + 1);
                text.insert(0, "<h" + num + ">");
                text.append("</h").append(num).append(">");
            } else {
                text.insert(0, "<p>");
                text.append("</p>");
            }
        } else {
            text.insert(0, "<p>");
            text.append("</p>");
        }
    }

    private static StringBuilder tagsToHtml(StringBuilder text, ArrayDeque<String> tags, AtomicInteger index, String tag) {
        StringBuilder result = new StringBuilder();

        if (index.get() >= text.length()) {
            result.append(tag);
        } else {
            while (index.get() < text.length()) {
                if (text.charAt(index.get()) == '\\') {
                    index.addAndGet(1);
                    result.append(text.charAt(index.getAndAdd(1)));
                } else {
                    String buf = "";

                    int tagIndex = index.get() + maxTagSize;
                    if (tagIndex >= text.length()) {
                        tagIndex = text.length();
                    }

                    while (tagIndex - index.get() > 0) {
                        buf = text.substring(index.get(), tagIndex);
                        
                        if (isMarkupElement(buf) || (index.get() + 1 == text.length() && !tag.equals(""))) {
                            if (buf.equals(tag)) {
                                result.insert(0, "<" + mdToHtml.get(buf) + ">");
                                result.append("</").append(mdToHtml.get(buf)).append(">");
                                tags.pop();
                                index.addAndGet(buf.length());
                                return result;
                            } else if (tags.contains(buf) || index.get() + 1 == text.length()) {
                                result.insert(0, tag);
                                if (index.get() + 1 != text.length()) {
                                    tags.pop();
                                }
                                return result;
                            } else {
                                tags.push(buf);
                                index.addAndGet(buf.length());
                                result.append(tagsToHtml(text, tags, index, buf));
                                break;
                            }
                        } else if (isSpecialSymbol(buf)) {
                            result.append(spToHtml.get(buf));
                            index.addAndGet(buf.length());
                            tagIndex = index.get() + 1;
                            break;
                        }
                        tagIndex--;
                    }
                    if (tagIndex == index.get()) {
                        result.append(text.charAt(index.getAndAdd(1)));
                    }
                }
            }
        }
        return result;
    }

    private static StringBuilder markdownToHtml(StringBuilder text) {
        text = tagsToHtml(text, new ArrayDeque<String>(), new AtomicInteger(0), "");
        toParagraphOrHeader(text);
        return text;
    }

    public static void main(String[] args) throws IOException {
        StringBuilder outputText = new StringBuilder();

        try (
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(new File(args[0])),
                            StandardCharsets.UTF_8
                    )
            )
        ) {
            while (true) {
                StringBuilder text = new StringBuilder();
                String line = in.readLine();
                if (line == null) {
                    break;
                } else if (!line.equals("")) {
                    while (line != null && !line.equals("")) {
                        text.append(line).append('\n');
                        line = in.readLine();
                    }
                    text.delete(text.length() - 1, text.length());
                    outputText.append(markdownToHtml(text)).append("\n");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Input error: " + e.getMessage());
        }

        try (
            BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(
                            new File(args[1])),
                            StandardCharsets.UTF_8
                    )
            )
        ) {
            out.write(outputText.toString());
			
        } catch (FileNotFoundException e) {
            System.out.println("Output file not found: " + e.getMessage());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Encoding error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Output error: " + e.getMessage());
        }
    }
}