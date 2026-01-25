package com.app.notes.integration.util;

import java.util.Map;
import java.util.Optional;

public final class PathReplacer {

  private PathReplacer() {
    // Utility class - prevent instantiation
  }

  public static String replacePath(
      String path, String entityType, Map<Long, Long> idMappings, Optional<String> storedId) {
    String result = replaceStoredIdPlaceholder(path, storedId);
    result = replaceHardcodedIds(result, entityType, idMappings);
    return result;
  }

  private static String replaceStoredIdPlaceholder(String path, Optional<String> storedId) {
    return storedId
        .map(id -> path.replace("{storedId}", id))
        .orElseGet(() -> path.replace("{storedId}", ""));
  }

  private static String replaceHardcodedIds(
      String path, String entityType, Map<Long, Long> idMappings) {
    String result = path;
    String entityPath = "/" + entityType + "/";
    for (Map.Entry<Long, Long> entry : idMappings.entrySet()) {
      result = result.replace(entityPath + entry.getKey(), entityPath + entry.getValue());
      result =
          result.replace(entityPath + entry.getKey() + "/", entityPath + entry.getValue() + "/");
    }
    return result;
  }
}
