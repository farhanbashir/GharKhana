package com.example.muhammadfarhanbashir.gharkhana.models.login;

/**
 * Created by muhammadfarhanbashir on 20/02/2017.
 */

public class LoginBasicClass {
    public String user_id;
    public String role_id;
    public String first_name;
    public String last_name;
    public String contact_number;
    public String email;
    public String password;
    public String new_password;
    public String cnic;
    public String address;
    public String status;
    public String updated;
    public String created;
    public String latitude;
    public String longitude;
    public String device_id;

    public String getFullName()
    {
        return this.first_name+" "+this.last_name;
    }
}
