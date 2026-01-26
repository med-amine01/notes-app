package com.app.notes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
        @NotBlank(message = "Title is required")
                @Size(min = 1, max = 200, message = "Title must be between 1 and 200 characters")
                String title,
        @NotBlank(message = "Content is required")
                @Size(min = 1, max = 2000, message = "Content must be between 1 and 2000 characters")
                String content) {}
