import static org.hamcrest.Matchers.hasSize;

import client.ScooterCourierApiClient;
import client.ScooterOrderApiClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.Order;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тест списка заказов.
 */
public class OrderListTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();
    private final ScooterOrderApiClient apiOrder = new ScooterOrderApiClient();

    @Step("Регистрация нового курьера")
    public Response registerNewCourier(Courier courier){
        return apiCourier.registerNewCourier(courier);
    }

    @Step("Авторизация курьера")
    public Response loginCourier(Courier courier){
        return apiCourier.loginCourier(courier);
    }

    @Step("Создание заказа")
    public Response makeOrder(Order order){
        return apiOrder.makeOrder(Order.getMockOrderWithTwoColors());
    }

    @Step("Получение заказа по треку")
    public Response getOrderByTrackId(int trackId){
        return apiOrder.getOrderByTrackId(trackId);
    }

    @Step("Подтверждение заказа")
    public boolean acceptOrder(int orderId, int courierId){
        return
            apiOrder
                .acceptOrder(orderId, courierId)
                .then().statusCode(HttpStatus.SC_OK)
                .and().extract().body().path("ok");
    }

    @Step("Завершение заказа")
    public boolean finishOrder(int orderId){
        return
            apiOrder
                .finishOrder(orderId)
                .then().statusCode(HttpStatus.SC_OK)
                .and().extract().body().path("ok");
    }

    @Step("Получение списка заказов по курьеру")
    public Response getOrderListByCourierId(int courierId){
        return apiOrder.getOrderList(courierId, null, null, null);
    }

    @Test
    @DisplayName("Должна быть возможность получить список заказов по идентификатор курьера")
    public void shouldGetOrderListByCourierId() {
        Courier courier = Courier.getRandomCourier();

        // Создание нового курьера
        Response registerNewCourierResponse = registerNewCourier(courier);
        if (registerNewCourierResponse.statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать курьера для проверки.");
            return;
        }

        // Авторизация нового курьера
        Response loginCourierResponse = loginCourier(courier);
        if (loginCourierResponse.statusCode() != HttpStatus.SC_OK){
            Assert.fail("Не удалось залогиниться курьеру.");
            return;
        }
        int courierId = loginCourierResponse.then().extract().body().path("id");

        // Создание нового заказа
        Response makeOrderResponse = makeOrder(Order.getMockOrderWithTwoColors());
        if (makeOrderResponse.statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("Не удалось создать заказ.");
            return;
        }
        int trackId = makeOrderResponse.then().extract().body().path("track");

        // Получение заказа по треку
        Response getOrderByTrackIdResponse = getOrderByTrackId(trackId);
        if (getOrderByTrackIdResponse.statusCode() != HttpStatus.SC_OK){
            Assert.fail("Не удалось получить заказ по треку.");
            return;
        }
        int orderId = getOrderByTrackIdResponse.then().extract().body().path("order.id");

        // Подтверждение заказа
        boolean isAccepted = acceptOrder(orderId, courierId);

        if (!isAccepted){
            Assert.fail("Не удалось подтвердить заказ.");
            return;
        }

        // Завершение заказа
        boolean isFinished = finishOrder(orderId);

        if (!isFinished){
            Assert.fail("Не удалось завершить заказ.");
            return;
        }

        // Получение списка заказов
        getOrderListByCourierId(courierId)
            .then().assertThat().body("orders", hasSize(2))
            .and().statusCode(HttpStatus.SC_OK);
    }

    @Before
    public void beforeTest(){
        apiCourier.clearCreatedCouriers();
    }
}
