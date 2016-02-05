package com.caliente.express.api;

import com.caliente.express.api.models.AppSettings;
import com.caliente.express.api.models.ErrorInfo;
import com.caliente.express.api.models.MenuItem;
import com.caliente.express.api.models.Order;
import com.caliente.express.api.models.FilterParams;
import com.caliente.express.api.models.User;
import com.caliente.express.api.requests.ApiRequest;
import com.caliente.express.api.requests.LoginRequest;
import com.caliente.express.api.responses.ApiResponse;
import com.caliente.express.api.responses.AppSettingsResponse;
import com.caliente.express.api.responses.MenuResponse;
import com.caliente.express.api.responses.OrderListResponse;
import com.caliente.express.api.responses.UserResponse;
import com.caliente.express.api.responses.UserListResponse;
import com.caliente.express.storage.LocalStorage;
import com.caliente.express.util.StringUtil;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.GZIPInputStream;

/**
 * Created by John R. Kosinski on 21/1/2559.
 */
public class ApiService {
    private static final String LogTag = "ApiService";
    private static final String loginUri = "login";
    private static final String logoutUri = "logout";
    private static final String usersUri = "users";
    private static final String ordersUri = "orders";
    private static final String appSettingsUri = "appSettings";
    private static final String reportUri = "report";
    private static String apiUriBase;
    private static UserListResponse cachedUsersList;
    private static final boolean useCompression = true;

    public static void configure(final String uriBase) {
        apiUriBase = uriBase;
    }

    //Login
    public static UserResponse login(final LoginRequest request) {
        UserResponse response = SendRequestWithContent(
                constructLoginUri(),
                "POST",
                request,
                new ParseUserResponseCallback(),
                null
        );

        if (response != null && response.isSuccessful()) {
            doPostLogin(response.getUser(), request.getPassword());
        }

        return response;
    }

    //Logout
    public static ApiResponse logout() {
        return SendRequestWithContent(
                constructLogoutUri(),
                "POST",
                new ApiRequest(),
                new ParseGenericResponseCallback(),
                null
        );
    }

