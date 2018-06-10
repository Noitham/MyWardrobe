package com.soft.morales.mysmartwardrobe.model.persist;

public class ApiUtils {

    // Constructor
    private ApiUtils() {
    }

    //Trailing slash is needed
    public static final String BASE_URL = "http://52.47.130.162/Project/";

    //public static final String BASE_URL = "http://192.168.127.125:8000/";

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
