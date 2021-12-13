package info.kgeorgiy.ja.vircev.implementor;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

/**
 * Realisation of {@link JarImpler} interface
 */
public class Implementor implements JarImpler {
    private static final String LINE_SEP = System.lineSeparator();

    /**
     *
     * @param token type token to create implementation for.
     * @param jarFile target <var>.jar</var> file.
     * @throws ImplerException throws if:
     *                          <ul>
     *                              <li> Creating of temp directory for implementation failed</li>
     *                              <li> Creating if directory for .jar file failed</li>
     *                              <li> Given class is not an interface</li>
     *                              <li> {@link Modifier} of given class is not public</li>
     *                              <li> Writing to .java file failed</li>
     *                              <li> {@link JavaCompiler} failed to compile</li>
     *                              <li> Writing to jar file failed</li>
     *                          </ul>
     */
    @Override
    public void implementJar(final Class<?> token, final Path jarFile) throws ImplerException {
        createParent(jarFile);

        final Path tempPath;

        try {
            tempPath = Files.createTempDirectory(jarFile.getParent() == null ? Path.of("") : jarFile.getParent(), "jarDir");
        } catch (final IOException e) {
            throw new ImplerException("IOException temporary directory can't be created", e);
        }

        try {
            implement(token, tempPath);
            compileFiles(token, tempPath);
            createJar(token, jarFile, tempPath);
        } catch(final ImplerException e) {
            // :NOTE: Обработка ошибок??
            throw new ImplerException("Error while creating implementation", e);
        } finally {
            try {
                clean(tempPath);
            } catch (final IOException e) {
                System.out.println("Error while deleting temporary directory");
            }
        }
    }

    /**
     *
     * @param token type token to create implementation for.
     * @param root root directory.
     * @throws ImplerException throws if:
     *                          <ul>
     *                              <li> Given class is not an interface</li>
     *                              <li> {@link Modifier} of given class is not public</li>
     *                              <li> Writing to .java file failed</li>
     *                          </ul>
     */
    @Override
    public void implement(final Class<?> token, final Path root) throws ImplerException {
        if (!token.isInterface()) {
            // :NOTE: Подробности??
            throw new ImplerException("Can't create class " + token.getSimpleName() + "Impl because given token is not an interface");
        }
        if (Modifier.isPrivate(token.getModifiers())) {
            throw new ImplerException("Can't create class " + token.getSimpleName() + "Impl token is private");
        }

        // :NOTE: Упростить
        final Path path = getPath(token, root);

        createParent(path);

        // :NOTE: Кодировка по-умолчанию
        try {
            Files.writeString(path, translateToUnicode(generateClass(token)));
        } catch (final IOException e) {
            throw new ImplerException("IOException occurred while writing to " + path.toString(), e);
        }
    }

    /**
     * Converting {@link String} to Unicode
     *
     * @param code is code that will be translated to Unicode
     * @return code translated to Unicode
     */
    static String translateToUnicode(final String code) {
        final StringBuilder builder = new StringBuilder();
        for (final char symb : code.toCharArray()) {
            if (symb < 128) {
                builder.append(symb);
            } else {
                builder.append(String.format("\\u%04X", (int) symb));
            }
        }
        return builder.toString();
    }

