import static org.hamcrest.Matchers.greaterThan;

import client.BaseHttpClient;
import client.ScooterCourierApiClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тест авторизации курьера.
 */
public class LoginCourierTest {
    private final ScooterCourierApiClient api = new ScooterCourierApiClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    /**
     * Курьер может авторизоваться.
     */
    @Test
    @DisplayName("Курьер должен успешно авторизоваться")
    public void shouldNewCourierSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = api.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() == HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            api
                .loginCourier(randomCourier)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().body("id", greaterThan(0)); // Успешный логин

            api.clearCourierInfo(randomCourier);
        }
        else{
            Assert.fail("Не удалось создать курьера для проверки.");
        }
    }

    /**
     * Для авторизации нужно передать все обязательные поля.
     */
    @Test
    @DisplayName("Курьер должен успешно авторизоваться только со всеми обязательными полями")
    public void shouldLoginCourierWithAllNecessaryFieldsTest(){
        Courier courier = Courier.getRandomCourier();

        courier.setFirstName(null);

        Response registerResponse = api.registerNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        api
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_OK)
            .and().body("id", greaterThan(0)); // Успешный логин

        api.clearCourierInfo(courier);
    }

    /**
     * Система вернёт ошибку, если неправильно указать логин или пароль.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если логин не передан")
    public void shouldGetErrorWhenLoginIsMissedTest(){
        Courier courier = Courier.getRandomCourier();

        if (api.registerNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        courier.setLogin(null);

        api
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля

        api.clearCourierInfo(courier);
    }

    /**
     * Если какого-то поля нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если пароль не передан")
    public void shouldGetErrorWhenPasswordIsMissingTest(){
        Courier courier = Courier.getRandomCourier();

        if (api.registerNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        courier.setPassword("");

        api
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // Запрос без логина или пароля

        api.clearCourierInfo(courier);
    }

    /**
     * Если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;.
     */
    @Test
    @DisplayName("Должна быть ошибка авторизации, если вход под несуществующим логином")
    public void shouldGetErrorWhenLoginWithNotExistedLoginTest(){
        Courier courier = Courier.getRandomCourier();

        // Создаем курьера. Авторизуемся под ним. Удаляем его. Пытаемся авторизоваться еще раз.

        Response registerResponse = api.registerNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Response loginResponse = api.loginCourier(courier);
        if (loginResponse.statusCode() != HttpStatus.SC_OK){ // Успешный логин
            api.clearCourierInfo(courier);

            String message = loginResponse.then().extract().body().path("message");
            Assert.fail("Не удалось залогиниться курьеру (login:" + courier.getLogin() + "): " + message);

            return;
        }

        int id = loginResponse.then().extract().body().path("id");

        Response deleteResponse = api.deleteCourier(id);
        if (deleteResponse.statusCode() != HttpStatus.SC_OK){ // Успешное удаление курьера
            String message = deleteResponse.then().extract().body().path("message");
            Assert.fail("Не удалось удалить курьера для провнерки (login:" + courier.getLogin() + "/id:" + id + "): " + message);

            return;
        }

        api
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND); // Запрос c несуществующей парой логин-пароль
    }

    /**
     * Успешный запрос возвращает id.
     */
    @Test
    @DisplayName("Успешная авторизация возвращает идентификатор курьера")
    public void shouldGetCourierIdWhenSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = api.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // Успешное создание учетной записи
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        api
            .loginCourier(randomCourier)
            .then().assertThat().body("id", greaterThan(0))
            .and().statusCode(HttpStatus.SC_OK);

        api.clearCourierInfo(randomCourier);
    }
}
