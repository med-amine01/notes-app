package com.app.notes.integration.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TestIdStorage {

  private static final Map<String, Map<Long, Long>> ENTITY_ID_MAPS = new HashMap<>();
  private static final Map<String, String> STORED_IDS = new HashMap<>();

  private TestIdStorage() {
    // Utility class - prevent instantiation
  }

  public static void clear() {
    ENTITY_ID_MAPS.clear();
    STORED_IDS.clear();
  }

  public static void storeIdMapping(String entityType, Long expectedId, Long actualId) {
    ENTITY_ID_MAPS.computeIfAbsent(entityType, k -> new HashMap<>()).put(expectedId, actualId);
  }

  public static Optional<Long> getActualId(String entityType, Long expectedId) {
    return Optional.ofNullable(ENTITY_ID_MAPS.get(entityType)).map(map -> map.get(expectedId));
  }

  public static Map<Long, Long> getIdMappings(String entityType) {
    return Optional.ofNullable(ENTITY_ID_MAPS.get(entityType))
        .map(HashMap::new)
        .orElseGet(HashMap::new);
  }

  public static void setStoredId(String entityType, String id) {
    STORED_IDS.put(entityType, id);
  }

  public static Optional<String> getStoredId(String entityType) {
    return Optional.ofNullable(STORED_IDS.get(entityType));
  }

  public static void clearEntity(String entityType) {
    ENTITY_ID_MAPS.remove(entityType);
    STORED_IDS.remove(entityType);
  }
}
