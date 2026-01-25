package com.app.notes.mapper.impl;

import com.app.notes.dto.NoteRequest;
import com.app.notes.dto.NoteResponse;
import com.app.notes.mapper.NoteMapper;
import com.app.notes.model.Note;
import org.springframework.stereotype.Component;

@Component
public class NoteMapperImpl implements NoteMapper {

  @Override
  public Note toEntity(NoteRequest noteRequest) {
    return Note.builder().title(noteRequest.title()).content(noteRequest.content()).build();
  }

  @Override
  public NoteResponse toResponse(Note note) {
    return new NoteResponse(
        note.getId(), note.getTitle(), note.getContent(), note.getCreatedAt(), note.getUpdatedAt());
  }
}
