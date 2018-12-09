package com.android.agzakhanty.general.api;


import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

/**
 * Created by GAFI on 28/08/2017.
 */

public class ErrorUtils {

    public static ApiError parseError(Response<?> response) {
        Converter<ResponseBody, ApiError> converter =
                ApiClient.getClient()
                        .responseBodyConverter(ApiError.class, new Annotation[0]);

        ApiError error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiError();
        }

        return error;
    }
}
