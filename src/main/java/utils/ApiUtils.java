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
}
