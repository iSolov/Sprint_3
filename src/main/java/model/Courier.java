package model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * Описание курьера.
 */
public class Courier {
    /**
     * Логин.
     */
    private String login;

    /**
     * Пароль.
     */
    private String password;

    /**
     * Имя.
     */
    private String firstName;

    /**
     * Создание курьера по логину без пароля.
     * @param login Логин.
     */
    public Courier(String login) {
        this.login = login;
        this.password = null;
        this.firstName = null;
    }

    /**
     * Создание курьера со всеми необходимыми полями.
     * @param login Логин.
     * @param password Пароль.
     */
    public Courier(String login, String password) {
        this.login = login;
        this.password = password;
        this.firstName = null;
    }

    /**
     * Создание курьера.
     * @param login Логин.
     * @param password Пароль.
     * @param firstName Имя.
     */
    public Courier(String login, String password, String firstName) {
        this.login = login;
        this.password = password;
        this.firstName = firstName;
    }

    /**
     * Получить курьера со случайными данными.
     * @return
     */
    public static Courier getRandomCourier() {
        return new Courier(
            getRandomLogin(),
            getRandomPassword(),
            getRandomFirstName()
        );
    }

    /**
     * Получить логин курьера.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Установить логин.
     * @param login Новый логин.
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Установить пароль.
     * @param password Новый пароль.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Получить пароль курьера.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Установить новое имя.
     * @param firstName Новое имя.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Получить имя курьера.
     */
    public String getFirstName() {
        return firstName;
    }


    /**
     * Получить случайный логин.
     */
    public static String getRandomLogin() {
        return getRandomString(10);
    }

    /**
     * Получить случайный пароль.
     */
    public static String getRandomPassword() {
        return getRandomString(10);
    }

    /**
     * Получить случайное имя.
     */
    private static String getRandomFirstName() {
        return getRandomString(10);
    }

    /**
     * Получить курьера в виде json-строки.
     */
    public static String toJson(Courier courier){
        Gson gson = new GsonBuilder().serializeNulls().create();
        return gson.toJson(courier);
    }

    /**
     * Получить случайную строку указанной длины.
     * @param count Количество символов в случайной строке.
     */
    private static String getRandomString(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }

    @Override
    public String toString(){
        return "[Login: " + getLogin() + "; password: " + getPassword() + "; First name: " + getFirstName() + "]";
    }
}
