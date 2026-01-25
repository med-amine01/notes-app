package com.app.notes.integration.stepdefs;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.notes.dto.NoteRequest;
import com.app.notes.integration.util.PathReplacer;
import com.app.notes.integration.util.TestIdStorage;
import com.app.notes.model.Note;
import com.app.notes.repository.NoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class NoteStepDefinitions {

  private static final String NOTES_ENTITY_TYPE = "notes";

  @Autowired private MockMvc mockMvc;

  @Autowired private NoteRepository noteRepository;

  @Autowired private ObjectMapper objectMapper;

  private MvcResult mvcResult;

  @Before
  public void setUp() {
    noteRepository.deleteAll();
    TestIdStorage.clear();
  }

  @Given("the API is available at {string}")
  public void theApiIsAvailableAt(String path) {
    assertNotNull(mockMvc);
  }

  @Given("a note exists with id {long} and title {string} and content {string}")
  public void aNoteExistsWithIdAndTitleAndContent(Long id, String title, String content) {
    Note note = Note.builder().title(title).content(content).build();
    Note savedNote = noteRepository.saveAndFlush(note);
    // Store mapping from expected ID to actual generated ID
    TestIdStorage.storeIdMapping(NOTES_ENTITY_TYPE, id, savedNote.getId());
  }

  @Given("I create a note with title {string} and content {string}")
  public void iCreateANoteWithTitleAndContent(String title, String content) throws Exception {
    NoteRequest request = new NoteRequest(title, content);
    String requestBody = objectMapper.writeValueAsString(request);

    mvcResult =
        mockMvc
            .perform(
                post("/api/v1/notes").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isCreated())
            .andReturn();

    String responseBody = mvcResult.getResponse().getContentAsString();
    Map<String, Object> response = objectMapper.readValue(responseBody, Map.class);
    TestIdStorage.setStoredId(NOTES_ENTITY_TYPE, String.valueOf(response.get("id")));
  }

  @Given("I store the created note ID")
  public void iStoreTheCreatedNoteId() {
    Optional<String> storedId = TestIdStorage.getStoredId(NOTES_ENTITY_TYPE);
    assertTrue(storedId.isPresent(), "Note ID should be stored from previous step");
  }

  @When("I send a POST request to {string} with body:")
  public void iSendAPostRequestToWithBody(String path, String body) throws Exception {
    mvcResult =
        mockMvc
            .perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
            .andReturn();
  }

  @When("I send a GET request to {string}")
  public void iSendAGetRequestTo(String path) throws Exception {
    String actualPath = replacePathWithStoredIds(path);
    mvcResult = mockMvc.perform(get(actualPath)).andReturn();
  }

  @When("I send a PUT request to {string} with body:")
  public void iSendAPutRequestToWithBody(String path, String body) throws Exception {
    String actualPath = replacePathWithStoredIds(path);
    mvcResult =
        mockMvc
            .perform(put(actualPath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andReturn();
  }

  @When("I send a DELETE request to {string}")
  public void iSendADeleteRequestTo(String path) throws Exception {
    String actualPath = replacePathWithStoredIds(path);
    mvcResult = mockMvc.perform(delete(actualPath)).andReturn();
  }

  private String replacePathWithStoredIds(String path) {
    Map<Long, Long> idMappings = TestIdStorage.getIdMappings(NOTES_ENTITY_TYPE);
    Optional<String> storedId = TestIdStorage.getStoredId(NOTES_ENTITY_TYPE);
    return PathReplacer.replacePath(path, NOTES_ENTITY_TYPE, idMappings, storedId);
  }

  @Then("the response status code should be {int}")
  public void theResponseStatusCodeShouldBe(int expectedStatus) {
    assertEquals(expectedStatus, mvcResult.getResponse().getStatus());
  }

  @Then("the response body should contain {string}")
  public void theResponseBodyShouldContain(String expectedText) throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    assertTrue(
        responseBody.contains(expectedText),
        "Response body should contain: " + expectedText + " but was: " + responseBody);
  }

  @Then("the response body should be an empty array")
  public void theResponseBodyShouldBeAnEmptyArray() throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    assertEquals("[]", responseBody.trim());
  }

  @Then("the response body should be a non-empty array")
  public void theResponseBodyShouldBeANonEmptyArray() throws Exception {
    String responseBody = mvcResult.getResponse().getContentAsString();
    assertTrue(responseBody.startsWith("[") && responseBody.endsWith("]"));
    assertTrue(responseBody.length() > 2, "Array should not be empty");
  }
}
