package com.aetherteam.aether.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public final class AetherConfigFile {
    static final long MAX_FILE_BYTES = 1024L * 1024L;
    static final int MAX_LINE_LENGTH = 32_768;
    static final int MAX_LIST_ENTRIES = 1_024;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern INTEGER = Pattern.compile("[+-]?(?:0|[1-9][0-9_]*|[0-9][0-9_]*)");
    private static final AetherConfigEntry.ValueCodec<Boolean> BOOLEAN_CODEC = new AetherConfigEntry.ValueCodec<>() {
        @Override
        public Boolean parse(String value) {
            return switch (value.trim()) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException("expected true or false");
            };
        }

        @Override
        public String format(Boolean value) {
            return value.toString();
        }
    };
    private static final AetherConfigEntry.ValueCodec<Integer> INTEGER_CODEC = new AetherConfigEntry.ValueCodec<>() {
        @Override
        public Integer parse(String value) {
            String normalized = value.trim();
            if (!INTEGER.matcher(normalized).matches()) {
                throw new IllegalArgumentException("expected a decimal integer");
            }
            return Integer.parseInt(normalized.replace("_", ""));
        }

        @Override
        public String format(Integer value) {
            return value.toString();
        }
    };
    private static final AetherConfigEntry.ValueCodec<String> STRING_CODEC = new AetherConfigEntry.ValueCodec<>() {
        @Override
        public String parse(String value) {
            return parseString(value);
        }

        @Override
        public String format(String value) {
            return quote(value);
        }
    };
    private static final AetherConfigEntry.ValueCodec<List<String>> STRING_LIST_CODEC = new AetherConfigEntry.ValueCodec<>() {
        @Override
        public List<String> parse(String value) {
            return parseStringList(value);
        }

        @Override
        public String format(List<String> value) {
            return value.stream().map(AetherConfigFile::quote).collect(java.util.stream.Collectors.joining(", ", "[", "]"));
        }

        @Override
        public List<String> copy(List<String> value) {
            return List.copyOf(value);
        }
    };

    private final String fileName;
    private final Map<List<String>, AetherConfigEntry<?>> entries = new LinkedHashMap<>();
    private Path path;
    private Document document;
    private boolean loaded;
    private boolean writable;

    public AetherConfigFile(String fileName) {
        if (fileName.isBlank() || Path.of(fileName).getNameCount() != 1) {
            throw new IllegalArgumentException("Config file name must be a single path segment");
        }
        this.fileName = fileName;
    }

    public String fileName() {
        return this.fileName;
    }

    public synchronized BooleanConfigEntry booleanEntry(String section, String key, boolean defaultValue, String comment) {
        return this.booleanEntry(section, key, defaultValue, comment, AetherConfigEntry.RestartRequirement.NONE);
    }

    public synchronized BooleanConfigEntry worldRestartBooleanEntry(String section, String key, boolean defaultValue, String comment) {
        return this.booleanEntry(section, key, defaultValue, comment, AetherConfigEntry.RestartRequirement.WORLD);
    }

    private BooleanConfigEntry booleanEntry(String section, String key, boolean defaultValue, String comment,
                                             AetherConfigEntry.RestartRequirement restartRequirement) {
        BooleanConfigEntry entry = new BooleanConfigEntry(this, path(section, key), defaultValue, comment, restartRequirement, BOOLEAN_CODEC);
        return this.register(entry);
    }

    public synchronized AetherConfigEntry<Integer> integerEntry(String section, String key, int defaultValue, String comment) {
        return this.register(new AetherConfigEntry<>(this, path(section, key), defaultValue, comment,
                AetherConfigEntry.RestartRequirement.NONE, INTEGER_CODEC));
    }

    public synchronized AetherConfigEntry<String> stringEntry(String section, String key, String defaultValue, String comment) {
        return this.register(new AetherConfigEntry<>(this, path(section, key), defaultValue, comment,
                AetherConfigEntry.RestartRequirement.NONE, STRING_CODEC));
    }

    public synchronized AetherConfigEntry<List<String>> stringListEntry(String section, String key, List<String> defaultValue, String comment) {
        return this.register(new AetherConfigEntry<>(this, path(section, key), defaultValue, comment,
                AetherConfigEntry.RestartRequirement.NONE, STRING_LIST_CODEC));
    }

    private <T extends AetherConfigEntry<?>> T register(T entry) {
        if (this.loaded) {
            throw new IllegalStateException("Cannot add entries after " + this.fileName + " has loaded");
        }
        if (this.entries.putIfAbsent(entry.path(), entry) != null) {
            throw new IllegalArgumentException("Duplicate config entry " + entry.path());
        }
        return entry;
    }

    public synchronized void load(Path directory) {
        Objects.requireNonNull(directory, "directory");
        this.resetEntries();
        this.path = directory.resolve(this.fileName);
        this.document = null;
        this.writable = false;

        try {
            Files.createDirectories(directory);
            if (Files.notExists(this.path)) {
                this.document = Document.canonical(this.entries.values());
                this.writeDocument();
                this.entries.values().forEach(AetherConfigEntry::markPersisted);
            } else {
                long size = Files.size(this.path);
                if (size > MAX_FILE_BYTES) {
                    throw new IOException("file is " + size + " bytes; limit is " + MAX_FILE_BYTES);
                }
                String content = Files.readString(this.path, StandardCharsets.UTF_8);
                this.document = Document.parse(content);
                this.loadEntries(this.document);
                this.writable = true;
            }
        } catch (IOException | IllegalArgumentException exception) {
            LOGGER.error("Unable to load Aether config {}; defaults will be used and the existing file will not be overwritten", this.path, exception);
            this.document = null;
            this.writable = false;
        } finally {
            this.loaded = true;
        }
    }

    public synchronized void applySynchronizedValues(Map<String, String> values) {
        Objects.requireNonNull(values, "values");
        if (this.loaded && this.path != null) {
            return;
        }
        this.resetEntries();
        this.path = null;
        this.document = null;
        this.writable = false;
        for (Map.Entry<String, String> value : values.entrySet()) {
            AetherConfigEntry<?> entry = this.entries.get(parseSerializedPath(value.getKey()));
            if (entry == null) {
                continue;
            }
            try {
                entry.load(value.getValue());
            } catch (IllegalArgumentException exception) {
                LOGGER.warn("Ignoring invalid synchronized value for {} in {}: {}", entry.path(), this.fileName, exception.getMessage());
            }
        }
        this.loaded = true;
    }

    public synchronized void clearSynchronizedValues() {
        if (this.loaded && this.path == null) {
            this.unload();
        }
    }

    public synchronized Map<String, String> synchronizedValues() {
        Map<String, String> values = new LinkedHashMap<>();
        this.entries.forEach((entryPath, entry) -> values.put(serializePath(entryPath), entry.serializedValue()));
        return Collections.unmodifiableMap(values);
    }

    public synchronized void unload() {
        this.resetEntries();
        this.path = null;
        this.document = null;
        this.loaded = false;
        this.writable = false;
    }

    public synchronized boolean isLoaded() {
        return this.loaded;
    }

    public Map<List<String>, AetherConfigEntry<?>> entries() {
        return Collections.unmodifiableMap(this.entries);
    }

    public synchronized void save() {
        if (!this.loaded || this.path == null) {
            throw new IllegalStateException(this.fileName + " is not loaded from a writable local file");
        }
        if (!this.writable || this.document == null) {
            throw new IllegalStateException(this.fileName + " could not be loaded safely and will not be overwritten");
        }

        List<AetherConfigEntry<?>> changed = this.entries.values().stream().filter(AetherConfigEntry::hasUnsavedValue).toList();
        if (changed.isEmpty()) {
            return;
        }
        for (AetherConfigEntry<?> entry : changed) {
            this.document = this.document.withValue(entry);
        }
        try {
            this.writeDocument();
            changed.forEach(AetherConfigEntry::markPersisted);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to save Aether config " + this.path, exception);
        }
    }

    public static String serializePath(List<String> path) {
        return path.toString();
    }

    public static List<String> parseSerializedPath(String value) {
        String trimmed = value.trim();
        if (trimmed.length() < 2 || trimmed.charAt(0) != '[' || trimmed.charAt(trimmed.length() - 1) != ']') {
            return List.of();
        }
        String body = trimmed.substring(1, trimmed.length() - 1);
        if (body.isEmpty()) {
            return List.of();
        }
        return List.of(body.split(", ", -1));
    }

    private void loadEntries(Document parsedDocument) {
        for (Map.Entry<List<String>, Assignment> assignment : parsedDocument.assignments.entrySet()) {
            AetherConfigEntry<?> entry = this.entries.get(assignment.getKey());
            if (entry == null) {
                continue;
            }
            try {
                entry.load(assignment.getValue().value());
            } catch (IllegalArgumentException exception) {
                LOGGER.warn("Ignoring invalid value for {} in {}: {}", entry.path(), this.path, exception.getMessage());
            }
        }
    }

    private void resetEntries() {
        this.entries.values().forEach(AetherConfigEntry::reset);
    }

    private void writeDocument() throws IOException {
        Path parent = this.path.getParent();
        Files.createDirectories(parent);
        Path temporary = Files.createTempFile(parent, "." + this.path.getFileName(), ".tmp");
        boolean moved = false;
        try {
            Files.writeString(temporary, this.document.content(), StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                Files.move(temporary, this.path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporary, this.path, StandardCopyOption.REPLACE_EXISTING);
            }
            moved = true;
            this.writable = true;
        } finally {
            if (!moved) {
                Files.deleteIfExists(temporary);
            }
        }
    }

    private static List<String> path(String section, String key) {
        if (section.isBlank() || key.isBlank()) {
            throw new IllegalArgumentException("Config sections and keys cannot be blank");
        }
        return List.of(section, key);
    }

    private static String quote(String value) {
        StringBuilder result = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '\\' -> result.append("\\\\");
                case '"' -> result.append("\\\"");
                case '\b' -> result.append("\\b");
                case '\t' -> result.append("\\t");
                case '\n' -> result.append("\\n");
                case '\f' -> result.append("\\f");
                case '\r' -> result.append("\\r");
                default -> {
                    if (character < 0x20 || character == 0x7F) {
                        result.append(String.format("\\u%04X", (int) character));
                    } else {
                        result.append(character);
                    }
                }
            }
        }
        return result.append('"').toString();
    }

    private static String parseString(String value) {
        String trimmed = value.trim();
        if (trimmed.length() < 2) {
            throw new IllegalArgumentException("expected a quoted string");
        }
        char quote = trimmed.charAt(0);
        if ((quote != '"' && quote != '\'') || trimmed.charAt(trimmed.length() - 1) != quote) {
            throw new IllegalArgumentException("expected a quoted string");
        }
        if (quote == '\'') {
            return trimmed.substring(1, trimmed.length() - 1);
        }

        StringBuilder result = new StringBuilder();
        for (int i = 1; i < trimmed.length() - 1; i++) {
            char character = trimmed.charAt(i);
            if (character != '\\') {
                result.append(character);
                continue;
            }
            if (++i >= trimmed.length() - 1) {
                throw new IllegalArgumentException("unterminated string escape");
            }
            char escaped = trimmed.charAt(i);
            switch (escaped) {
                case 'b' -> result.append('\b');
                case 't' -> result.append('\t');
                case 'n' -> result.append('\n');
                case 'f' -> result.append('\f');
                case 'r' -> result.append('\r');
                case '"' -> result.append('"');
                case '\\' -> result.append('\\');
                case 'u', 'U' -> {
                    int digits = escaped == 'u' ? 4 : 8;
                    if (i + digits >= trimmed.length()) {
                        throw new IllegalArgumentException("incomplete unicode escape");
                    }
                    String hexadecimal = trimmed.substring(i + 1, i + 1 + digits);
                    try {
                        result.appendCodePoint(Integer.parseUnsignedInt(hexadecimal, 16));
                    } catch (IllegalArgumentException exception) {
                        throw new IllegalArgumentException("invalid unicode escape", exception);
                    }
                    i += digits;
                }
                default -> throw new IllegalArgumentException("unsupported string escape \\" + escaped);
            }
        }
        return result.toString();
    }

    private static List<String> parseStringList(String value) {
        String trimmed = value.trim();
        if (trimmed.length() < 2 || trimmed.charAt(0) != '[' || trimmed.charAt(trimmed.length() - 1) != ']') {
            throw new IllegalArgumentException("expected an array of strings");
        }
        List<String> result = new ArrayList<>();
        int index = 1;
        while (index < trimmed.length() - 1) {
            while (index < trimmed.length() - 1 && (Character.isWhitespace(trimmed.charAt(index)) || trimmed.charAt(index) == ',')) {
                index++;
            }
            if (index >= trimmed.length() - 1) {
                break;
            }
            if (result.size() >= MAX_LIST_ENTRIES) {
                throw new IllegalArgumentException("string list exceeds " + MAX_LIST_ENTRIES + " entries");
            }
            char quote = trimmed.charAt(index);
            if (quote != '"' && quote != '\'') {
                throw new IllegalArgumentException("list entries must be strings");
            }
            int start = index++;
            boolean escaped = false;
            while (index < trimmed.length() - 1) {
                char character = trimmed.charAt(index++);
                if (quote == '"' && character == '\\' && !escaped) {
                    escaped = true;
                    continue;
                }
                if (character == quote && !escaped) {
                    break;
                }
                escaped = false;
            }
            if (trimmed.charAt(index - 1) != quote) {
                throw new IllegalArgumentException("unterminated string in list");
            }
            result.add(parseString(trimmed.substring(start, index)));
            while (index < trimmed.length() - 1 && Character.isWhitespace(trimmed.charAt(index))) {
                index++;
            }
            if (index < trimmed.length() - 1 && trimmed.charAt(index) != ',') {
                throw new IllegalArgumentException("expected a comma between list entries");
            }
        }
        return List.copyOf(result);
    }

    private record Assignment(int startLine, int endLine, int equalsIndex, int commentIndex, String value) {
    }

    private record Section(String name, int line) {
    }

    private static final class Document {
        private final List<String> lines;
        private final String newline;
        private final Map<List<String>, Assignment> assignments;
        private final List<Section> sections;

        private Document(List<String> lines, String newline, Map<List<String>, Assignment> assignments, List<Section> sections) {
            this.lines = lines;
            this.newline = newline;
            this.assignments = assignments;
            this.sections = sections;
        }

        static Document parse(String content) {
            String newline = content.contains("\r\n") ? "\r\n" : "\n";
            String[] rawLines = content.split("\r\n|\n|\r", -1);
            List<String> lines = new ArrayList<>(List.of(rawLines));
            if (!lines.isEmpty() && lines.get(0).startsWith("\uFEFF")) {
                lines.set(0, lines.get(0).substring(1));
            }
            if (lines.size() > 65_536) {
                throw new IllegalArgumentException("config contains too many lines");
            }

            Map<List<String>, Assignment> assignments = new LinkedHashMap<>();
            List<Section> sections = new ArrayList<>();
            String section = null;
            for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
                String line = lines.get(lineIndex);
                if (line.length() > MAX_LINE_LENGTH) {
                    throw new IllegalArgumentException("line " + (lineIndex + 1) + " exceeds " + MAX_LINE_LENGTH + " characters");
                }
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                String parsedSection = parseSection(trimmed);
                if (parsedSection != null) {
                    section = parsedSection;
                    sections.add(new Section(section, lineIndex));
                    continue;
                }
                if (section == null) {
                    continue;
                }
                int equalsIndex = findOutsideString(line, '=');
                if (equalsIndex < 0) {
                    continue;
                }
                String key;
                try {
                    key = parseKey(line.substring(0, equalsIndex));
                } catch (IllegalArgumentException ignored) {
                    continue;
                }

                int commentIndex = findOutsideString(line, '#', equalsIndex + 1);
                String initialValue = line.substring(equalsIndex + 1, commentIndex >= 0 ? commentIndex : line.length());
                StringBuilder value = new StringBuilder(initialValue.trim());
                int endLine = lineIndex;
                if (startsArray(value) && arrayDepth(value.toString()) > 0) {
                    int depth = arrayDepth(value.toString());
                    while (depth > 0 && ++endLine < lines.size()) {
                        if (endLine - lineIndex > MAX_LIST_ENTRIES + 2) {
                            throw new IllegalArgumentException("array beginning on line " + (lineIndex + 1) + " is too long");
                        }
                        String continuation = lines.get(endLine);
                        int continuationComment = findOutsideString(continuation, '#');
                        String withoutComment = continuation.substring(0, continuationComment >= 0 ? continuationComment : continuation.length());
                        value.append('\n').append(withoutComment.trim());
                        depth += arrayDepth(withoutComment);
                    }
                    if (depth != 0) {
                        throw new IllegalArgumentException("unterminated array beginning on line " + (lineIndex + 1));
                    }
                }
                assignments.put(List.of(section, key), new Assignment(lineIndex, endLine, equalsIndex, commentIndex, value.toString()));
                lineIndex = endLine;
            }
            return new Document(lines, newline, assignments, sections);
        }

        static Document canonical(Iterable<AetherConfigEntry<?>> entries) {
            String newline = System.lineSeparator();
            List<String> lines = new ArrayList<>();
            String section = null;
            for (AetherConfigEntry<?> entry : entries) {
                String entrySection = entry.path().get(0);
                if (!entrySection.equals(section)) {
                    if (!lines.isEmpty()) {
                        lines.add("");
                    }
                    section = entrySection;
                    lines.add(formatSection(section));
                }
                lines.add("\t#" + entry.comment());
                lines.add("\t" + quote(entry.path().get(1)) + " = " + entry.serializedValue());
            }
            lines.add("");
            return parse(String.join(newline, lines));
        }

        Document withValue(AetherConfigEntry<?> entry) {
            List<String> updated = new ArrayList<>(this.lines);
            Assignment assignment = this.assignments.get(entry.path());
            if (assignment != null) {
                String currentLine = updated.get(assignment.startLine());
                String prefix = currentLine.substring(0, assignment.equalsIndex() + 1);
                String suffix = assignment.commentIndex() >= 0 ? " " + currentLine.substring(assignment.commentIndex()) : "";
                updated.subList(assignment.startLine(), assignment.endLine() + 1).clear();
                updated.add(assignment.startLine(), prefix + " " + entry.serializedValue() + suffix);
            } else {
                int insertion = this.insertionLine(entry.path().get(0));
                List<String> newLines = List.of(
                        "\t#" + entry.comment(),
                        "\t" + quote(entry.path().get(1)) + " = " + entry.serializedValue());
                if (insertion >= 0) {
                    updated.addAll(insertion, newLines);
                } else {
                    if (!updated.isEmpty() && !updated.get(updated.size() - 1).isEmpty()) {
                        updated.add("");
                    }
                    updated.add(formatSection(entry.path().get(0)));
                    updated.addAll(newLines);
                    updated.add("");
                }
            }
            return parse(String.join(this.newline, updated));
        }

        String content() {
            return String.join(this.newline, this.lines);
        }

        private int insertionLine(String sectionName) {
            for (int i = 0; i < this.sections.size(); i++) {
                Section section = this.sections.get(i);
                if (!section.name().equals(sectionName)) {
                    continue;
                }
                int insertion = i + 1 < this.sections.size() ? this.sections.get(i + 1).line() : this.lines.size();
                while (insertion > section.line() + 1 && this.lines.get(insertion - 1).isEmpty()) {
                    insertion--;
                }
                return insertion;
            }
            return -1;
        }

        private static String parseSection(String line) {
            if (!line.startsWith("[") || line.startsWith("[[")) {
                return null;
            }
            int close = findOutsideString(line, ']');
            if (close < 0) {
                return null;
            }
            String trailing = line.substring(close + 1).trim();
            if (!trailing.isEmpty() && !trailing.startsWith("#")) {
                return null;
            }
            return parseKey(line.substring(1, close));
        }

        private static String parseKey(String key) {
            String trimmed = key.trim();
            if (trimmed.startsWith("\"") || trimmed.startsWith("'")) {
                return parseString(trimmed);
            }
            if (trimmed.matches("[A-Za-z0-9_-]+")) {
                return trimmed;
            }
            throw new IllegalArgumentException("unsupported TOML key");
        }

        private static String formatSection(String section) {
            return section.matches("[A-Za-z0-9_-]+") ? "[" + section + "]" : "[" + quote(section) + "]";
        }

        private static boolean startsArray(StringBuilder value) {
            return !value.isEmpty() && value.charAt(0) == '[';
        }

        private static int arrayDepth(String value) {
            int depth = 0;
            boolean quoted = false;
            boolean literal = false;
            boolean escaped = false;
            for (int i = 0; i < value.length(); i++) {
                char character = value.charAt(i);
                if (quoted) {
                    if (character == '"' && !escaped) {
                        quoted = false;
                    }
                    escaped = character == '\\' && !escaped;
                    if (character != '\\') {
                        escaped = false;
                    }
                } else if (literal) {
                    if (character == '\'') {
                        literal = false;
                    }
                } else if (character == '"') {
                    quoted = true;
                } else if (character == '\'') {
                    literal = true;
                } else if (character == '[') {
                    depth++;
                } else if (character == ']') {
                    depth--;
                } else if (character == '#') {
                    break;
                }
            }
            return depth;
        }

        private static int findOutsideString(String value, char target) {
            return findOutsideString(value, target, 0);
        }

        private static int findOutsideString(String value, char target, int start) {
            boolean quoted = false;
            boolean literal = false;
            boolean escaped = false;
            for (int i = start; i < value.length(); i++) {
                char character = value.charAt(i);
                if (quoted) {
                    if (character == '"' && !escaped) {
                        quoted = false;
                    }
                    escaped = character == '\\' && !escaped;
                    if (character != '\\') {
                        escaped = false;
                    }
                } else if (literal) {
                    if (character == '\'') {
                        literal = false;
                    }
                } else if (character == '"') {
                    quoted = true;
                } else if (character == '\'') {
                    literal = true;
                } else if (character == target) {
                    return i;
                }
            }
            return -1;
        }
    }
}
