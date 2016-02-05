package com.caliente.express.tests.testsupport;

import org.junit.Assert;

import com.caliente.express.api.ApiService;
import com.caliente.express.api.models.User;
import com.caliente.express.api.requests.LoginRequest;
import com.caliente.express.api.responses.ApiResponse;
import com.caliente.express.api.responses.OrderListResponse;
import com.caliente.express.api.responses.UserResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.util.StringUtil;

/**
 * Created by John R. Kosinski on 27/1/2559.
 */
public class TestLogic  {

    public static void signUp()
    {
        //create new user
        User user = new User();
        user.setPassword(TestData.getPassword());
        UserResponse response = createUser(user.getPassword());

        assert(response.isSuccessful());
        assert(response.getUser() != null);

        User newUser = response.getUser();
        TestData.setTestUser(newUser);

        //test values in returned user
        assert(newUser.getId() > 0);
        assert(!StringUtil.isNullOrEmpty(newUser.getAuthToken()));

        //test values in local storage
        Assert.assertEquals(newUser.getAuthToken(), LocalStorage.getAuthToken());
        Assert.assertEquals(newUser.getUsername(), LocalStorage.getUsername());
        Assert.assertEquals(newUser.getId(), LocalStorage.getUserId());
        Assert.assertEquals(newUser.getId(), LocalStorage.getCurrentTargetUserId());
        Assert.assertEquals(newUser.getPermissionLevel(), LocalStorage.getPermissionLevel());

        //try again, and ensure that user name is taken
        response = createUser(TestData.getTestUser().getUsername(), "passwd11");
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //try with bad username
        response = createUser("X", "passwd11");
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);

        //try with bad password
        response = createUser("X");
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 400);
    }

    public static void logOut()
    {
        //log out
        ApiResponse response = ApiService.logout();
        assert(response.isSuccessful());
        assert(response.getResponseCode() == 200);

        //try to log out again - should not be allowed
        response = ApiService.logout();
        assert(!response.isSuccessful());

        //try to do something that requires login and verify that you cannot
        UserResponse userResponse = ApiService.getUser(TestData.getTestUser().getId());
        assert(!userResponse.isSuccessful());
        assert(userResponse.getUser() == null);
        Assert.assertEquals(403, userResponse.getResponseCode());
    }

    public static void logIn()
    {
        //attempt a login with bad Password
        LoginRequest request = new LoginRequest(TestData.getTestUser().getUsername(), TestData.getPassword() + "!");
        UserResponse response = ApiService.login(request);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //attempt a login with bad username
        request = new LoginRequest(TestData.getTestUser().getUsername() + "L", TestData.getPassword());
        response = ApiService.login(request);
        assert(!response.isSuccessful());
        assert(response.getResponseCode() == 409);

        //attempt a good login
        request = new LoginRequest(TestData.getTestUser().getUsername(), TestData.getPassword());
        response = ApiService.login(request);
        assert(response.isSuccessful());
        assert(response.getResponseCode() == 200);

        //test user properties
        User user = response.getUser();
        Assert.assertEquals(TestData.getTestUser().getUsername(), user.getUsername());

        TestData.setTestUser(user);

        //test values in local storage
        Assert.assertEquals(TestData.getTestUser().getAuthToken(), LocalStorage.getAuthToken());
        Assert.assertEquals(TestData.getTestUser().getUsername(), LocalStorage.getUsername());
        Assert.assertEquals(TestData.getTestUser().getId(), LocalStorage.getUserId());
        Assert.assertEquals(TestData.getTestUser().getId(), LocalStorage.getCurrentTargetUserId());
        Assert.assertEquals(TestData.getTestUser().getPermissionLevel(), LocalStorage.getPermissionLevel());

        //get orders
        OrderListResponse orders = ApiService.getOrders(TestData.getTestUser().getId());
        assert(orders.getResponseCode() == 204);
        assert(orders.getOrders().size() == 0);
    }

    public static UserResponse createUser(String password)
    {
        return createUser(null, password);
    }

    public static UserResponse createUser(String username, String password)
    {
        User user = new User();

        if (username != null)
            user.setUsername(username);
        else
            user.setUsername("abcde");

        user.setPassword(password);
        user.setPermissionLevel("User");

        UserResponse response = ApiService.createUser(user);

        int usernameIndex = 0;
        if (username == null) {
            while (!response.isSuccessful() && response.getResponseCode() == 409) {
                user.setUsername("abcde" + Integer.toString(usernameIndex));
                response = ApiService.createUser(user);
                usernameIndex++;
            }
        }

        return response;
    }
}
