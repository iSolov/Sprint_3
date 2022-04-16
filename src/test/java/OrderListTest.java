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
 * ���� ������ �������.
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
     * ���������� �������.
     * @return ������������� �������.
     */
    private int prepareCourier(){
        Courier courier = Courier.getRandomCourier();

        // �������� ������ �������
        steps.registerNewCourier(courier);

        // ����������� ������ �������
        return
            steps.loginCourier(courier)
            .then().extract().body().path("id");
    }

    /**
     * ���������� ������.
     * @param courierId ������������� �������.
     */
    private void prepareOrderByCourierId(int courierId){
        // �������� ������ ������
        int trackId =
            steps
                .makeOrder(Order.getMockOrderWithTwoColors())
                .then().extract().body().path("track");

        // ��������� ������ �� �����
        int orderId =
            steps
                .getOrderByTrackId(trackId)
                .then().extract().body().path("order.id");

        // ������������� ������
        steps.acceptOrder(orderId, courierId);

        // ���������� ������
        steps.finishOrder(orderId);
    }

    @Test
    @DisplayName("������ ���� ����������� �������� ������ ������� �� ������������� �������")
    public void shouldGetOrderListByCourierIdTest() {
        // ���������� �������.
        int courierId = prepareCourier();

        // ���������� ������.
        prepareOrderByCourierId(courierId);

        // ��������� ������ �������
        steps.getOrderListByCourierId(courierId)
            .then().assertThat().body("orders", hasSize(2))
            .and().statusCode(HttpStatus.SC_OK);
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
