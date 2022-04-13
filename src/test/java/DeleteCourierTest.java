import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

import client.ScooterCourierApiClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Test;

/**
 * Тест удаления курьера.
 */
public class DeleteCourierTest {
    private final ScooterCourierApiClient api = new ScooterCourierApiClient();

    /**
     * Должна быть возможность удалить курьера.
     */
    @Test
    @DisplayName("Курьер должен удалиться")
    public void shouldDeleteCourier(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = api.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Response loginCourierResponse = api.loginCourier(randomCourier);

        if (loginCourierResponse.statusCode() != HttpStatus.SC_OK){
            Assert.fail("Не удалось авторизоваться курьеру.");
            return;
        }

        int id = loginCourierResponse.then().extract().body().path("id");

        api
            .deleteCourier(id)
            .then().assertThat().statusCode(HttpStatus.SC_OK)
            .and().assertThat().body("ok", equalTo(true));
    }
}
