package modelClass;

import java.time.LocalDate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import serializer.MyLocalDate;

public class MyJson {
    private Gson gson;

    public Gson getGson() {
        return gson;
    }

    public MyJson() {
        GsonBuilder gBuilder = new GsonBuilder().registerTypeAdapter(LocalDate.class, new MyLocalDate());
        this.gson = gBuilder.create();
    }

    
}