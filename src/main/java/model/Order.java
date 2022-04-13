package model;

import com.google.gson.Gson;

/**
 * Заказ.
 */
public class Order {
    private String firstName;
    private String lastName;
    private String address;
    private String metroStation;
    private String phone;
    private int rentTime;
    private String deliveryDate;
    private String comment;
    private String[] color;

    private int id;
    private boolean cancelled;
    private boolean finished;
    private boolean inDelivery;
    private String createdAt;
    private String updatedAt;
    private int status;
    private int track;

    public Order(){

    }

    public Order(String firstName, String lastName, String address, String metroStation,
        String phone, int rentTime, String deliveryDate, String comment, String[] color) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
        this.color = color;
    }

    public Order(String firstName, String lastName, String address, String metroStation,
        String phone, int rentTime, String deliveryDate, String comment) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.metroStation = metroStation;
        this.phone = phone;
        this.rentTime = rentTime;
        this.deliveryDate = deliveryDate;
        this.comment = comment;
    }

    public int getId() {
        return id;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Получает заготовку заказа без указания цветов.
     */
    public static Order getMockOrderWithoutColor(){
        return new Order(
            "Naruto",
            "Uchiha",
            "Konoha, 142 apt.",
            "4",
            "+7 800 355 35 35",
            5,
            "2020-06-06",
            "Saske, come back to Konoha"
        );
    }

    /**
     * Получает заготовку заказа только с черным цветом.
     */
    public static Order getMockOrderWithBlackColor(){
        Order mock = Order.getMockOrderWithoutColor();
        mock.color = new String[]{ "BLACK" };
        return mock;
    }

    /**
     * Получает заготовку заказа со всеми цветами.
     */
    public static Order getMockOrderWithTwoColors(){
        Order mock = Order.getMockOrderWithoutColor();
        mock.color = new String[]{ "BLACK", "GREY" };
        return mock;
    }
}
