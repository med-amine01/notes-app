package com.app.notes.controller;

import com.app.notes.dto.NoteRequest;
import com.app.notes.dto.NoteResponse;
import com.app.notes.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
@Tag(name = "Notes", description = "Notes management API")
public class NoteController {

  private final NoteService noteService;

  @PostMapping
  @Operation(
      summary = "Create a new note",
      description = "Creates a new note with the provided title and content")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Note created successfully",
            content = @Content(schema = @Schema(implementation = NoteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
      })
  public ResponseEntity<NoteResponse> createNote(
      @Parameter(description = "Note request containing title and content", required = true)
          @Valid
          @RequestBody
          NoteRequest noteRequest) {
    NoteResponse noteResponse = noteService.createNote(noteRequest);
    return new ResponseEntity<>(noteResponse, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get note by ID", description = "Retrieves a note by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Note found",
            content = @Content(schema = @Schema(implementation = NoteResponse.class))),
        @ApiResponse(responseCode = "404", description = "Note not found")
      })
  public ResponseEntity<NoteResponse> getNoteById(
      @Parameter(description = "Note ID", required = true) @PathVariable Long id) {
    NoteResponse noteResponse = noteService.getNoteById(id);
    return ResponseEntity.ok(noteResponse);
  }

  @GetMapping
  @Operation(summary = "Get all notes", description = "Retrieves all notes from the system")
  @ApiResponse(
      responseCode = "200",
      description = "List of all notes",
      content = @Content(schema = @Schema(implementation = NoteResponse.class)))
  public ResponseEntity<List<NoteResponse>> getAllNotes() {
    List<NoteResponse> notes = noteService.getAllNotes();
    return ResponseEntity.ok(notes);
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update a note",
      description = "Updates an existing note with new title and content")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Note updated successfully",
            content = @Content(schema = @Schema(implementation = NoteResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Note not found")
      })
  public ResponseEntity<NoteResponse> updateNote(
      @Parameter(description = "Note ID", required = true) @PathVariable Long id,
      @Parameter(description = "Updated note request", required = true) @Valid @RequestBody
          NoteRequest noteRequest) {
    NoteResponse noteResponse = noteService.updateNote(id, noteRequest);
    return ResponseEntity.ok(noteResponse);
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a note", description = "Deletes a note by its unique identifier")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Note deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Note not found")
      })
  public ResponseEntity<Void> deleteNote(
      @Parameter(description = "Note ID", required = true) @PathVariable Long id) {
    noteService.deleteNote(id);
    return ResponseEntity.noContent().build();
  }
}
