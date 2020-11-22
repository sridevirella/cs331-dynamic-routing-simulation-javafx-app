package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileReadWrite {

    private FileReadWrite() {}

    public static void createFile(String filePathString, String fileName) throws IOException {

        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(filePathString));
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(getInputPath(fileName))) {
            bufferedReader.lines().forEach(line -> {
                try {
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Path getInputPath(String fileName) throws IOException {

        Path path = Path.of("build", "resources", "main").toAbsolutePath();
        if(!Files.exists(path)) {
            Files.createDirectories(path);
        }
        return Path.of( path.toString(), fileName);
    }

    public static Path getOutPath(String fileName) throws IOException {

        return Path.of("build", "resources", "main", fileName).toRealPath();
    }
}
