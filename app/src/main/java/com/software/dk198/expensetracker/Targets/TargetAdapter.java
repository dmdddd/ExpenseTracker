package com.software.dk198.expensetracker.Targets;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.software.dk198.expensetracker.DBHelper;
import com.software.dk198.expensetracker.MainActivity;
import com.software.dk198.expensetracker.R;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TargetAdapter extends RecyclerView.Adapter<TargetAdapter.ViewHolder> {
    private static final String TAG = "TargetAdapter";
    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView buttonViewOption;
        public TextView nameTextView;
        private Context context;
        private CardView cardView;
        private String m_Text = "";
        int target_id;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(Context context, View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.targetName);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position

//            Intent intent = new Intent(context, TargetShowingActivity.class);
            Intent intent = new Intent(context, ShowTargetActivity.class);
            intent.putExtra("targetName", nameTextView.getText());
            intent.putExtra("targetId", target_id);
            context.startActivity(intent);
//            }
        }

    }

    private List<Target> mTargets;

    // Pass in the target array into the constructor
    public TargetAdapter(List<Target> targets) {
        mTargets = targets;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TargetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
//        View targetView = inflater.inflate(R.layout.item_target, parent, false);
        View targetView = inflater.inflate(R.layout.item_target_cardview, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(context, targetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final TargetAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Target target = mTargets.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(target.getName());
        viewHolder.target_id = target.getId();


        viewHolder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creating a popup menu
                PopupMenu popup = new PopupMenu(viewHolder.context, viewHolder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.target_menu_in_cardview);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.rename:
                                update_target_name(viewHolder.context, target.getName(), target.getId());

                                break;
                            case R.id.remove:
//                                Toast.makeText(viewHolder.context, "remove "+target.getId(), Toast.LENGTH_LONG).show();
                                remove_target(viewHolder.context, target.getId());
                                break;

                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTargets.size();
    }


    private void update_target_name(Context context, String current_name, int target_id){
        final Context given_context = context;
        final int given_target_id = target_id;
        AlertDialog.Builder builder = new AlertDialog.Builder(given_context);
        builder.setTitle(given_context.getString(R.string.new_name));
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(given_context).inflate(R.layout.rename_target_popup, null);
        // Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.newNamePlainText);
        input.setText(current_name);
        input.setSelectAllOnFocus(true);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);
        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                DBHelper database = new DBHelper(given_context);
                Target target = database.getTargetById(given_target_id);
                String given_text = input.getText().toString();
                if(given_text.length()>0){ //Changing Target's name
                    target.setName(given_text);
                    // Update target's entry in the database
                    database.updateTarget(target);
                    int index = 0;
                    for (Target target_in_main : MainActivity.targets){
                        if (target_in_main.getId() == target.getId())
                            break;
                        index++;
                    }
                    MainActivity.targets.get(index).setName(target.getName());
                    MainActivity.adapter.notifyItemChanged(index);
//                                            CategoriesShowingFragment.targetName.setText(target.getName());
                }//Else: Not changing Target's name
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void remove_target(Context context, int target_id){
        final Context given_context = context;
        final int given_target_id = target_id;
        Log.d(TAG, "Deleting target From MainActivity");

        // add an "Are you sure?" popup window
        AlertDialog.Builder builder = new AlertDialog.Builder(given_context);
        builder.setCancelable(true);
        builder.setTitle(context.getResources().getString(R.string.remove)+"?");    // Remove?
        builder.setMessage(context.getResources().getString(R.string.target_deleting_warrning));
        builder.setPositiveButton(context.getResources().getString(R.string.confirm),   //Confirm
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // removes the target from the database
                        DBHelper database = new DBHelper(given_context);
                        database.deleteTargetGivenTarget(database.getTargetById(given_target_id));
                        int index = 0;
                        for (Target target_in_list : MainActivity.targets){
                            if(target_in_list.getId() == given_target_id) {
                                break;
                            }
                            index++;
                        }
                        MainActivity.targets.remove(index);
                        MainActivity.adapter.notifyItemRemoved(index);
                        MainActivity.adapter.notifyItemRangeChanged(index, MainActivity.targets.size());
                        MainActivity.targetsView.scrollToPosition(0);

                        // Show the no subjects to display if the last one has been deleted
                        if (MainActivity.targets.size() == 0){
                            MainActivity.noSubjectsTextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}