package com.claudioav;


import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

class ExampleResourceTest {
    void testHelloEndpoint() {
        given()
                .when().get("/hello")
                .then()
                .statusCode(200)
                .body(is("Hello from RESTEasy Reactive"));
    }

}