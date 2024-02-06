package ariefsyaifu.controller;

import java.io.File;
import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.model.Voucher;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.transaction.Transactional;

@QuarkusTest
class VoucherController_Import_Test {

    @Transactional
    @BeforeEach
    void beforeEach() {
    }

    @Transactional
    @AfterEach
    void afterEach() {
        Voucher.deleteAll();
    }

    @Test
    void testImportCsv_InsertOne() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("file", new File("./src/test/resources/voucher-template-insert-one.csv"))
                .multiPart("delimiter", ",")
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(200, r.getStatusCode());

        JsonArray ja = new JsonArray(r.asString());
        Assertions.assertEquals(0, ja.size());

        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals("name0", v.name);
        Assertions.assertEquals(BigDecimal.valueOf(1000).setScale(2), v.amount);
        Assertions.assertEquals("prefixCode0", v.prefixCode);
    }

    @Test
    void testImportCsv_InsertMany() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("file", new File("./src/test/resources/voucher-template-insert-many.csv"))
                .multiPart("delimiter", ",")
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(200, r.getStatusCode());

        JsonArray ja = new JsonArray(r.asString());
        Assertions.assertEquals(0, ja.size());

        Assertions.assertEquals(10, Voucher.count());
    }

    @Test
    void testImportCsv_InsertOne_WithoutSendingDelimiter() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("file", new File("./src/test/resources/voucher-template-insert-one.csv"))
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(400, r.getStatusCode());

        JsonObject jo = new JsonObject(r.asString());
        Assertions.assertEquals("delimiter required", jo.getString("message"));
    }

    @Test
    void testImportCsv_InsertOne_WithoutSendingFile() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("delimiter", ",")
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(400, r.getStatusCode());

        JsonObject jo = new JsonObject(r.asString());
        Assertions.assertEquals("file required", jo.getString("message"));
    }

    @Test
    void testImportCsv_InsertOne_CustomDelimiter() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("file", new File("./src/test/resources/voucher-template-insert-one-delimiter-is-;.csv"))
                .multiPart("delimiter", ";")
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(200, r.getStatusCode());

        JsonArray ja = new JsonArray(r.asString());
        Assertions.assertEquals(0, ja.size());

        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals("name0", v.name);
        Assertions.assertEquals(BigDecimal.valueOf(1000).setScale(2), v.amount);
        Assertions.assertEquals("prefixCode0", v.prefixCode);
    }

    @Test
    void testImportCsv_InsertOneWithDuplicate() {
        Assertions.assertEquals(0, Voucher.count());

        Response r = RestAssured.given()
                .multiPart("file", new File("./src/test/resources/voucher-template-insert-one-with-duplicate.csv"))
                .multiPart("delimiter", ",")
                .when()
                .post("/api/v1/voucher/import")
                .andReturn();
        Assertions.assertEquals(200, r.getStatusCode());

        JsonArray ja = new JsonArray(r.asString());
        Assertions.assertEquals(1, ja.size());
        Assertions.assertEquals("prefixCode0",
                ja.getJsonObject(0).getString("prefixCode"));
        Assertions.assertEquals("prefixCode already exists",
                ja.getJsonObject(0).getString("reason"));

        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();
        Assertions.assertEquals("name0", v.name);
        Assertions.assertEquals(BigDecimal.valueOf(1000).setScale(2), v.amount);
        Assertions.assertEquals("prefixCode0", v.prefixCode);
    }
}
