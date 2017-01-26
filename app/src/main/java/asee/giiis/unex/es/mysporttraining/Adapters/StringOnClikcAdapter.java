package asee.giiis.unex.es.mysporttraining.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import asee.giiis.unex.es.mysporttraining.R;


public class StringOnClikcAdapter extends RecyclerView.Adapter<StringOnClikcAdapter.ViewHolder> {
    Context mContext;
    List<String> mActivitiesList = new ArrayList<String>();

    public interface OnItemClickListener {
        void onItemClick(String item); //Type of the element to be returned
    }

    private final OnItemClickListener listener;


    // Provide a suitable constructor (depends on the kind of dataset)
    public StringOnClikcAdapter(Context context, List<String> activitiesList, OnItemClickListener listener){
        this.mActivitiesList = activitiesList;
        this.mContext = context;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_training_category, parent, false);
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mActivitiesList.get(position), listener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mActivitiesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            // Get the references to every widget of the User View
            mName = (TextView) itemView.findViewById(R.id.rv_training_category_name);
        }

        public void bind (final String string, final OnItemClickListener listener){
            // Display each item in layout
            mName.setText(string);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(string);
                }
            });

        }
    }

}
