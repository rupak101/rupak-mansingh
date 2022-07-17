package com.n26.apiTest;

import com.api.model.PetStatusResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Story;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.api.clients.RestAssuredPetClient.getClientForPets;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@Owner("Rupak Mansingh")
@Feature("Everything about your pets")
public class PetsAPITest {

  private static final int INVALID_PET_ID = 44444444;

  @DataProvider
  public Object[] petsIDInputData() {
    return new Object[] {2};
  }

  @DataProvider
  public Object[] petsStatusInputData() {
    return new Object[] {"available", "pending", "sold"};
  }

  @Story("Add new pet into store (POST /pet)")
  @Description("Add new pet into store")
  @Test
  public void addNewPetToStore() {
    PetStatusResponse petStatusResponse = getClientForPets().addNewPet();
    VerifyPetDetails(petStatusResponse);
  }

  @Story("Update the existing pet details (PUT /pet)")
  @Description("Update the existing pet details")
  @Test(dependsOnMethods = "addNewPetToStore")
  public void updatePetToStore() {
    // Update the existing pet details
    PetStatusResponse petStatusResponse = getClientForPets().updateExistingPet();
    VerifyPetDetails(petStatusResponse);
  }

  @Story("Update the pets image(POST /pet/{petId}/uploadImage)")
  @Description("Update the pets image by uploading the image")
  @Test(dataProvider = "petsIDInputData", dependsOnMethods = "addNewPetToStore")
  public void updatePetImage(int petId) {
    // Update the image
    PetStatusResponse petStatusResponse = getClientForPets().uploadImage(petId);
    verifyPetResponse(petStatusResponse);
  }

  @Story("Get the pet details by status(GET /pet/findByStatus)")
  @Description("Get the pet details by status")
  @Test(dataProvider = "petsStatusInputData")
  public void getPetDetailsByStatus(String petStatus) {
    PetStatusResponse[] petsByStatus = getClientForPets().getPetsByStatus(petStatus);

    // validate first pet details response from list
    assertThat("Response Pet ID is null", petsByStatus[0].id, is(notNullValue()));
    assertThat("Response category is null", petsByStatus[0].getCategory().id, is(notNullValue()));
    assertThat("Response tag is null", petsByStatus[0].getTags().get(0).id, is(notNullValue()));
  }

  @Story("Get the pet details by pet ID(GET /pet/{petId})")
  @Description("Get the pet details by Pet ID")
  @Test(dataProvider = "petsIDInputData", dependsOnMethods = "addNewPetToStore")
  public void getPetDetailsById(int petId) {
    PetStatusResponse petStatusResponse = getClientForPets().getPetByIdAsExtractedResponse(petId);

    // Verify the pet details by pet ID
    assertThat(
        "Response code didn't match with the response", petStatusResponse.getId(), is(petId));
    assertThat("Response type is null", petStatusResponse.getCategory().id, is(0));
    assertThat("Response message is null", petStatusResponse.getTags().get(0).id, is(0));
  }

  @Story("Get the pet details by invalid pet ID(GET /pet/{petId})")
  @Description("Get the pet details by invalid pet ID")
  @Test
  public void getPetDetailsByInvalidPetId() {
    getClientForPets().getPetById(INVALID_PET_ID).assertThat().statusCode(HttpStatus.SC_NOT_FOUND);
  }

  @Story("Update the pet in the store with form data(POST /pet/{petId})")
  @Description("Update the pet in the store with form data")
  @Test(dataProvider = "petsIDInputData", dependsOnMethods = "addNewPetToStore")
  public void updatePetInStoreWithFormData(int petId) {
    PetStatusResponse petStatusResponse = getClientForPets().updatePetInStoreFormData(petId);
    verifyPetResponse(petStatusResponse);
  }

  @Story("Delete the pet (DELETE /pet/{petId})")
  @Description("Deletes the pet from the store")
  @Test(
      dataProvider = "petsIDInputData",
      dependsOnMethods = "updatePetInStoreWithFormData",
      priority = 5)
  public void deletePet(int petId) {
    // Delete the pet
    getClientForPets().deletePets(petId).assertThat().statusCode(HttpStatus.SC_OK);
  }

  private void VerifyPetDetails(PetStatusResponse petStatusResponse) {
    assertThat("Pet ID didn't match with the response", petStatusResponse.id, is(2));
    assertThat(
        "Pet category didn't match with the response", petStatusResponse.getCategory().id, is(0));
    assertThat(
        "Pet tag didn't match with the response", petStatusResponse.getTags().get(0).id, is(0));
  }

  private void verifyPetResponse(PetStatusResponse petStatusResponse) {
    assertThat(
        "Response code didn't match with the response",
        petStatusResponse.category,
        is(notNullValue()));
    assertThat("Response type is null", petStatusResponse.getId(), is(2));
    assertThat("Response message is null", petStatusResponse.photoUrls, is(notNullValue()));
  }
}
