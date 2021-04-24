package com.example.weather.cb;

public interface ICallback<T> {
    void onSuccess(String responce);
    void onComplete(String data);
    void onComplete(T t);
}
