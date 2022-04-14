package client;

import static io.restassured.RestAssured.*;

import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;

/**
 * Клиент для обращения к API курьера.
 */
public class ScooterCourierApiClient extends BaseHttpClient {

    /**
     * Создание нового курьера.
     * @param courier Новый курьер.
     * @apiNote 201 Успешное создание учетной записи; 400 Запрос без логина или пароля; 409 Запрос с повторяющимся логином.
     */
    public Response registerNewCourier(Courier courier){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .body(Courier.toJson(courier))
            .post("/api/v1/courier");
    }

    /**
     * Авторизация курьера.
     * @param courier Курьер.
     * @apiNote 200 Успешный логин; 400 Запрос без логина или пароля; 404 Запрос c несуществующей парой логин-пароль.
     */
    public Response loginCourier(Courier courier){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .body(Courier.toJson(courier))
            .post("/api/v1/courier/login");
    }

    /**
     * Удаление курьера.
     * @param id Идентификатор курьера.
     * @apiNote 200 Успешное удаление курьера; 400 Запрос без id; 404 Запрос c несуществующим id.
     */
    public Response deleteCourier(int id){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .delete("/api/v1/courier/" + id);
    }

    /**
     * Очистить информацию о курьере.
     * @param courier Курьер.
     */
    public void clearCourierInfo(Courier courier){
        Response loginResponse = loginCourier(courier);
        if (loginResponse.statusCode() == HttpStatus.SC_OK){ // Успешный логин
            int id = loginResponse.then().extract().body().path("id");

            deleteCourier(id);
        }
    }
}
