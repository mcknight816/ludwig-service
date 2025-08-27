package com.bluntsoftware.ludwig.utils;

import com.bluntsoftware.ludwig.domain.McpServerDescriptor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Traverses arbitrary nested structures (maps/lists) and finds nodes at depth >= 4
 * that can be converted into McpServerDescriptor instances.
 *
 * Depth counting starts at 0 for the root. A direct child is depth 1, etc.
 */
public class McpServerDescriptorJsonFinder {

    private final ObjectMapper mapper;

    public McpServerDescriptorJsonFinder() {
        this(new ObjectMapper());
    }

    public McpServerDescriptorJsonFinder(ObjectMapper mapper) {
        this.mapper = mapper.copy()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
    }

    // Entry points

    public List<FoundDescriptor> findInString(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        return findInNode(root);
    }

    public List<FoundDescriptor> findInMap(Map<String, Object> map) {
        JsonNode root = mapper.valueToTree(map);
        return findInNode(root);
    }

    public List<FoundDescriptor> findInFile(File file) throws IOException {
        try (JsonParser jp = mapper.getFactory().createParser(file)) {
            JsonNode root = mapper.readTree(jp);
            return findInNode(root);
        }
    }

    public List<FoundDescriptor> findInPath(Path path) throws IOException {
        return findInFile(path.toFile());
    }

    public List<FoundDescriptor> findInNode(JsonNode root) {
        List<FoundDescriptor> results = new ArrayList<>();
        walk(root, "$", 0, results);
        return results;
    }

    // Core traversal

    private void walk(JsonNode node, String path, int depth, List<FoundDescriptor> out) {
        if (node == null || node.isNull()) return;

        if (node.isObject()) {
            if (depth <= 6) {
                extractDescriptorIfPresent((ObjectNode) node).ifPresent(d ->
                        out.add(new FoundDescriptor(path, d)));
            }
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> f = fields.next();
                walk(f.getValue(), path + "." + escapeKey(f.getKey()), depth + 1, out);
            }
        } else if (node.isArray()) {
            int idx = 0;
            for (JsonNode child : node) {
                walk(child, path + "[" + idx + "]", depth + 1, out);
                idx++;
            }
        }
    }

    private String escapeKey(String key) {
        return key.contains(".") ? "['" + key + "']" : key;
    }

    // Descriptor extraction logic

    private Optional<McpServerDescriptor> extractDescriptorIfPresent(ObjectNode obj) {
        Map<String, JsonNode> fields = normalizeFields(obj);

        String name = asText(fields, "name");
        String transport = firstNonNullText(fields, "transport");
        String command = firstNonNullText(fields, "command", "cmd");
        String url = firstNonNullText(fields, "url");
        String[] args = asStringArray(fields, "args");
        Map<String, String> environment = asStringMap(fields, "environment", "env");

        boolean presentEnough =  notBlank(command) || (args != null && notBlank(args[0]));
        if (!presentEnough) return Optional.empty();

        McpServerDescriptor descriptor = McpServerDescriptor.builder()
                .name(name)
                .transport(transport)
                .command(command)
                .url(url)
                .args(args)
                .environment(environment)
                .build();

        return Optional.of(descriptor);
    }

    private Map<String, JsonNode> normalizeFields(ObjectNode obj) {
        Map<String, JsonNode> map = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = obj.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            map.put(e.getKey().toLowerCase(Locale.ROOT), e.getValue());
        }
        return map;
    }

    private String firstNonNullText(Map<String, JsonNode> fields, String... keys) {
        for (String k : keys) {
            String v = asText(fields, k);
            if (notBlank(v)) return v;
        }
        return null;
    }

    private String asText(Map<String, JsonNode> fields, String key) {
        JsonNode n = fields.get(key.toLowerCase(Locale.ROOT));
        if (n == null || n.isNull()) return null;
        if (n.isTextual() || n.isNumber() || n.isBoolean()) return n.asText();
        return null;
    }

    private String[] asStringArray(Map<String, JsonNode> fields, String key) {
        JsonNode n = fields.get(key.toLowerCase(Locale.ROOT));
        if (n == null || n.isNull()) return null;
        if (n.isTextual()) {
            String text = n.asText();
            if (text.contains(",")) {
                return Arrays.stream(text.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
            }
            if (text.contains(" ")) {
                return Arrays.stream(text.split("\\s+"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
            }
            return new String[]{ text };
        }
        if (n.isArray()) {
            List<String> list = new ArrayList<>();
            for (JsonNode item : n) {
                if (item.isTextual() || item.isNumber() || item.isBoolean()) {
                    list.add(item.asText());
                }
            }
            return list.isEmpty() ? null : list.toArray(new String[0]);
        }
        return null;
    }

    private Map<String, String> asStringMap(Map<String, JsonNode> fields, String... keys) {
        for (String key : keys) {
            JsonNode n = fields.get(key.toLowerCase(Locale.ROOT));
            Map<String, String> converted = toStringMap(n);
            if (converted != null) return converted;
        }
        return null;
    }

    private Map<String, String> toStringMap(JsonNode n) {
        if (n == null || n.isNull() || !n.isObject()) return null;
        Map<String, String> m = new LinkedHashMap<>();
        Iterator<Map.Entry<String, JsonNode>> it = n.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            JsonNode v = e.getValue();
            if (v != null && !v.isNull() && (v.isTextual() || v.isNumber() || v.isBoolean())) {
                m.put(e.getKey(), v.asText());
            }
        }
        return m.isEmpty() ? null : m;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    @Value
    public static class FoundDescriptor {
        String jsonPath;
        McpServerDescriptor descriptor;
    }
}
