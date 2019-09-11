package com.software.dk198.expensetracker.DataBackup;

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
import com.software.dk198.expensetracker.Targets.ShowTargetActivity;
import com.software.dk198.expensetracker.Targets.Target;

import java.util.List;

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class TargetForImportAdapter extends RecyclerView.Adapter<TargetForImportAdapter.ViewHolder> {
    private static final String TAG = "TargetForImportAdapter";

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
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
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            this.context = context;
            // Attach a click listener to the entire row view
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition(); // gets item position

//            Intent intent = new Intent(context, ShowTargetActivity.class);
//            intent.putExtra("targetName", nameTextView.getText());
//            intent.putExtra("targetId", target_id);
//            context.startActivity(intent);
        }

    }

    private List<Target> mTargets;

    // Pass in the target array into the constructor
    public TargetForImportAdapter(List<Target> targets) {
        mTargets = targets;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public TargetForImportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
//        View targetView = inflater.inflate(R.layout.item_target, parent, false);
        View targetView = inflater.inflate(R.layout.item_target_import_cardview, parent, false);

        // Return a new holder instance
        TargetForImportAdapter.ViewHolder viewHolder = new TargetForImportAdapter.ViewHolder(context, targetView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final TargetForImportAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        final Target target = mTargets.get(position);

        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(target.getName());
        viewHolder.target_id = target.getId();

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mTargets.size();
    }

}