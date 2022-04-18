import static org.hamcrest.Matchers.equalTo;

import client.ScooterCourierApiClient;
import io.qameta.allure.junit4.DisplayName;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тест регистрации курьера.
 */
public class ScooterRegisterCourierTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();

    /**
     * Курьера можно создать (возвращается корректный статус и в теле получен результат).
     */
    @Test
    @DisplayName("Должна быть возможность создать курьера")
    public void shouldRegisterNewCourier(){
        Courier randomCourier = Courier.getRandomCourier();

        apiCourier
            .registerNewCourier(randomCourier)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED); // Успешное создание учетной записи
    }

    /**
     * Нельзя создать двух одинаковых курьеров.
     */
    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void shouldGetErrorWhenTwoEqualCouriersAreCreated(){
        Courier courier = Courier.getRandomCourier();

        boolean isCourierRegistered =
            apiCourier
                .registerNewCourier(courier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isCourierRegistered){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        apiCourier
            .registerNewCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // Запрос с повторяющимся логином
    }

    /**
     * Чтобы создать курьера, нужно передать в ручку все обязательные поля.
     */
    @Test
    @DisplayName("Чтобы создать курьера, нужно передать в ручку все обязательные поля")
    public void shouldCreateNewCreateWithOnlyNecessaryFields(){
        Courier courierWithoutFirstName = new Courier(Courier.getRandomLogin(), Courier.getRandomPassword());

        apiCourier
            .registerNewCourier(courierWithoutFirstName)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED);
    }

    /**
     * Если одного из полей нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("Должна быть ошибка, если при создании курьера не передан пароль")
    public void shouldGetErrorWhenRegisterNewCourierWithoutPassword(){
        Courier courierWithoutPassword = new Courier(Courier.getRandomLogin());

        apiCourier
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
            apiCourier
                .registerNewCourier(firstCourier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isFirstCourierRegistered){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        Courier secondCourier = Courier.getRandomCourier();
        secondCourier.setLogin(firstCourier.getLogin());

        apiCourier
            .registerNewCourier(secondCourier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // Запрос с повторяющимся логином
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
