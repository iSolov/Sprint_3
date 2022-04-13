package client;

import static io.restassured.RestAssured.given;

import io.restassured.response.Response;
import model.Order;

public class ScooterOrderApiClient extends BaseHttpClient {

    /**
     * URL API заказов.
     */
    private final String ORDERS_API = API_HOST + "/orders";

    /**
     * Создание заказа.
     * @param order Заказ.
     * @apiNote 201 Успешное создание заказа.
     */
    public Response makeOrder(Order order){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .and()
            .body(order.toJson())
            .when()
            .post(ORDERS_API);
    }

    /**
     * Принять заказ.
     * @param orderId Идентификатор заказа.
     * @param courierId Идентификатор курьера.
     * @apiNote 200 Успешный ответ; 400 Запрос без номера; 404 Запрос c несуществующим номером;
     * 404 Запрос c несуществующим номер курьера; 409 Заказ уже был в работе;
     * 400 Нет id курьера или id заказа.
     */
    public Response acceptOrder(int orderId, int courierId){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .queryParam("courierId", courierId)
            .when()
            .put(ORDERS_API + "/accept/" +  orderId);
    }

    /**
     * Завершить заказ.
     * @param orderId Идентификатор заказа.
     * @apiNote 200 Успешный ответ; 400 Запрос без номера; 404 Запрос c несуществующим номером;
     * 404 Запрос c несуществующим номер курьера; 409 Заказ нельзя завершить.
     */
    public Response finishOrder(int orderId){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .when()
            .put(ORDERS_API + "/finish/" + orderId);
    }

    /**
     * Получить заказ по его номеру.
     * @param trackId Трекинговый номер заказа.
     * @apiNote 200 Response success; 400 Запрос без номера; 404 Запрос c несуществующим номером;
     */
    public Response getOrderByTrackId(int trackId){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .get(ORDERS_API + "/track?t="+trackId);
    }

    /**
     * Отменить заказ.
     * @param trackId Номер заказа.
     * @apiNote 200 Успешный ответ; 400 Запрос без номера; 404 Запрос c несуществующим номером;
     * 409 Заказ уже в работе.
     */
    public Response cancelOrderByTrackId(int trackId){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .and()
            .body("{ \"track\": " + trackId + " }")
            .put(ORDERS_API + "/cancel");
    }

    /**
     * Получение списка заказов.
     * @param courierId Идентификатор курьера.
     * @param nearestStation Фильтр станций метро.
     * @param limit Количество заказов на странице.
     * @param page Текущая страница показа заказов.
     * @apiNote 200 Успешный ответ; 404 Запрос c несуществующим id.
     */
    public Response getOrderList(Integer courierId, String nearestStation, Integer limit, Integer page){
        return given()
            .header("Content-type", HEADER_CONTENT_TYPE)
            .queryParam("courierId", courierId)
            .queryParam("nearestStation", nearestStation)
            .queryParam("limit", limit)
            .queryParam("page", page)
            .and()
            .get(ORDERS_API);
    }
}
