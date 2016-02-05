package com.caliente.express.tests.testsupport;

import com.caliente.express.api.models.Order;
import com.caliente.express.api.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */
public class TestData {
    public static User testUser;
    public static String password = "12345";
    public static List<Order> orders = new ArrayList<Order>();

    public static User getTestUser() {
        return testUser;
    }
    public static void setTestUser(User testUser) {
        TestData.testUser = testUser;
    }

    public static String getPassword() {
        return password;
    }
    public static void setPassword(String value)  {
        password = value;
    }

    public static List<Order> getOrders() {
        return orders;
    }
    public static void setOrders(List<Order> orders) {
        TestData.orders = orders;
    }
}