    /**
     * Compiles implementation of given class that contains in given path
     *
     * @param token is {@link Class} that implementation made from
     * @param path is {@link Path} of java file
     * @throws ImplerException throws if {@link JavaCompiler} failed to compile
     */
    private void compileFiles(final Class<?> token, final Path path) throws ImplerException {
        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null || compiler.run(null, null, null,
                "-cp", getClasspath(token, path),
                "-encoding", "UTF8",
                getPath(token, path).toString()) != 0) {
            throw new ImplerException("Error while compiling files");
        }
    }

    private static String getClasspath(final Class<?> token, final Path path) throws ImplerException {
        try {
            return path + File.pathSeparator + Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (final URISyntaxException e) {
            throw new ImplerException("Error while creating classpath", e);
        }
    }

    /**
     * Creates jar file with compiled implementation of token
     *
     * @param token is {@link Class} that implementation made from
     * @param jarFile is {@link Path} where completed jar file should be
     * @param tempPath is {@link Path} where compiled implementation contains
     * @throws ImplerException throws if writing to jar file failed
     */
    private static void createJar(final Class<?> token, final Path jarFile, final Path tempPath) throws ImplerException {
        final Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        try (final JarOutputStream output = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            final String fileName = token.getPackageName().replace('.', '/')
                    + '/' + token.getSimpleName() + "Impl.class";
            output.putNextEntry(new ZipEntry(fileName));
            Files.copy(tempPath.resolve(fileName), output);
        } catch (final IOException e) {
            throw new ImplerException("Error while writing to jar file", e);
        }
    }

    /**
     * Recursively deletes given directory
     *
     * @param root is {@link Path} of given directory
     * @throws IOException if an error occurred while deleting directory
     */
    public static void clean(final Path root) throws IOException {
        if (Files.exists(root)) {
            Files.walkFileTree(root, DELETE_VISITOR);
        }
    }

    /**
     * Returns path of implementation of token
     *
     * @param token is {@link Class} that implementation made from
     * @param path is {@link Path} of directory where package of implementation contains
     * @return {@link Path} of implementation of token
     */
    private Path getPath(final Class<?> token, final Path path) {
        return path.resolve(token.getPackageName().replace(".", File.separator)
                + File.separator + token.getSimpleName() + "Impl.java");
    }

    /**
     * Creates parent directory of path
     *
     * @param path is given {@link Path}
     * @throws ImplerException if directories can't be created
     */
    private void createParent(final Path path) throws ImplerException {
        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (final IOException e) {
                throw new ImplerException("IOException directories for output file can't be created", e);
            }
        }
    }

    /**
     * Creates code of method by given {@link Method}
     *
     * @param method is {@link Method} of {@link Class}
     * @return code of given {@link Method}
     */
    private static String methodCode(final Method method) {
        final Class<?> returnType = method.getReturnType();

        return String.format("  public %s %s(%s) {%s        return%s;%s    }",
                returnType.getCanonicalName(),
                method.getName(),
                Arrays.stream(method.getParameters())
                        .map(param -> param.getType().getCanonicalName() + " " + param.getName())
                        .collect(Collectors.joining(", ")),
                LINE_SEP,
                getDefaultValue(returnType),
                LINE_SEP
        );
    }

    private static String getDefaultValue(final Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return " null";
        } else if (returnType == void.class) {
            return "";
        } else if (returnType == boolean.class) {
            return  " false";
        } else {
            return  " 0";
        }
    }

    /**
     * Creates code of implementation of given {@link Class}
     *
     * @param token is {@link Class} that implementation made from
     * @return code of implementation
     */
    private static String generateClass(final Class<?> token) {
        // :NOTE: Переводы строк
        return String.format("%spublic class %sImpl implements %s {%s    %s%s}",
                !token.getPackageName().isEmpty() ? "package " + token.getPackageName() + ";" + LINE_SEP + LINE_SEP : "",
                token.getSimpleName(),
                token.getCanonicalName(),
                LINE_SEP,
                Arrays.stream(token.getMethods())
                        .map(Implementor::methodCode)
                        .collect(Collectors.joining(LINE_SEP)),
                LINE_SEP);
    }

    private static final SimpleFileVisitor<Path> DELETE_VISITOR = new SimpleFileVisitor<>() {

        /**
         * Deletes file in directory
         *
         * @param file {@link Path} of file
         * @param attrs {@link BasicFileAttributes}
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException throws if deleting of file failed
         */
        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        /**
         * Deletes directory after deleting all files
         *
         * @param dir {@link Path} of directory
         * @param exc {@link IOException}
         * @return {@link FileVisitResult#CONTINUE}
         * @throws IOException throws if deleting of directory failed
         */
        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    };

    /**
     * Creates implementation of given {@link Class} in given {@link Path} if has 2 arguments. Creates .jar file
     * of implementations if has 3 arguments
     *
     * @param args is arguments of command line
     */
    public static void main(final String[] args) {
        if (args == null || args.length < 2 || args.length > 3 || (args.length == 3 && !args[0].equals("-jar"))) {
            System.out.println("Wrong arguments. Should be: -jar [InterfaceName] [JarFile]" + LINE_SEP +
                    "or: [InterfaceName] [RootPath]");
            return;
        }

        final JarImpler implementor = new Implementor();
        try {
            if (args.length == 2) {
                implementor.implement(Class.forName(args[0]), Paths.get(args[1]));
            } else {
                implementor.implementJar(Class.forName(args[1]), Paths.get(args[2]));
            }
        } catch (final ClassNotFoundException e) {
            System.err.println("Invalid class name: " + e.getMessage());
        } catch (final InvalidPathException e) {
            System.err.println("Invalid root or jar file path: " + e.getMessage());
        } catch (final ImplerException e) {
            System.err.println("Error while creating implementation: " + e.getMessage());
        }
    }
    // :NOTE: :(
}