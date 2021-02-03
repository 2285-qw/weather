package com.example.weatherhuihaoda.cb;

public interface ICallback<T> {
    void onSuccess(String responce);
    void onComplete(String data);
    void onComplete(T t);
}
