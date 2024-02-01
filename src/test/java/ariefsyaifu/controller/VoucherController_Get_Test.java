package ariefsyaifu.controller;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ariefsyaifu.model.Voucher;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.vertx.core.json.JsonArray;
import jakarta.transaction.Transactional;
import net.bytebuddy.utility.RandomString;

@QuarkusTest
class VoucherController_Get_Test {

    @Transactional
    @BeforeEach
    void beforeEach() {
        Voucher v = new Voucher();
        v.name = RandomString.make();
        v.amount = BigDecimal.valueOf(10_000);
        v.prefixCode = RandomString.make();
        v.persist();
    }

    @Transactional
    @AfterEach
    void afterEach() {
        Voucher.deleteAll();
    }

    @Test
    void testGet() {
        Assertions.assertEquals(1, Voucher.count());
        Voucher v = Voucher.findAll().firstResult();

        Response r = RestAssured.given()
                .when()
                .get("/v1/voucher")
                .andReturn();
        Assertions.assertEquals(200, r.getStatusCode());

        JsonArray ja = new JsonArray(r.asString());
        Assertions.assertEquals(1, ja.size());
        Assertions.assertEquals(v.name, ja.getJsonObject(0).getString("name"));
        Assertions.assertEquals(v.amount, BigDecimal.valueOf(ja.getJsonObject(0).getDouble("amount")).setScale(2));
        Assertions.assertEquals(v.prefixCode, ja.getJsonObject(0).getString("prefixCode"));
    }

}
