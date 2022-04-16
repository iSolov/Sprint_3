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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.hasSize;

/**
 * ���� ��� ����� ������ �������.
 */
public class OrderListStepsTest {
    private final ScooterCourierApiClient apiCourier;
    private final ScooterOrderApiClient apiOrder;

    public OrderListStepsTest(ScooterCourierApiClient apiCourier, ScooterOrderApiClient apiOrder){
        this.apiCourier = apiCourier;
        this.apiOrder = apiOrder;
    }

    @Step("����������� ������ �������")
    public void registerNewCourier(Courier courier){
        apiCourier.registerNewCourier(courier);
    }

    @Step("����������� �������")
    public Response loginCourier(Courier courier){
        return apiCourier.loginCourier(courier);
    }

    @Step("�������� ������")
    public Response makeOrder(Order order){
        return apiOrder.makeOrder(order);
    }

    @Step("��������� ������ �� �����")
    public Response getOrderByTrackId(int trackId){
        return apiOrder.getOrderByTrackId(trackId);
    }

    @Step("������������� ������")
    public void acceptOrder(int orderId, int courierId){
        apiOrder.acceptOrder(orderId, courierId);
    }

    @Step("���������� ������")
    public void finishOrder(int orderId){
        apiOrder.finishOrder(orderId);
    }

    @Step("��������� ������ ������� �� �������")
    public Response getOrderListByCourierId(int courierId){
        return apiOrder.getOrderList(courierId, null, null, null);
    }
}
