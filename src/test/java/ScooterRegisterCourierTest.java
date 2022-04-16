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
 * “ест регистрации курьера.
 */
public class ScooterRegisterCourierTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();

    /**
     *  урьера можно создать (возвращаетс€ корректный статус и в теле получен результат).
     */
    @Test
    @DisplayName("ƒолжна быть возможность создать курьера")
    public void shouldRegisterNewCourier(){
        Courier randomCourier = Courier.getRandomCourier();

        apiCourier
            .registerNewCourier(randomCourier)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED); // ”спешное создание учетной записи
    }

    /**
     * Ќельз€ создать двух одинаковых курьеров.
     */
    @Test
    @DisplayName("Ќельз€ создать двух одинаковых курьеров")
    public void shouldGetErrorWhenTwoEqualCouriersAreCreated(){
        Courier courier = Courier.getRandomCourier();

        boolean isCourierRegistered =
            apiCourier
                .registerNewCourier(courier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isCourierRegistered){
            Assert.fail("Ќе удалось создать курьера дл€ проверки.");
            return;
        }

        apiCourier
            .registerNewCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // «апрос с повтор€ющимс€ логином
    }

    /**
     * „тобы создать курьера, нужно передать в ручку все об€зательные пол€.
     */
    @Test
    @DisplayName("„тобы создать курьера, нужно передать в ручку все об€зательные пол€")
    public void shouldCreateNewCreateWithOnlyNecessaryFields(){
        Courier courierWithoutFirstName = new Courier(Courier.getRandomLogin(), Courier.getRandomPassword());

        apiCourier
            .registerNewCourier(courierWithoutFirstName)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED);
    }

    /**
     * ≈сли одного из полей нет, запрос возвращает ошибку.
     */
    @Test
    @DisplayName("ƒолжна быть ошибка, если при создании курьера не передан пароль")
    public void shouldGetErrorWhenRegisterNewCourierWithoutPassword(){
        Courier courierWithoutPassword = new Courier(Courier.getRandomLogin());

        apiCourier
            .registerNewCourier(courierWithoutPassword)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // «апрос без логина или парол€
    }

    /**
     * ≈сли создать пользовател€ с логином, который уже есть, возвращаетс€ ошибка.
     */
    @Test
    @DisplayName("ƒолжна быть ошибка, если создаетс€ курьер с существующим именем")
    public void shouldGetErrorWhenTwoCouriersWithEqualLoginsAreCreated(){
        Courier firstCourier = Courier.getRandomCourier();

        boolean isFirstCourierRegistered =
            apiCourier
                .registerNewCourier(firstCourier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isFirstCourierRegistered){
            Assert.fail("Ќе удалось создать курьера дл€ проверки.");
            return;
        }

        Courier secondCourier = Courier.getRandomCourier();
        secondCourier.setLogin(firstCourier.getLogin());

        apiCourier
            .registerNewCourier(secondCourier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // «апрос с повтор€ющимс€ логином
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
