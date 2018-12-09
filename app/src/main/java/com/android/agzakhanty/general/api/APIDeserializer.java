package com.android.agzakhanty.api;

import com.android.agzakhanty.api.responses.MainWSResponseModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;


/**
 * Created by a.moanes on 09/10/2017.
 */

 class APIDeserializer implements JsonDeserializer<MainWSResponseModel> {
    @Override
    public MainWSResponseModel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        MainWSResponseModel result = new Gson().fromJson(json, MainWSResponseModel.class);
        JsonArray itemsArr = json.getAsJsonObject().getAsJsonArray("Items");
        return result;
    }
}

