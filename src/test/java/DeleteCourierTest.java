import static org.hamcrest.Matchers.equalTo;

import client.BaseHttpClient;
import client.ScooterCourierApiClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тест удаления курьера.
 */
public class DeleteCourierTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    /**
     * Должна быть возможность удалить курьера.
     */
    @Test
    @DisplayName("Курьер должен удалиться")
    public void shouldDeleteCourierTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = apiCourier.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Response loginCourierResponse = apiCourier.loginCourier(randomCourier);

        if (loginCourierResponse.statusCode() != HttpStatus.SC_OK){
            Assert.fail("Не удалось авторизоваться курьеру.");
            return;
        }

        int id = loginCourierResponse.then().extract().body().path("id");

        apiCourier
            .deleteCourier(id)
            .then().assertThat().statusCode(HttpStatus.SC_OK)
            .and().assertThat().body("ok", equalTo(true));
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
