package com.udacity.firebase.shoppinglistplusplus.ui.activeListDetails;

/**
 * Created by Elias Myronidis on 2/8/17.
 */

import android.app.Dialog;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.udacity.firebase.shoppinglistplusplus.R;
import com.udacity.firebase.shoppinglistplusplus.model.ShoppingList;
import com.udacity.firebase.shoppinglistplusplus.utils.Constants;

import java.util.HashMap;

/**
 * Lets user edit list item name for all copies of the current list
 */
public class EditListItemNameDialogFragment extends EditListDialogFragment {

    private String mListId;
    private String mItemId;
    private String mItemName;
    /**
     * Public static constructor that creates fragment and passes a bundle with data into it when adapter is created
     */
    public static EditListItemNameDialogFragment newInstance(ShoppingList shoppingList, String listId, String itemName, String itemId) {
        EditListItemNameDialogFragment editListItemNameDialogFragment = new EditListItemNameDialogFragment();

        Bundle bundle = EditListDialogFragment.newInstanceHelper(shoppingList, R.layout.dialog_edit_item);
        bundle.putString(Constants.KEY_LIST_ID, listId);
        bundle.putString(Constants.KEY_ITEM_ID, itemId);
        bundle.putString(Constants.KEY_ITEM_NAME, itemName);
        editListItemNameDialogFragment.setArguments(bundle);

        return editListItemNameDialogFragment;
    }

    /**
     * Initialize instance variables with data from bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListId = getArguments().getString(Constants.KEY_LIST_ID);
        mItemId = getArguments().getString(Constants.KEY_ITEM_ID);
        mItemName = getArguments().getString(Constants.KEY_ITEM_NAME);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /** {@link EditListDialogFragment#createDialogHelper(int)} is a
         * superclass method that creates the dialog
         */
        Dialog dialog = super.createDialogHelper(R.string.positive_button_edit_item);
        helpSetDefaultValueEditText(mItemName);
        return dialog;
    }

    /**
     * Change selected list item name to the editText input if it is not empty
     */
    protected void doListEdit() {
        String inputItemName = mEditTextForList.getText().toString();

        if (!inputItemName.equals("")){
            if (mItemName != null) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                HashMap<String, Object> updatedItemMap = new HashMap<>();
                updatedItemMap.put("/" + Constants.FIREBASE_LOCATION_SHOPPING_LIST_ITEMS + "/"
                        + mListId + "/" + mItemId + "/" + Constants.FIREBASE_PROPERTY_ITEM_NAME, inputItemName);

                HashMap<String, Object> updatedTimestampMap = new HashMap<>();
                updatedTimestampMap.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                updatedItemMap.put("/" + Constants.FIREBASE_LOCATION_ACTIVE_LISTS + "/"
                + mListId + "/" + Constants.FIREBASE_PROPERTY_TIMESTAMP_LAST_CHANGED, updatedTimestampMap);

                ref.updateChildren(updatedItemMap);

            }
        }
    }
}
