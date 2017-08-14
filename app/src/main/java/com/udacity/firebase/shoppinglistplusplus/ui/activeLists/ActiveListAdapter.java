package com.udacity.firebase.shoppinglistplusplus.ui.activeLists;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.udacity.firebase.shoppinglistplusplus.R;
import com.udacity.firebase.shoppinglistplusplus.model.ShoppingList;
import com.udacity.firebase.shoppinglistplusplus.utils.Utils;


/**
 * Created by Elias Myronidis on 5/8/17.
 */

/**
 * Populates the list_view_active_lists inside ShoppingListsFragment
 */
public class ActiveListAdapter extends FirebaseListAdapter<ShoppingList> {

    private Activity mActivity;

    /**
     * Public constructor that initializes private instance variables when adapter is created
     */
    public ActiveListAdapter(Activity activity, Class<ShoppingList> modelClass, int modelLayout,
                             Query ref) {
        super(activity, modelClass, modelLayout, ref);
        this.mActivity = activity;
    }

    /**
     * Protected method that populates the view attached to the adapter (list_view_active_lists)
     * with items inflated from single_active_list.xml
     * populateView also handles data changes and updates the listView accordingly
     */
    @Override
    protected void populateView(View v, ShoppingList model, int position) {
        // TODO This is where you need to populate the single_active_list layout with
        // the data in the current shopping list. It should be similar to what you
        // were displaying in ShoppingListsFragment

        TextView tvListName = (TextView) v.findViewById(R.id.text_view_list_name);
        TextView tvCreatedByUser = (TextView) v.findViewById(R.id.text_view_created_by_user);
        TextView tvEditTime = (TextView) v.findViewById(R.id.text_view_edit_time);

        tvListName.setText(model.getListName());
        tvCreatedByUser.setText(model.getOwner());
        tvEditTime.setText(Utils.SIMPLE_DATE_FORMAT.format(model.getTimestampLastChangedLong()));

    }
}

