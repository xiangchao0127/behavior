package com.handge.bigdata.models;

public class Reponse1<T> {
    private T Response;

    public T getResponse() {
        return Response;
    }

    public void setResponse(T response) {
        Response = response;
    }

    @Override
    public String toString() {
        return "Reponse1{" +
                "Response=" + Response +
                '}';
    }
}
