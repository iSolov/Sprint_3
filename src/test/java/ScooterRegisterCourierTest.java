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
 * ���� ����������� �������.
 */
public class ScooterRegisterCourierTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();

    /**
     * ������� ����� ������� (������������ ���������� ������ � � ���� ������� ���������).
     */
    @Test
    @DisplayName("������ ���� ����������� ������� �������")
    public void shouldRegisterNewCourier(){
        Courier randomCourier = Courier.getRandomCourier();

        apiCourier
            .registerNewCourier(randomCourier)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED); // �������� �������� ������� ������
    }

    /**
     * ������ ������� ���� ���������� ��������.
     */
    @Test
    @DisplayName("������ ������� ���� ���������� ��������")
    public void shouldGetErrorWhenTwoEqualCouriersAreCreated(){
        Courier courier = Courier.getRandomCourier();

        boolean isCourierRegistered =
            apiCourier
                .registerNewCourier(courier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isCourierRegistered){
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        apiCourier
            .registerNewCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // ������ � ������������� �������
    }

    /**
     * ����� ������� �������, ����� �������� � ����� ��� ������������ ����.
     */
    @Test
    @DisplayName("����� ������� �������, ����� �������� � ����� ��� ������������ ����")
    public void shouldCreateNewCreateWithOnlyNecessaryFields(){
        Courier courierWithoutFirstName = new Courier(Courier.getRandomLogin(), Courier.getRandomPassword());

        apiCourier
            .registerNewCourier(courierWithoutFirstName)
            .then().assertThat().body("ok", equalTo(true))
            .and().assertThat().statusCode(HttpStatus.SC_CREATED);
    }

    /**
     * ���� ������ �� ����� ���, ������ ���������� ������.
     */
    @Test
    @DisplayName("������ ���� ������, ���� ��� �������� ������� �� ������� ������")
    public void shouldGetErrorWhenRegisterNewCourierWithoutPassword(){
        Courier courierWithoutPassword = new Courier(Courier.getRandomLogin());

        apiCourier
            .registerNewCourier(courierWithoutPassword)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // ������ ��� ������ ��� ������
    }

    /**
     * ���� ������� ������������ � �������, ������� ��� ����, ������������ ������.
     */
    @Test
    @DisplayName("������ ���� ������, ���� ��������� ������ � ������������ ������")
    public void shouldGetErrorWhenTwoCouriersWithEqualLoginsAreCreated(){
        Courier firstCourier = Courier.getRandomCourier();

        boolean isFirstCourierRegistered =
            apiCourier
                .registerNewCourier(firstCourier)
                .then().statusCode(HttpStatus.SC_CREATED)
                .and().extract().body().path("ok");

        if (!isFirstCourierRegistered){
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        Courier secondCourier = Courier.getRandomCourier();
        secondCourier.setLogin(firstCourier.getLogin());

        apiCourier
            .registerNewCourier(secondCourier)
            .then().assertThat().statusCode(HttpStatus.SC_CONFLICT); // ������ � ������������� �������
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
