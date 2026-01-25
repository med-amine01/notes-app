package com.app.notes.service;

import com.app.notes.dto.NoteRequest;
import com.app.notes.dto.NoteResponse;
import java.util.List;

public interface NoteService {

  NoteResponse createNote(NoteRequest noteRequest);

  NoteResponse getNoteById(Long id);

  List<NoteResponse> getAllNotes();

  NoteResponse updateNote(Long id, NoteRequest noteRequest);

  void deleteNote(Long id);
}
