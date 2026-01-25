Feature: Notes API CRUD Operations
  As a user
  I want to manage notes through REST API
  So that I can create, read, update, and delete notes

  Background:
    Given the API is available at "/api/v1/notes"

  Scenario: Create a new note successfully
    When I send a POST request to "/api/v1/notes" with body:
      """
      {
        "title": "My First Note",
        "content": "This is the content of my first note"
      }
      """
    Then the response status code should be 201
    And the response body should contain "id"
    And the response body should contain "My First Note"
    And the response body should contain "This is the content of my first note"
    And the response body should contain "createdAt"
    And the response body should contain "updatedAt"

  Scenario: Create a note with invalid data - missing title
    When I send a POST request to "/api/v1/notes" with body:
      """
      {
        "content": "This is the content"
      }
      """
    Then the response status code should be 400
    And the response body should contain "Validation failed"
    And the response body should contain "title"

  Scenario: Create a note with invalid data - missing content
    When I send a POST request to "/api/v1/notes" with body:
      """
      {
        "title": "My Note"
      }
      """
    Then the response status code should be 400
    And the response body should contain "Validation failed"
    And the response body should contain "content"

  Scenario: Create a note with invalid data - title too long
    When I send a POST request to "/api/v1/notes" with body:
      """
      {
        "title": "This is a very long title that exceeds the maximum allowed length of 200 characters and should cause a validation error when trying to create a note in the system. This string is definitely over 200 characters to test the validation limit properly and ensure the API correctly rejects titles that exceed the maximum allowed length of exactly 200 characters.",
        "content": "Valid content"
      }
      """
    Then the response status code should be 400
    And the response body should contain "Validation failed"

  Scenario: Create a note with invalid data - content too long
    When I send a POST request to "/api/v1/notes" with body:
      """
      {
        "title": "Valid title",
        "content": "This content exceeds the maximum allowed length of 2000 characters. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur? At vero eos et accusamus et iusto odio dignissimos ducimus qui blanditiis praesentium voluptatum deleniti atque corrupti quos dolores et quas molestias excepturi sint occaecati cupiditate non provident, similique sunt in culpa qui officia deserunt mollitia animi, id est laborum et dolorum fuga. Et harum quidem rerum facilis est et expedita distinctio. Nam libero tempore, cum soluta nobis est eligendi optio cumque nihil impedit quo minus id quod maxime placeat facere possimus, omnis voluptas assumenda est, omnis dolor repellendus. Temporibus autem quibusdam et aut officiis debitis aut rerum necessitatibus saepe eveniet ut et voluptates repudiandae sint et molestiae non recusandae. Itaque earum rerum hic tenetur a sapiente delectus, ut aut reiciendis voluptatibus maiores alias consequatur aut perferendis doloribus asperiores repellat. This string is definitely over 2000 characters to test the validation limit properly and ensure the API correctly rejects content that exceeds the maximum allowed length."
      }
      """
    Then the response status code should be 400
    And the response body should contain "Validation failed"

  Scenario: Get a note by ID successfully
    Given a note exists with id 1 and title "Test Note" and content "Test Content"
    When I send a GET request to "/api/v1/notes/1"
    Then the response status code should be 200
    And the response body should contain "id"
    And the response body should contain "Test Note"
    And the response body should contain "Test Content"

  Scenario: Get a note by non-existent ID
    When I send a GET request to "/api/v1/notes/999"
    Then the response status code should be 404
    And the response body should contain "Note not found"

  Scenario: Get all notes when no notes exist
    When I send a GET request to "/api/v1/notes"
    Then the response status code should be 200
    And the response body should be an empty array

  Scenario: Get all notes when notes exist
    Given a note exists with id 1 and title "Note 1" and content "Content 1"
    And a note exists with id 2 and title "Note 2" and content "Content 2"
    When I send a GET request to "/api/v1/notes"
    Then the response status code should be 200
    And the response body should be a non-empty array
    And the response body should contain "Note 1"
    And the response body should contain "Note 2"

  Scenario: Update a note successfully
    Given a note exists with id 1 and title "Original Title" and content "Original Content"
    When I send a PUT request to "/api/v1/notes/1" with body:
      """
      {
        "title": "Updated Title",
        "content": "Updated Content"
      }
      """
    Then the response status code should be 200
    And the response body should contain "Updated Title"
    And the response body should contain "Updated Content"
    And the response body should contain "id"
    And the response body should contain "updatedAt"

  Scenario: Update a note with invalid data
    Given a note exists with id 1 and title "Original Title" and content "Original Content"
    When I send a PUT request to "/api/v1/notes/1" with body:
      """
      {
        "title": "",
        "content": "Valid content"
      }
      """
    Then the response status code should be 400
    And the response body should contain "Validation failed"

  Scenario: Update a non-existent note
    When I send a PUT request to "/api/v1/notes/999" with body:
      """
      {
        "title": "Updated Title",
        "content": "Updated Content"
      }
      """
    Then the response status code should be 404
    And the response body should contain "Note not found"

  Scenario: Delete a note successfully
    Given a note exists with id 1 and title "Note to Delete" and content "Content to Delete"
    When I send a DELETE request to "/api/v1/notes/1"
    Then the response status code should be 204
    When I send a GET request to "/api/v1/notes/1"
    Then the response status code should be 404

  Scenario: Delete a non-existent note
    When I send a DELETE request to "/api/v1/notes/999"
    Then the response status code should be 404
    And the response body should contain "Note not found"

  Scenario: Complete CRUD workflow
    Given I create a note with title "Workflow Note" and content "Workflow Content"
    And I store the created note ID
    When I send a GET request to "/api/v1/notes/{storedId}"
    Then the response status code should be 200
    And the response body should contain "Workflow Note"
    When I send a PUT request to "/api/v1/notes/{storedId}" with body:
      """
      {
        "title": "Updated Workflow Note",
        "content": "Updated Workflow Content"
      }
      """
    Then the response status code should be 200
    And the response body should contain "Updated Workflow Note"
    When I send a DELETE request to "/api/v1/notes/{storedId}"
    Then the response status code should be 204
    When I send a GET request to "/api/v1/notes/{storedId}"
    Then the response status code should be 404

