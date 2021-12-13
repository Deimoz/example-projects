package info.kgeorgiy.ja.vircev.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Walk {

    private static long hash(final Path path) {
        long h = 0;

        try (InputStream input = new FileInputStream(path.toString())) {
            byte[] b = new byte[1024];
            int c;
            while ((c = input.read(b)) >= 0) {
                for (int i = 0; i < c; i++) {
                    h = (h << 8) + (b[i] & 0xff);
                    long test;
                    if ((test = h & 0xff00000000000000L) != 0) {
                        h = ((h ^ (test >> 48)) & ~test);
                    }
                }
            }
        } catch (IOException e) {
            h = 0;
        }

        return h;
    }

    private static void printHashWithFile(final long hash, final String filePath, final BufferedWriter writer) throws IOException {
        try {
            String res = String.format("%016x", hash) + " " + filePath;
            writer.write(res);
            writer.newLine();
        } catch (IOException e) {
            printError("IOException error with writing to output file", e);
        }
    }

    private static void printError(String mes, Exception e) {
        System.out.println(mes + ": " + e.getMessage());
    }

    public static void main(String[] args) {
        if (args != null) {
            if (args.length != 2) {
                System.out.println("Wrong number of arguments");
            } else if (args[0] == null || args[1] == null) {
                System.out.println("Error with arguments");
            } else {
                final Path inputFile, outputFile;

                try {
                    inputFile = Paths.get(args[0]);
                } catch (InvalidPathException e) {
                    printError("InvalidPathException for input file", e);
                    return;
                }

                try {
                    outputFile = Paths.get(args[1]);
                } catch (InvalidPathException e) {
                    printError("InvalidPathException for output file", e);
                    return;
                }

                if (outputFile.getParent() != null) {
                    try {
                        Files.createDirectories(outputFile.getParent());
                    } catch (IOException e) {
                        printError("IOException directories for output file can't be created", e);
                        return;
                    }
                }

                try (final BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
                    try (final BufferedWriter writer = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
                        String currFile = reader.readLine();
                        while (currFile != null) {
                            try {
                                final Path currPath = Paths.get(currFile);
                                Walk.printHashWithFile(hash(currPath), currPath.toString(), writer);
                            } catch (InvalidPathException e) {
                                Walk.printHashWithFile(0, currFile, writer);
                            }
                            currFile = reader.readLine();
                        }
                    } catch (FileNotFoundException e) {
                        printError("FileNotFoundException no output file", e);
                    } catch (IOException e) {
                        printError("IOException while working with output file", e);
                    }
                } catch (FileNotFoundException e) {
                    printError("FileNotFoundException no input file", e);
                } catch (IOException e) {
                    printError("IOException while working with input file", e);
                }
            }
        }
    }
}
