package com.api.clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;

import static com.api.RestAssuredConstants.BASIC_REQUEST_SPECIFICATION;
import static io.restassured.http.ContentType.JSON;

public class RestAssuredClient {

    protected static String baseUrl = "http://localhost:8080/api/v3/";
    protected static boolean loggingEnabled = false;

    /**
     * @return RequestSpecification containing
     * base URL,
     * JSON content type,
     * default configs(logging, objectMapping)
     * and other inside
     */
    protected static RequestSpecification getBaseSpec() {
        return new RequestSpecBuilder()
                .addRequestSpecification(BASIC_REQUEST_SPECIFICATION)
                .setAccept(JSON)
                .setContentType(JSON)
                .setBaseUri(baseUrl)
                .build();
    }

    /**
     * @return RequestSpecification containing
     * base URL,
     * default configs(logging, objectMapping)
     * and other inside
     */
    protected static RequestSpecification getBaseSpecWithoutType() {
        return new RequestSpecBuilder()
                .addRequestSpecification(BASIC_REQUEST_SPECIFICATION)
                .setConfig(RestAssuredConfig.newConfig().encoderConfig(EncoderConfig.encoderConfig()
                        .appendDefaultContentCharsetToContentTypeIfUndefined(false)))
                .setBaseUri(baseUrl)
                .build();
    }
}
