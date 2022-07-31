package apiSet;

import ResponseModel.responseModel;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface apiSet {

//    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("register.php")
    Call<responseModel> addUser(
            @Field("enrollment") String enrollment

    );

    @FormUrlEncoded
    @POST("attendance.php")
    Call<responseModel> addAttendance(
            @Field("subject_name") String subject_name,
            @Field("enrollment_no") String enrollment_no

    );
}
