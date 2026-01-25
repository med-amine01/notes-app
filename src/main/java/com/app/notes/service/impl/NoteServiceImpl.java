package com.app.notes.service.impl;

import com.app.notes.dto.NoteRequest;
import com.app.notes.dto.NoteResponse;
import com.app.notes.exception.ResourceNotFoundException;
import com.app.notes.mapper.NoteMapper;
import com.app.notes.model.Note;
import com.app.notes.repository.NoteRepository;
import com.app.notes.service.NoteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

  private final NoteRepository noteRepository;
  private final NoteMapper noteMapper;

  @Override
  @Transactional
  public NoteResponse createNote(NoteRequest noteRequest) {
    Note note = noteMapper.toEntity(noteRequest);
    Note savedNote = noteRepository.save(note);
    return noteMapper.toResponse(savedNote);
  }

  @Override
  @Transactional(readOnly = true)
  public NoteResponse getNoteById(Long id) {
    Note note =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
    return noteMapper.toResponse(note);
  }

  @Override
  @Transactional(readOnly = true)
  public List<NoteResponse> getAllNotes() {
    return noteRepository.findAll().stream().map(noteMapper::toResponse).toList();
  }

  @Override
  @Transactional
  public NoteResponse updateNote(Long id, NoteRequest noteRequest) {
    Note note =
        noteRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
    note.setTitle(noteRequest.title());
    note.setContent(noteRequest.content());
    Note updatedNote = noteRepository.save(note);
    return noteMapper.toResponse(updatedNote);
  }

  @Override
  @Transactional
  public void deleteNote(Long id) {
    if (!noteRepository.existsById(id)) {
      throw new ResourceNotFoundException("Note not found with id: " + id);
    }
    noteRepository.deleteById(id);
  }
}
