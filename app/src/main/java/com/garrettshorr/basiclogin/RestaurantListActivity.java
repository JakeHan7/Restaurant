package com.garrettshorr.basiclogin;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {
    private ListView listViewRestaurant;
    private FloatingActionButton floatingActionButtonAddRestaurant;

    public static final String EXTRA_RESTAURANT = "restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        wireWidgets();

        registerForContextMenu(listViewRestaurant);

        floatingActionButtonAddRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNewRestaurant = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                startActivity(addNewRestaurant);
                finish();
            }
        });

        populateListView();

    }

    private void populateListView(){

        //refactor to only get the items that belong to the user
        // get the current user's objectId (hint: use Backendless,UserSErvice
        // make a data query and use the advanced object retrieval pattern
        // to find all restaurants whose ownerId matches the user's object Id
        // sample WHERE clause with a string:name = 'Joe'


        String ownerId = Backendless.UserService.CurrentUser().getObjectId();
        String whereClause = "ownerId = '" + ownerId + "'";
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(whereClause);


        Backendless.Data.of(Restaurant.class).find(queryBuilder, new AsyncCallback<List<Restaurant>>() {
            @Override
            public void handleResponse(final List<Restaurant> restaurantList) {

                Log.d("LISTACTIVITY", "handleResponse: " + restaurantList.toString());

                RestaurantAdapter adapter = new RestaurantAdapter(
                        RestaurantListActivity.this,
                        R.layout.item_restaurantlist,
                        restaurantList);
                listViewRestaurant.setAdapter(adapter);
                //set the onItemClickListener to open the Restaurant Activity
                listViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent restaurantDetailIntent = new Intent(RestaurantListActivity.this, RestaurantActivity.class);
                        restaurantDetailIntent. putExtra(EXTRA_RESTAURANT, restaurantList.get(position));
                        startActivity(restaurantDetailIntent);
                        deleteRestaurant(restaurantList.get(position));
                        finish();


//                ArrayAdapter<Restaurant> adapter = new RestaurantAdapter(RestaurantListActivity.this,android.R.layout.simple_list_item_1, response);
//            listViewRestaurant.setAdapter(adapter);
//            //set the onItemClickListener to open the Restaurant Activity
//                // take the clicked object and include in the intent
//                // in the RestaurantActivity's onCreate, check if there is a Parcelabler extra
//                // if there is, then get the Restaurant object and populate the fields
//                listViewRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Intent restaurantDetailIntent = new Intent (RestaurantListActivity.this, RestaurantActivity.class);
//                        restaurantDetailIntent. putExtra(EXTRA_RESTAURANT, response.get(position));
//                        startActivity(restaurantDetailIntent);
                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,v,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.regmenu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        //find out which menu_delete item was pressed
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.option1:
                Restaurant restaurant = (Restaurant) listViewRestaurant.getItemAtPosition(info.position);
                deleteRestaurant(restaurant);
                return true;
            default:
                return false;
        }
    }


    private void deleteRestaurant(Restaurant restaurant) {
        Backendless.Persistence.of(Restaurant.class ).remove(restaurant, new AsyncCallback<Long>()
        {
            public void handleResponse( Long response )
            {
                // Contact has been deleted. The response is the
                // time in milliseconds when the object was deleted
                populateListView();
            }
            public void handleFault( BackendlessFault fault )
            {
                // an error has occurred, the error code can be
                // retrieved with fault.getCode()
                Toast.makeText(RestaurantListActivity.this, fault.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } );
    }


    private void wireWidgets() {
        listViewRestaurant = findViewById(R.id.Listview_restaurant);
        floatingActionButtonAddRestaurant = findViewById(R.id.floatingActionButton);

    }
}
