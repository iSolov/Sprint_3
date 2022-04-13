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
 * Тест регистрации курьера.
 */
public class ScooterRegisterCourierTest {
    private final ScooterCourierApiClient api = new ScooterCourierApiClient();

    /**
     * Курьера можно создать (возвращается корректный статус и в теле получен результат).
     */
    @Test
    @DisplayName("Должна быть возможность создать курьера")
    public void shouldRegisterNewCourier(){
        Courier randomCourier = Courier.getRandomCourier();

        api
            .registerNewCourier(randomCourier)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED); // Успешное создание учетной записи

        api.clearCourierInfo(randomCourier);
    }

    /**
     * Нельзя создать двух одинаковых курьеров.
     */
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void shouldGetErrorWhenTwoEqualCouriersAreCreated(){
        Courier courier = Courier.getRandomCourier();

        boolean isCourierRegistered =
            api
                .registerNewCourier(courier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isCourierRegistered){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        api
            .registerNewCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // Запрос с повторяющимся логином

        api.clearCourierInfo(courier);
    }

    /**
     * Чтобы создать курьера, нужно передать в ручку все обязательные поля.
     */
    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void shouldCreateNewCreateWithOnlyNecessaryFields(){
        Courier courierWithoutFirstName = new Courier(Courier.getRandomLogin(), Courier.getRandomPassword());

        api
            .registerNewCourier(courierWithoutFirstName)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED);

        api.clearCourierInfo(courierWithoutFirstName);
    }

    /**
     * Если одного из полей нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("Должна быть ошибка, если при создании курьера не передан пароль")
    public void shouldGetErrorWhenRegisterNewCourierWithoutPassword(){
        Courier courierWithoutPassword = new Courier(Courier.getRandomLogin());

        api
            .registerNewCourier(courierWithoutPassword)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля
    }

    /**
     * Если создать пользователя с логином, который уже есть, возвращается ошибка.
     */
    @Test
    @DisplayName("Должна быть ошибка, если создается курьер с существующим именем")
    public void shouldGetErrorWhenTwoCouriersWithEqualLoginsAreCreated(){
        Courier firstCourier = Courier.getRandomCourier();

        boolean isFirstCourierRegistered =
            api
                .registerNewCourier(firstCourier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isFirstCourierRegistered){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Courier secondCourier = Courier.getRandomCourier();
        secondCourier.setLogin(firstCourier.getLogin());

        api
            .registerNewCourier(secondCourier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // Запрос с повторяющимся логином

        api.clearCourierInfo(firstCourier);
        api.clearCourierInfo(secondCourier);
    }
}
