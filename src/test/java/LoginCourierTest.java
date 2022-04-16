import static org.hamcrest.Matchers.greaterThan;

import client.BaseHttpClient;
import client.ScooterCourierApiClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * ���� ����������� �������.
 */
public class LoginCourierTest {
    private final ScooterCourierApiClient apiCourier = new ScooterCourierApiClient();

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseHttpClient.BASE_URL;
    }

    /**
     * ������ ����� ��������������.
     */
    @Test
    @DisplayName("������ ������ ������� ��������������")
    public void shouldNewCourierSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = apiCourier.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() == HttpStatus.SC_CREATED){ // �������� �������� ������� ������
            apiCourier
                .loginCourier(randomCourier)
                .then().assertThat().statusCode(HttpStatus.SC_OK)
                .and().body("id", greaterThan(0)); // �������� �����
        }
        else{
            Assert.fail("�� ������� ������� ������� ��� ��������.");
        }
    }

    /**
     * ��� ����������� ����� �������� ��� ������������ ����.
     */
    @Test
    @DisplayName("������ ������ ������� �������������� ������ �� ����� ������������� ������")
    public void shouldLoginCourierWithAllNecessaryFieldsTest(){
        Courier courier = Courier.getRandomCourier();

        courier.setFirstName(null);

        Response registerResponse = apiCourier.registerNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // �������� �������� ������� ������
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        apiCourier
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_OK)
            .and().body("id", greaterThan(0)); // �������� �����
    }

    /**
     * ������� ����� ������, ���� ����������� ������� ����� ��� ������.
     */
    @Test
    @DisplayName("������ ���� ������ �����������, ���� ����� �� �������")
    public void shouldGetErrorWhenLoginIsMissedTest(){
        Courier courier = Courier.getRandomCourier();

        if (apiCourier.registerNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        courier.setLogin(null);

        apiCourier
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // ������ ��� ������ ��� ������
    }

    /**
     * ���� ������-�� ���� ���, ������ ���������� ������.
     */
    @Test
    @DisplayName("������ ���� ������ �����������, ���� ������ �� �������")
    public void shouldGetErrorWhenPasswordIsMissingTest(){
        Courier courier = Courier.getRandomCourier();

        if (apiCourier.registerNewCourier(courier).statusCode() != HttpStatus.SC_CREATED){
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        courier.setPassword("");

        apiCourier
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_BAD_REQUEST); // ������ ��� ������ ��� ������
    }

    /**
     * ���� �������������� ��� �������������� �������������, ������ ���������� ������;.
     */
    @Test
    @DisplayName("������ ���� ������ �����������, ���� ���� ��� �������������� �������")
    public void shouldGetErrorWhenLoginWithNotExistedLoginTest(){
        Courier courier = Courier.getRandomCourier();

        // ������� �������. ������������ ��� ���. ������� ���. �������� �������������� ��� ���.

        Response registerResponse = apiCourier.registerNewCourier(courier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // �������� �������� ������� ������
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        Response loginResponse = apiCourier.loginCourier(courier);
        if (loginResponse.statusCode() != HttpStatus.SC_OK){ // �������� �����
            int id = loginResponse.then().extract().body().path("id");
            apiCourier.deleteCourier(id);

            String message = loginResponse.then().extract().body().path("message");
            Assert.fail("�� ������� ������������ ������� (login:" + courier.getLogin() + "): " + message);

            return;
        }

        int id = loginResponse.then().extract().body().path("id");

        Response deleteResponse = apiCourier.deleteCourier(id);
        if (deleteResponse.statusCode() != HttpStatus.SC_OK){ // �������� �������� �������
            String message = deleteResponse.then().extract().body().path("message");
            Assert.fail("�� ������� ������� ������� ��� ��������� (login:" + courier.getLogin() + "/id:" + id + "): " + message);

            return;
        }

        apiCourier
            .loginCourier(courier)
            .then().assertThat().statusCode(HttpStatus.SC_NOT_FOUND); // ������ c �������������� ����� �����-������
    }

    /**
     * �������� ������ ���������� id.
     */
    @Test
    @DisplayName("�������� ����������� ���������� ������������� �������")
    public void shouldGetCourierIdWhenSuccessLoginTest(){
        Courier randomCourier = Courier.getRandomCourier();

        Response registerResponse = apiCourier.registerNewCourier(randomCourier);
        if (registerResponse.statusCode() != HttpStatus.SC_CREATED){ // �������� �������� ������� ������
            Assert.fail("�� ������� ������� ������� ��� ��������.");
            return;
        }

        apiCourier
            .loginCourier(randomCourier)
            .then().assertThat().body("id", greaterThan(0))
            .and().statusCode(HttpStatus.SC_OK);
    }

    @After
    public void afterTest(){
        apiCourier.clearCreatedCouriers();
    }
}
