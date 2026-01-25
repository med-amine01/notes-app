package com.app.notes.mapper;

import com.app.notes.dto.NoteRequest;
import com.app.notes.dto.NoteResponse;
import com.app.notes.model.Note;

public interface NoteMapper {
  Note toEntity(NoteRequest noteRequest);

  NoteResponse toResponse(Note note);
}
