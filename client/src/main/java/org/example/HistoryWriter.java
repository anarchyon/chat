package org.example;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class HistoryWriter {
    private final Path historyFilePath;
    private final int messageToRestore = 100;

    public HistoryWriter(String login) {
        historyFilePath = Paths.get(String.format("client\\history\\history_%s.txt", login));
        try {
            Files.createDirectories(historyFilePath.getParent());
            if (!Files.exists(historyFilePath)) {
                Files.createFile(historyFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readHistory() throws IOException {
        List<String> messages = Files.readAllLines(historyFilePath, StandardCharsets.UTF_8);
        int messageAll = messages.size();
        if (messageAll > messageToRestore) {
            messages = messages.subList(messageAll - messageToRestore, messageAll);
        }
        return messages;
    }

    public void writeHistory(String message) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(
                historyFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        writer.write(message + "\n");
        writer.close();
    }
}
