package com.example.weather.cb;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class IConvertCallback<T> implements ICallback<T>, Callback {
    @Override
    public void onFailure(@NotNull Call call, @NotNull IOException e) {
        Log.d("mylog","onFailure"+e);
    }

    @Override
    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
        onSuccess(response.body().string());
    }

    @Override
    public void onSuccess(String responce) {
    }
}
