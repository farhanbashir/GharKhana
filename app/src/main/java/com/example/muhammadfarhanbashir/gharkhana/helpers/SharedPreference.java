package com.example.muhammadfarhanbashir.gharkhana.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.example.muhammadfarhanbashir.gharkhana.models.login.LoginBasicClass;
import com.google.gson.Gson;

/**
 * Created by muhammadfarhanbashir on 20/02/2017.
 */

public class SharedPreference {

    private static SharedPreference   sharedPreference;
    public static final String PREFS_NAME = "APP_PREFS";
    public static final String LOGGEDIN = "loggedIn";
    public static final String USER = "user";


    public static SharedPreference getInstance()
    {
        if (sharedPreference == null)
        {
            sharedPreference = new SharedPreference();
        }
        return sharedPreference;
    }

    public SharedPreference() {
        super();
    }

    public void save(Context context, String Key , String text) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putString(Key, text); //3

        editor.commit(); //4
    }

    public String getValue(Context context , String Key) {
        SharedPreferences settings;
        String text = "";
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        text = settings.getString(Key, "");
        return text;
    }

    public void clearSharedPreference(Context context) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.clear();
        editor.commit();
    }

    public void removeValue(Context context , String value) {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();

        editor.remove(value);
        editor.commit();
    }

    public void setLoggedIn(Context context, Boolean value)
    {
        SharedPreferences settings;
        Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
        editor = settings.edit(); //2

        editor.putBoolean(LOGGEDIN, value);

        editor.commit(); //4
    }

    public LoginBasicClass getUserObject(Context context)
    {
        SharedPreferences settings;
        String user = "";
        LoginBasicClass userObject = new LoginBasicClass();

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        user = settings.getString(USER, "");

        if(!user.equals(""))
        {
            Gson gson = new Gson();
            userObject = gson.fromJson(user, LoginBasicClass.class);

        }

        return userObject;
    }

    public Boolean getLoggedIn(Context context)
    {
        SharedPreferences settings;
        Boolean result = false;

        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        result = settings.getBoolean(LOGGEDIN, false);
        return result;
    }
}