    //GetUsers
    public static UserListResponse getUsers() {
        if (cachedUsersList != null)
            return cachedUsersList;

        final String uri = constructUsersUri();
        final String method = "GET";
        final ParseUserListResponseCallback callback = new ParseUserListResponseCallback();

        RetryCallback<UserListResponse> retryCallback = new RetryCallback<UserListResponse>(){
            @Override
            protected UserListResponse execute()  {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetUser
    public static UserResponse getUser(final int userId)
    {
        final String uri = constructUserUri(userId);
        final String method = "GET";
        final ParseUserResponseCallback callback = new ParseUserResponseCallback();

        RetryCallback<UserResponse> retryCallback = new RetryCallback<UserResponse>(){
            @Override
            protected UserResponse execute()  {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //CreateUser
    public static UserResponse createUser(final User user)
    {
        final String uri = constructUsersUri();
        final String method = "POST";
        final ParseCreateUserResponseCallback callback = new ParseCreateUserResponseCallback(user.getPassword());

        return SendRequestWithContent(uri, method, user, callback, null);
    }

    //UpdateUser
    public static UserResponse updateUser(final int userId, final User user)
    {
        final String uri = constructUserUri(userId);
        final String method = "PUT";
        final ParseUserResponseCallback callback = new ParseUserResponseCallback();

        RetryCallback<UserResponse> retryCallback = new RetryCallback<UserResponse>(){
            @Override
            protected UserResponse execute() {
                return SendRequestWithContent(uri, method, user, callback, null);
            }
        };

        return SendRequestWithContent(uri, method, user, callback, retryCallback);
    }

    //GetOrders
    public static OrderListResponse getOrders(final int userId) {
        return getOrders(userId, null);
    }

    //GetOrders
    public static OrderListResponse getOrders(final int userId, final FilterParams filterParams) {
        final String uri = (filterParams == null || filterParams.isEmpty()) ?
                constructOrdersUri(userId) :
                constructFilteredOrdersUri(userId, filterParams);
        final String method = "GET";
        final ParseOrderListResponseCallback callback = new ParseOrderListResponseCallback();

        RetryCallback<OrderListResponse> retryCallback = new RetryCallback<OrderListResponse>(){
            @Override
            protected OrderListResponse execute() {
                return SendRequest(uri, method, callback, null);
            }
        };

        return SendRequest(uri, method, callback, retryCallback);
    }

    //GetAppSettings
    public static AppSettingsResponse getAppSettings() {
        AppSettingsResponse output = SendRequest(
                constructAppSettingsUri(),
                "GET",
                new ParseAppSettingsResponseCallback(),
                null
        );

        return output;
    }

    //GetMenu
    public static MenuResponse getMenu()
    {
        MenuResponse output = new MenuResponse(200);

        MenuItem menuItem = new MenuItem();
        menuItem.setId(1);
        menuItem.setName("Cappuccino");
        menuItem.addOption("Hot", "40฿");
        menuItem.addOption("Iced", "45฿");
        output.getMenuItems().add(menuItem);

        menuItem = new MenuItem();
        menuItem.setId(2);
        menuItem.setName("Espresso");
        menuItem.addOption("Hot", "40฿");
        menuItem.addOption("Iced", "45฿");
        output.getMenuItems().add(menuItem);

        menuItem = new MenuItem();
        menuItem.setId(3);
        menuItem.setName("Americano");
        menuItem.addOption("Hot", "40฿");
        menuItem.addOption("Iced", "45฿");
        output.getMenuItems().add(menuItem);

        menuItem = new MenuItem();
        menuItem.setId(4);
        menuItem.setName("Africano");
        menuItem.addOption("Hot", "80฿");
        menuItem.addOption("Iced", "100฿");
        output.getMenuItems().add(menuItem);

        menuItem = new MenuItem();
        menuItem.setId(5);
        menuItem.setName("Lemon Tea");
        menuItem.addOption("Hot", "40฿");
        menuItem.addOption("Iced", "45฿");
        output.getMenuItems().add(menuItem);

        menuItem = new MenuItem();
        menuItem.setId(6);
        menuItem.setName("Green Tea");
        menuItem.addOption("Hot", "40฿");
        menuItem.addOption("Iced", "45฿");
        output.getMenuItems().add(menuItem);

        return output;
    }


    private static <TResponse extends ApiResponse> TResponse SendRequestWithContent(
            final String uri,
            final String method,
            final Object request,
            final IParseResponseCallback<TResponse> callback,
            final RetryCallback<TResponse> retryCallback
    )
    {
        TResponse output = null;

        try {
            //serialize input
            Gson gson = new Gson();
            String req = gson.toJson(request);
            byte[] sendData = req.getBytes();

            //make connection
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set up to send
            setUpRequest(connection, method, sendData);

            //send data
            OutputStream out = connection.getOutputStream();
            out.write(sendData);
            out.close();

            //get response
            return parseResponse(connection, callback, retryCallback);
        } catch (Exception e) {
            //Log.e("ApiService", e.getMessage());
        }

        return output;
    }

    private static <TResponse extends ApiResponse> TResponse SendRequest (
            final String uri,
            final String method,
            final IParseResponseCallback<TResponse> callback,
            final RetryCallback<TResponse> retryCallback
    )
    {
        TResponse output = null;

        try {
            //make connection
            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set up request
            setUpRequest(connection, method, null);

            //get response
            output = parseResponse(connection, callback, retryCallback);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return output;
    }

    private static void setUpRequest(HttpURLConnection connection, String method, byte[] data) throws ProtocolException
    {
        connection.setRequestMethod(method);

        if (useCompression)
            connection.setRequestProperty("Accept-Encoding", "gzip");
        else
            connection.setRequestProperty("Accept-Encoding", "identity");

        if (data != null)
        {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Length", Integer.toString(data.length));
        }

        connection.setUseCaches(false);
        if (!StringUtil.isNullOrEmpty(LocalStorage.getAuthToken()))
            connection.setRequestProperty("AuthToken", LocalStorage.getAuthToken());
    }

    private static <TResponse extends ApiResponse> TResponse parseResponse(
            HttpURLConnection connection,
            IParseResponseCallback<TResponse> callback,
            RetryCallback<TResponse> retryCallback
    )  throws IOException
    {
        TResponse output = null;
        int responseCode = connection.getResponseCode();
        int responseLength = connection.getContentLength();

        if (responseCode == 401)
        {
            if (retryCallback != null) {
                output = tryAutoLogin(retryCallback);
                if (output != null)
                    return output;
            }
        }

        //prepare to read the output
        if (responseLength > 0)
        {
            char[] responseChars = new char[responseLength];
            InputStream in = responseIsSuccess(responseCode) ?
                    connection.getInputStream() :
                    connection.getErrorStream();

            if (useCompression)
                in = new GZIPInputStream(in);

            InputStreamReader ips = new InputStreamReader(in);
            StringBuilder buffer = new StringBuilder();

            try {
                int l;

                while ((l = ips.read(responseChars)) != -1) {
                    buffer.append(responseChars, 0, l);
                }

            } finally {
                ips.close();
            }

            String responseString = buffer.toString();

            //parse response json
            if (callback != null) {
                output = callback.parseJson(responseString, responseCode);
            }
            else {
                //output = (TResponse) gson.fromJson(responseString, TResponse);
            }
        }
        else
            output = callback.parseJson(null, responseCode);

        return output;
    }

    private static void doPostLogin(User user, String password)
    {
        LocalStorage.setAuthToken(user.getAuthToken());
        LocalStorage.setUsername(user.getUsername());
        LocalStorage.setUserId(user.getId());
        LocalStorage.setCurrentTargetUser(user);
        LocalStorage.setPassword(password);
        LocalStorage.setPermissionLevel(user.getPermissionLevel());
        cachedUsersList = null;
    }

    private static <TResponse extends ApiResponse>TResponse tryAutoLogin(RetryCallback<TResponse> retryCallback)
    {
        UserResponse loginResponse = login(new LoginRequest(LocalStorage.getUsername(), LocalStorage.getPassword()));
        if (loginResponse != null && loginResponse.isSuccessful())
        {
            if (retryCallback != null)
                return retryCallback.execute();
        }

        return null;
    }


    private static String constructUri(String uri)
    {
        String baseUrl = apiUriBase;
        if (!baseUrl.endsWith("/"))
            baseUrl += "/";

        return baseUrl + uri;
    }

    private static String constructAppSettingsUri()
    {
        return constructUri(appSettingsUri);
    }

    private static String constructLoginUri()
    {
        return constructUri(loginUri);
    }

    private static String constructLogoutUri()
    {
        return constructUri(logoutUri);
    }

    private static String constructUsersUri()
    {
        return constructUserUri(-1);
    }

    private static String constructUserUri(int userId)
    {
        String uri = usersUri;
        if (userId >= 0)
            uri += "/" + Integer.toString(userId);

        return constructUri(uri);
    }

    private static String constructOrdersUri(int userId)
    {
        return constructOrderUri(userId, 0);
    }

    private static String constructOrderUri(int userId, int orderId)
    {
        String uri = constructUserUri(userId);
        uri += "/" + ordersUri;

        if (orderId > 0)
            uri += "/" + Integer.toString(orderId);

        return uri;
    }

    private static String constructFilteredOrdersUri(int userId, FilterParams filterParams)
    {
        String uri = constructOrdersUri(userId);
        uri += constructFilterQuerystring(filterParams);

        return uri;
    }

    private static String constructFilterQuerystring(FilterParams filterParams)
    {
        boolean hasParams = false;
        String querystring = "";

        if (filterParams != null) {
            try {
                if (filterParams.getDateFrom() != null && filterParams.getDateFrom().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "dateFrom=" + (filterParams.getDateFrom());
                    hasParams = true;
                }
                if (filterParams.getDateTo() != null && filterParams.getDateTo().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "dateTo=" + (filterParams.getDateTo());
                    hasParams = true;
                }
                if (filterParams.getTimeFrom() != null && filterParams.getTimeFrom().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "timeFrom=" + (filterParams.getTimeFrom());
                    hasParams = true;
                }
                if (filterParams.getTimeTo() != null && filterParams.getTimeTo().length() > 0) {
                    querystring += (hasParams ? "&" : "?") + "timeTo=" + (filterParams.getTimeTo());
                    hasParams = true;
                }
            } catch (Exception e) {
                Log.e(LogTag, e.getMessage());
            }
        }

        return querystring;
    }

    private static boolean responseIsSuccess(int responseCode)
    {
        return (responseCode >= 200 && responseCode < 300);
    }


    //TODO: consolidate these
    private static interface IParseResponseCallback<T extends ApiResponse>
    {
        public T parseJson(String json, int responseCode);
    }

    private static class ParseUserResponseCallback implements IParseResponseCallback<UserResponse>
    {
        public UserResponse parseJson(String json, int responseCode)
        {
            UserResponse output = new UserResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    User user = (User) new Gson().fromJson(json, User.class);
                    output.setUser(user);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseCreateUserResponseCallback extends ParseUserResponseCallback
    {
        private String password;

        public ParseCreateUserResponseCallback(String password)
        {
            this.password = password;
        }

        public UserResponse parseJson(String json, int responseCode)
        {
            UserResponse output = super.parseJson(json, responseCode);

            if (output != null && output.isSuccessful()) {
                LocalStorage.clear();
                doPostLogin(output.getUser(), password);
            }

            return output;
        }
    }

    private static class ParseGenericResponseCallback implements IParseResponseCallback<ApiResponse>
    {
        public ApiResponse parseJson(String json, int responseCode)
        {
            ApiResponse output = new ApiResponse(responseCode);
            if (json != null) {
                if (!responseIsSuccess(responseCode)) {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseOrderListResponseCallback implements IParseResponseCallback<OrderListResponse>
    {
        public OrderListResponse parseJson(String json, int responseCode)
        {
            OrderListResponse output = new OrderListResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    Order[] array = new Gson().fromJson(json, Order[].class);
                    if (array != null)
                        output.setOrders(new ArrayList<Order>(Arrays.asList(array)));
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseUserListResponseCallback implements IParseResponseCallback<UserListResponse>
    {
        public UserListResponse parseJson(String json, int responseCode)
        {
            UserListResponse output = new UserListResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    User[] array = new Gson().fromJson(json, User[].class);
                    if (array != null) {
                        output.setUsers(new ArrayList<User>(Arrays.asList(array)));
                        cachedUsersList = output;
                    }
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class ParseAppSettingsResponseCallback implements  IParseResponseCallback<AppSettingsResponse>
    {
        public AppSettingsResponse parseJson(String json, int responseCode)
        {
            AppSettingsResponse output = new AppSettingsResponse(responseCode);
            if (json != null) {
                if (responseIsSuccess(responseCode)) {
                    AppSettings appSettings = (AppSettings) new Gson().fromJson(json, AppSettings.class);
                    output.setAppSettings(appSettings);
                } else {
                    ErrorInfo error = (ErrorInfo) new Gson().fromJson(json, ErrorInfo.class);
                    output.setErrorInfo(error);
                }
            }

            return output;
        }
    }

    private static class RetryCallback<TResponse extends  ApiResponse>
    {
        protected TResponse execute()
        {
            return null;
        }
    }
}
