package com.garrettshorr.basiclogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;


import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.ArrayList;
import java.util.List;


public class RestaurantActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextCuisine;
    private EditText editTextAddress;
    private RatingBar ratingBarRating;
    private SeekBar seekBarPrice;
    private Button buttonSave;

    private Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        wireWidgets();

        prefillFields();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_restaurantActivity_save:
                        saveNewRestaurant();
                        Intent intent = new Intent(RestaurantActivity.this, RestaurantListActivity.class);
                        startActivity(intent);
                }
            }
        });

    }


    private void prefillFields(){
        //check if there's a Restaurant in the intent
        //if so, fill all the fields with the restaurant
        Intent restaurantIntent = getIntent();
        Restaurant restaurant = restaurantIntent.getParcelableExtra(RestaurantListActivity.EXTRA_RESTAURANT);
        if(restaurant != null) {
            editTextName.setText((restaurant.getName()));
            editTextAddress.setText(restaurant.getAddress());
            editTextCuisine.setText(restaurant.getCuisine());
            ratingBarRating.setRating((float) restaurant.getRating());
            seekBarPrice.setProgress(restaurant.getPrice());


        }
    }

    private void saveNewRestaurant() {
        String restaurantName = editTextName.getText().toString();
        String restaurantCuisine = editTextCuisine.getText().toString();
        String restaurantAddress = editTextAddress.getText().toString();
        double restaurantRating = ratingBarRating.getRating();
        int restaurantPrice = seekBarPrice.getProgress();


        if (restaurant == null) {
            restaurant = new Restaurant();
        }
        restaurant.setName(restaurantName);
        restaurant.setCuisine(restaurantCuisine);
        restaurant.setAddress(restaurantAddress);
        restaurant.setRating(restaurantRating);
        restaurant.setPrice(restaurantPrice);

        // save object asynchronously
        Backendless.Persistence.save(restaurant, new AsyncCallback<Restaurant>() {
            @Override
            public void handleResponse( Restaurant response )
            {
                // new Contact instance has been saved
                Toast.makeText(RestaurantActivity.this, response.getName() + " Sucessfully Added", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be retrieved with fault.getCode()
                Toast.makeText(RestaurantActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void wireWidgets() {
        editTextName = findViewById(R.id.editText_restaurantActivity_name);
        editTextCuisine = findViewById(R.id.editText_restaurantActivity_cuisine);
        editTextAddress = findViewById(R.id.editText_restaurantActivity_address);
        ratingBarRating = findViewById(R.id.ratingBar_restaurantActivity_rating);
        buttonSave = findViewById(R.id.button_restaurantActivity_save);
        seekBarPrice = findViewById(R.id.seekBar_seekBarPrice);



    }



}