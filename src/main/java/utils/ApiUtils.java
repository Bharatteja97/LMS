package utils;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class ApiUtils {

    public static String getLoanAgreement(int applicationId, String token) {

        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .queryParam("applicationId", applicationId)
                .get("https://vehicle-product.alfinnext.com/api/los-vehicle/operation/loan-agreement");

        return response.getBody().asString();
    }
    
    public static String getMandateDetails(int applicationId, String token) {
        Response response = RestAssured
                .given()
                .header("Authorization", "Bearer " + token)
                .header("Accept", "application/json")
                .get("https://vehicle-product.alfinnext.com/api/los-vehicle/operation/mandate-details/" + applicationId);

        return response.getBody().asString();
    }

    public static Response postMandateCallback(String token, String requestBody) {
        return RestAssured
                .given()
                .baseUri("https://vehicle-product.alfinnext.com")
                .contentType(io.restassured.http.ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(requestBody)
                .post("/api/los-vehicle/operation/mandate-callback");
    }
}
