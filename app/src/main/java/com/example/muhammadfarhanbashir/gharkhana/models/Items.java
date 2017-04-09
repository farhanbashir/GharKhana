package com.example.muhammadfarhanbashir.gharkhana.models;

/**
 * Created by muhammadfarhanbashir on 02/03/2017.
 */

public class Items {
    public String item_id;
    public String category_id;
    public String chef_id;
    public String item_name;
    public String description;
    public String image;
    public String created;
    public String updated;
    public String price;
    public String quantity;
    public String chef_first_name;
    public String chef_last_name;
    public String chef_contact_number;
    public String latitude;
    public String longitude;

    public String getChef_id()
    {
        return this.chef_id;
    }

    public String getItem_id()
    {
        return this.item_id;
    }

    public String getItem_name()
    {
        return this.item_name;
    }

    public String getDescription()
    {
        return this.description;
    }

    public String getImage()
    {
        return this.image;
    }

    public String getPrice()
    {
        return this.price;
    }

    public String getQuantity()
    {
        return this.quantity;
    }

    public String getChef_first_name()
    {
        return this.chef_first_name;
    }

    public String getChef_last_name()
    {
        return this.chef_last_name;
    }

    public String getChef_contact_number()
    {
        return this.chef_contact_number;
    }

    public String getLatitude()
    {
        return this.latitude;
    }

    public String getLongitude()
    {
        return this.longitude;
    }
}
