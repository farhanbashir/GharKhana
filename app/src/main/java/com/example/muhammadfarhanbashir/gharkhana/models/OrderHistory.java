package com.example.muhammadfarhanbashir.gharkhana.models;

/**
 * Created by muhammadfarhanbashir on 07/03/2017.
 */

public class OrderHistory {
    public String order_id;
    public String chef_id;
    public String customer_id;
    public String item_id;
    public String status;
    public String created;
    public String updated;
    public String quantity;
    public String chef_first_name;
    public String chef_last_name;
    public String chef_address;
    public String customer_address;
    public String customer_first_name;
    public String customer_last_name;
    public String item_name;
    public String price;

    public String getName()
    {
        return this.item_name;
    }

    public String getPrice()
    {
        return this.price;
    }

    public String getQuantity()
    {
        return this.quantity;
    }

    public String getCustomerName()
    {
        return this.customer_first_name+" "+this.customer_last_name;
    }
}
