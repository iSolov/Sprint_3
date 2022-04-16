import client.BaseHttpClient;
import client.ScooterOrderApiClient;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import model.Order;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * ���� ������.
 */
@RunWith(Parameterized.class)
public class OrderTest {
    private final ScooterOrderApiClient api = new ScooterOrderApiClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    private final Order order;
    private final Object expectedResponseStatusCode;

    public OrderTest(Order order, Object expectedResponseStatusCode){
        this.order = order;
        this.expectedResponseStatusCode = expectedResponseStatusCode;
    }

    @Parameterized.Parameters
    public static Object[][] getColorsData() {
        return new Object[][]{
            { Order.getMockOrderWithoutColor(), HttpStatus.SC_CREATED }, // ����� ������ �� ��������� ����
            { Order.getMockOrderWithBlackColor(), HttpStatus.SC_CREATED }, // ����� ������� ���� �� ������ � BLACK ��� GREY
            { Order.getMockOrderWithTwoColors(), HttpStatus.SC_CREATED } // ����� ������� ��� �����
        };
    }

    @Test
    @DisplayName("������ ���� ����������� ������� ����� � ������ ������� ��������� ������")
    public void shouldMakeOrderTest() {
        api
            .makeOrder(this.order)
            .then().assertThat().statusCode((int)this.expectedResponseStatusCode);
    }
}
