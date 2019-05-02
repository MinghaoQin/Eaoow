package com.minghaoqin.q.eaoow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser
{
    private HashMap<String, String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String, String> googlePlaceMap = new HashMap<>();
        String NameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String price_level ="";
        String rating = "";

        try
        {
            if (!googlePlaceJSON.isNull("name")) NameOfPlace = googlePlaceJSON.getString("name");

            if (!googlePlaceJSON.isNull("vicinity")) vicinity = googlePlaceJSON.getString("vicinity");

            if (!googlePlaceJSON.isNull("price_level")) price_level = googlePlaceJSON.getString("price_level");

            if (!googlePlaceJSON.isNull("rating"))  rating = googlePlaceJSON.getString("rating");



            googlePlaceMap.put("place_name", NameOfPlace); // for displaying name of rcomdRestaurant
            googlePlaceMap.put("vicinity", vicinity); // possible extra feature - for "displaying address only" function

            googlePlaceMap.put("price_level",price_level); // one of food preference constraints
            googlePlaceMap.put("rating",rating); // one of food preference constraints

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return googlePlaceMap;
    }


    // for reference for the method parse
    private List<HashMap<String, String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();


        // 这是第一次出现list hashmap
        List<HashMap<String, String>> NearbyPlacesList = new ArrayList<>();


        HashMap<String, String> NearbyPlaceInfo = null;

        for (int i=0; i<counter; i++)
        {
            try
            {
                NearbyPlaceInfo = getSingleNearbyPlace( (JSONObject) jsonArray.get(i) );
                NearbyPlacesList.add(NearbyPlaceInfo);

            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return NearbyPlacesList;
    }


    //for call
    public List<HashMap<String, String>> parse(String jSONdata)
    {
        JSONArray jsonArrayParse = null;
        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(jSONdata);
            jsonArrayParse = jsonObject.getJSONArray("results");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return getAllNearbyPlaces(jsonArrayParse);
    }
}