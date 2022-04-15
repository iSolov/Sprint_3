import client.ScooterOrderApiClient;

import io.qameta.allure.junit4.DisplayName;
import model.Order;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Тест заказа.
 */
@RunWith(Parameterized.class)
public class OrderTest {
    private final ScooterOrderApiClient api = new ScooterOrderApiClient();

    private Order order;
    private Object expectedResponseStatusCode;

    public OrderTest(Order order, Object expectedResponseStatusCode){
        this.order = order;
        this.expectedResponseStatusCode = expectedResponseStatusCode;
    }

    @Parameterized.Parameters
    public static Object[][] getColorsData() {
        return new Object[][]{
            { Order.getMockOrderWithoutColor(), HttpStatus.SC_CREATED }, // можно совсем не указывать цвет
            { Order.getMockOrderWithBlackColor(), HttpStatus.SC_CREATED }, // можно указать один из цветов — BLACK или GREY
            { Order.getMockOrderWithTwoColors(), HttpStatus.SC_CREATED } // можно указать оба цвета
        };
    }

    @Test
    @DisplayName("Должна быть возможность сделать заказ с разным набором выбранных цветов")
    public void shouldMakeOrder() {
        api
            .makeOrder(this.order)
            .then().assertThat().statusCode((int)this.expectedResponseStatusCode);
    }
}
