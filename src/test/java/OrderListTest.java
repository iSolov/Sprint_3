import static org.hamcrest.Matchers.hasSize;

import client.BaseHttpClient;
import client.ScooterCourierApiClient;
import client.ScooterOrderApiClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import model.Order;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Тест списка заказов.
 */
public class OrderListTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();
    private final ScooterOrderApiClient apiOrder = new ScooterOrderApiClient();

    private final OrderListStepsTest steps = new OrderListStepsTest(apiCourier, apiOrder);

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    /**
     * Подготовка курьера.
     * @return Идентификатор курьера.
     */
    private int prepareCourier(){
        Courier courier = Courier.getRandomCourier();

        // Создание нового курьера
        steps.registerNewCourier(courier);

        // Авторизация нового курьера
        return
            steps.loginCourier(courier)
            .then().extract().body().path("id");
    }

    /**
     * Подготовка заказа.
     * @param courierId Идентификатор курьера.
     */
    private void prepareOrderByCourierId(int courierId){
        // Создание нового заказа
        int trackId =
            steps
                .makeOrder(Order.getMockOrderWithTwoColors())
                .then().extract().body().path("track");

        // Получение заказа по треку
        int orderId =
            steps
                .getOrderByTrackId(trackId)
                .then().extract().body().path("order.id");

        // Подтверждение заказа
        steps.acceptOrder(orderId, courierId);

        // Завершение заказа
        steps.finishOrder(orderId);
    }

    @Test
    @DisplayName("Должна быть возможность получить список заказов по идентификатор курьера")
    public void shouldGetOrderListByCourierIdTest() {
        // Подготовка курьера.
        int courierId = prepareCourier();

        // Подготовка заказа.
        prepareOrderByCourierId(courierId);

        // Получение списка заказов
        steps.getOrderListByCourierId(courierId)
            .then().assertThat().body("orders", hasSize(2))
            .and().statusCode(HttpStatus.SC_OK);
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
