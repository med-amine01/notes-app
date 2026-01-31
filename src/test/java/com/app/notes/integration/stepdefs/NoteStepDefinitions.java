package com.app.notes.integration.stepdefs;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.app.notes.dto.NoteRequest;
import com.app.notes.model.Note;
import com.app.notes.repository.NoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

public class NoteStepDefinitions {

  @Autowired private MockMvc mockMvc;

  @Autowired private NoteRepository noteRepository;

  @Autowired private ObjectMapper objectMapper;

  private MvcResult mvcResult;
  private String storedId;

  @Before
  public void setUp() {
    noteRepository.deleteAll();
  }

  @Given("the API is available at {string}")
  public void theApiIsAvailableAt(String path) {
    assertNotNull(mockMvc);
  }

  @Given("a note exists with id {long} and title {string} and content {string}")
  public void aNoteExistsWithIdAndTitleAndContent(Long id, String title, String content) {
    Note note =
        Note.builder()
            .id(id)
            .title(title)
            .content(content)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    noteRepository.save(note);
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
    storedId = String.valueOf(response.get("id"));
  }

  @Given("I store the created note ID")
  public void iStoreTheCreatedNoteId() {
    assertNotNull(storedId, "Note ID should be stored from previous step");
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
    String actualPath = path.replace("{storedId}", storedId != null ? storedId : "");
    mvcResult = mockMvc.perform(get(actualPath)).andReturn();
  }

  @When("I send a PUT request to {string} with body:")
  public void iSendAPutRequestToWithBody(String path, String body) throws Exception {
    String actualPath = path.replace("{storedId}", storedId != null ? storedId : "");
    mvcResult =
        mockMvc
            .perform(put(actualPath).contentType(MediaType.APPLICATION_JSON).content(body))
            .andReturn();
  }

  @When("I send a DELETE request to {string}")
  public void iSendADeleteRequestTo(String path) throws Exception {
    String actualPath = path.replace("{storedId}", storedId != null ? storedId : "");
    mvcResult = mockMvc.perform(delete(actualPath)).andReturn();
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
