package Controller;

import apiSet.apiSet;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class controller {
    private static final String URL = "http://192.168.195.161/smart_attendance/";
    private static controller clientObj;
    private static Retrofit retrofit;

    controller() {
        retrofit = new Retrofit.Builder()
                .baseUrl(URL).addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized controller getInstance() {
            if(clientObj == null)
                clientObj = new controller();
            return clientObj;
    }

    public apiSet getAPI(){
        return retrofit.create(apiSet.class);
    }

}
