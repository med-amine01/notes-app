package com.app.notes.dto;

import java.time.LocalDateTime;

public record NoteResponse(Long id, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt) {}
