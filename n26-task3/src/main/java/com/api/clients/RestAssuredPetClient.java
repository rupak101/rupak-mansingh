package com.api.clients;

import com.api.model.PetStatusResponse;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import lombok.Builder;
import org.apache.http.HttpStatus;

import java.io.File;

import static com.api.RestAssuredHelper.statusMatcherFor;
import static com.api.framework.utils.getJsonSchemaFactory;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.BINARY;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class RestAssuredPetClient extends RestAssuredClient {

  private static final String PET_ID_PATH_PARAMETER = "petId";
  private static final String PET_STATUS_QUERY_PARAMETER = "status";

  private static final String PET_PATH = "/pet";
  private static final String PET_ID_PATH_PLACEHOLDER = "{" + PET_ID_PATH_PARAMETER + "}";
  private static final String PET_PATH_WITH_PARAMETER =
      PET_PATH + "/" + "{" + PET_ID_PATH_PARAMETER + "}";
  private static final String UPLOAD_IMAGE_PATH =
      PET_PATH + "/" + PET_ID_PATH_PLACEHOLDER + "/uploadImage";
  private static final String FIND_BY_PET_STATUS_PATH = PET_PATH + "/findByStatus";

  private static String payload =
      "{\n"
          + "  \"id\": 2,\n"
          + "  \"category\": {\n"
          + "    \"id\": 0,\n"
          + "    \"name\": \"test_animal\"\n"
          + "  },\n"
          + "  \"name\": \"doggie\",\n"
          + "  \"photoUrls\": [\n"
          + "    \"string\"\n"
          + "  ],\n"
          + "  \"tags\": [\n"
          + "    {\n"
          + "      \"id\": 0,\n"
          + "      \"name\": \"string\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"status\": \"available\"\n"
          + "}";

  private static final String GET_PET_ERROR_MESSAGE = "Get user pet error message";

  @Builder
  public RestAssuredPetClient(boolean loggingEnabled) {
    this.loggingEnabled = loggingEnabled;
  }

  // Get the user pet client without login
  public static RestAssuredPetClient getClientForPets() {
    return RestAssuredPetClient.builder().loggingEnabled(false).build();
  }

  @Step("Upload image of pets")
  public PetStatusResponse uploadImage(int petId) {
    return given()
        .spec(getBaseSpecWithoutType())
        .pathParam(PET_ID_PATH_PARAMETER, petId)
        .contentType(BINARY)
        .body(new File(System.getProperty("user.dir") + "/src/test/resources/image.png"))
        .when()
        .post(UPLOAD_IMAGE_PATH)
        .then()
        .assertThat()
        // validate response schema
        .body(matchesJsonSchemaInClasspath("petUpdateSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse.class);
  }

  @Step("Add new pets extracted  response")
  public PetStatusResponse addNewPet() {
    return given()
        .spec(getBaseSpec())
        .body(payload)
        .when()
        .post(PET_PATH)
        .then()
        .assertThat()
        // validate add ner pet schema
        .body(matchesJsonSchemaInClasspath("petDetailsSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse.class);
  }

  @Step("Update the existing pet details")
  public PetStatusResponse updateExistingPet() {
    return given()
        .spec(getBaseSpec())
        .body(payload)
        .when()
        .put(PET_PATH)
        .then()
        .assertThat()
        // validate update existing pet schema
        .body(matchesJsonSchemaInClasspath("petDetailsSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse.class);
  }

  @Step("Get the pet details by status")
  public PetStatusResponse[] getPetsByStatus(String petStatus) {
    return given()
        .spec(getBaseSpec())
        .queryParam(PET_STATUS_QUERY_PARAMETER, petStatus)
        .when()
        .get(FIND_BY_PET_STATUS_PATH)
        .then()
        .assertThat()
        // validate pet status schema
        .body(matchesJsonSchemaInClasspath("petStatusSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse[].class);
  }

  @Step("Get pet details by ID")
  public ValidatableResponse getPetById(int petId) {
    return given()
        .spec(getBaseSpec())
        .pathParam(PET_ID_PATH_PARAMETER, petId)
        .when()
        .get(PET_PATH_WITH_PARAMETER)
        .then();
  }

  @Step("Get pet details by ID as extracted response")
  public PetStatusResponse getPetByIdAsExtractedResponse(int petId) {
    return getPetById(petId)
        .assertThat()
        // validate pet details schema
        .body(matchesJsonSchemaInClasspath("petUpdateSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse.class);
  }

  @Step("Update the pet in store form data")
  public PetStatusResponse updatePetInStoreFormData(int petId) {
    return given()
        .spec(getBaseSpecWithoutType())
        .header("accept", "application/json")
        .queryParam("name", "dogs")
        .pathParam(PET_ID_PATH_PARAMETER, petId)
        .when()
        .post(PET_PATH_WITH_PARAMETER)
        .then()
        .assertThat()
        // validate pet response schema
        .body(matchesJsonSchemaInClasspath("petUpdateSchema.json").using(getJsonSchemaFactory()))
        .statusCode(statusMatcherFor(HttpStatus.SC_OK, GET_PET_ERROR_MESSAGE))
        .extract()
        .as(PetStatusResponse.class);
  }

  @Step("Delete the pets")
  public ValidatableResponse deletePets(int petId) {
    return given()
        .spec(getBaseSpec())
        .pathParam(PET_ID_PATH_PARAMETER, petId)
        .when()
        .delete(PET_PATH_WITH_PARAMETER)
        .then();
  }
}
