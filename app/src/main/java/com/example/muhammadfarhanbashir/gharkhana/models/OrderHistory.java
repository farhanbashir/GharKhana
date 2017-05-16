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
    public String intimate_order;
    public String address;
    public String category_id;
    public String parent_category_id;

    public String getName()
    {
        String category_name = "";

        Categories categories = new Categories();
        category_name = categories.getCategoryName(this.category_id);
        if(category_name.toLowerCase().equals("others") || category_name.toLowerCase().equals("other"))
        {
            category_name = "";
        }
        return this.item_name+" "+category_name;
    }

    public String getChefName(){return this.chef_first_name+" "+this.chef_last_name;}

    public String getPrice()
    {
        return this.price;
    }

    public String getPriceForConsumer()
    {
        float price = Float.valueOf(this.price);
        price = price + 50;

        return String.valueOf(Math.round(price));
    }

    public String getQuantity()
    {
        return this.quantity;
    }

    public String getOrderDate() {return this.created;}

    public String getCustomerName()
    {
        return this.customer_first_name+" "+this.customer_last_name;
    }
}
