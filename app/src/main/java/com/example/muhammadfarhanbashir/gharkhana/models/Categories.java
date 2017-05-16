package com.example.muhammadfarhanbashir.gharkhana.models;

import android.content.Context;
import android.util.Log;

import com.example.muhammadfarhanbashir.gharkhana.R;
import com.example.muhammadfarhanbashir.gharkhana.helpers.SharedPreference;
import com.example.muhammadfarhanbashir.gharkhana.models.categories.CategoriesBasicClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import static android.R.attr.category;

/**
 * Created by muhammadfarhanbashir on 26/04/2017.
 */

public class Categories {

    public ArrayList<CategoriesBasicClass> all_categories;
    public ArrayList<CategoriesBasicClass> all_parents;

    public Categories()
    {
        final Gson gson = new Gson();
        String categories_string = "[{\"category_id\":\"1\",\"name\":\"Pakistani Khanay\",\"parent_id\":\"0\"},{\"category_id\":\"2\",\"name\":\"Biryani\",\"parent_id\":\"1\"},{\"category_id\":\"3\",\"name\":\"Qorma\",\"parent_id\":\"1\"},{\"category_id\":\"4\",\"name\":\"Karahi\",\"parent_id\":\"1\"},{\"category_id\":\"5\",\"name\":\"Handi\",\"parent_id\":\"1\"},{\"category_id\":\"6\",\"name\":\"Nihari\",\"parent_id\":\"1\"},{\"category_id\":\"7\",\"name\":\"Pulao\",\"parent_id\":\"1\"},{\"category_id\":\"8\",\"name\":\"Shami Kebab\",\"parent_id\":\"1\"},{\"category_id\":\"9\",\"name\":\"Keema\",\"parent_id\":\"1\"},{\"category_id\":\"10\",\"name\":\"Salan / Curry\",\"parent_id\":\"1\"},{\"category_id\":\"11\",\"name\":\"Other\",\"parent_id\":\"1\"},{\"category_id\":\"12\",\"name\":\"Chinese Khanay\",\"parent_id\":\"0\"},{\"category_id\":\"13\",\"name\":\"Manchorian\",\"parent_id\":\"12\"},{\"category_id\":\"14\",\"name\":\"Shashlik\",\"parent_id\":\"12\"},{\"category_id\":\"15\",\"name\":\"Chilli Dry\",\"parent_id\":\"12\"},{\"category_id\":\"16\",\"name\":\"Chowmein\",\"parent_id\":\"12\"},{\"category_id\":\"17\",\"name\":\"Black Pepper\",\"parent_id\":\"12\"},{\"category_id\":\"18\",\"name\":\"Fried Rice\",\"parent_id\":\"12\"},{\"category_id\":\"19\",\"name\":\"Soup\",\"parent_id\":\"12\"},{\"category_id\":\"20\",\"name\":\"Noodles\",\"parent_id\":\"12\"},{\"category_id\":\"21\",\"name\":\"Other\",\"parent_id\":\"12\"},{\"category_id\":\"22\",\"name\":\"B.B.Q.\",\"parent_id\":\"0\"},{\"category_id\":\"23\",\"name\":\"Tikka\",\"parent_id\":\"22\"},{\"category_id\":\"24\",\"name\":\"Dhaga Kebab\",\"parent_id\":\"22\"},{\"category_id\":\"25\",\"name\":\"Gola Kebab\",\"parent_id\":\"22\"},{\"category_id\":\"26\",\"name\":\"Seekh Kebab\",\"parent_id\":\"22\"},{\"category_id\":\"27\",\"name\":\"Fry Kebab\",\"parent_id\":\"22\"},{\"category_id\":\"28\",\"name\":\"Boti\",\"parent_id\":\"22\"},{\"category_id\":\"29\",\"name\":\"Bihari\",\"parent_id\":\"22\"},{\"category_id\":\"30\",\"name\":\"Broast\",\"parent_id\":\"22\"},{\"category_id\":\"31\",\"name\":\"Other\",\"parent_id\":\"22\"},{\"category_id\":\"32\",\"name\":\"Seafood\",\"parent_id\":\"0\"},{\"category_id\":\"33\",\"name\":\"Fish Fillet\",\"parent_id\":\"32\"},{\"category_id\":\"34\",\"name\":\"Finger Fish\",\"parent_id\":\"32\"},{\"category_id\":\"35\",\"name\":\"Fish Kebab\",\"parent_id\":\"32\"},{\"category_id\":\"36\",\"name\":\"Prawns\",\"parent_id\":\"32\"},{\"category_id\":\"37\",\"name\":\"Shrimp\",\"parent_id\":\"32\"},{\"category_id\":\"38\",\"name\":\"Fish Biryani\",\"parent_id\":\"32\"},{\"category_id\":\"39\",\"name\":\"Other\",\"parent_id\":\"32\"},{\"category_id\":\"40\",\"name\":\"Desserts\",\"parent_id\":\"0\"},{\"category_id\":\"41\",\"name\":\"Kheer\",\"parent_id\":\"40\"},{\"category_id\":\"42\",\"name\":\"Halwa\",\"parent_id\":\"40\"},{\"category_id\":\"43\",\"name\":\"Meetha\",\"parent_id\":\"40\"},{\"category_id\":\"44\",\"name\":\"Gulab Jaman\",\"parent_id\":\"40\"},{\"category_id\":\"45\",\"name\":\"Cake\",\"parent_id\":\"40\"},{\"category_id\":\"46\",\"name\":\"Other\",\"parent_id\":\"40\"},{\"category_id\":\"47\",\"name\":\"Others\",\"parent_id\":\"0\"},{\"category_id\":\"48\",\"name\":\"Others\",\"parent_id\":\"47\"}]";
        try
        {
            this.all_categories = gson.fromJson(categories_string, new TypeToken<ArrayList<CategoriesBasicClass>>(){}.getType());
        }
        catch(Exception e)
        {
            Log.d("json error", e.getMessage());
        }


    }

    public ArrayList<CategoriesBasicClass> getAllParents()
    {
        for(int i=0; i<all_categories.size(); i++)
        {
            if(all_categories.get(i).parent_id.equals("0"))
            {
                this.all_parents.add(all_categories.get(i));
            }
        }

        return this.all_parents;
    }

    public ArrayList<CategoriesBasicClass> getAllChildren(String parent_id)
    {
        ArrayList<CategoriesBasicClass> children = new ArrayList<CategoriesBasicClass>();

        for(int i=0; i<this.all_categories.size(); i++)
        {
            if(this.all_categories.get(i).parent_id.equals(parent_id))
            {
                children.add(this.all_categories.get(i));
            }
        }

        return children;
    }

    public String getCategoryName(String category_id)
    {
        String name = "";
        for(int i=0; i < this.all_categories.size(); i++)
        {
            if(this.all_categories.get(i).category_id.equals(category_id))
            {
                name = this.all_categories.get(i).getName();
                break;
            }
        }

        return name;
    }
}
