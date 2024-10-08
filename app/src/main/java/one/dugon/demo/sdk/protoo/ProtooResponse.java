package one.dugon.demo.sdk.protoo;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

public class ProtooResponse {
    private String response;
    public int id;
    private boolean ok;
    public JsonObject data;

    @NonNull
    @Override
    public String toString() {
        return "response:"+id;
    }
}
