package asee.giiis.unex.es.mysporttraining.Adapters;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import asee.giiis.unex.es.mysporttraining.R;

public class WeightControlAdapter extends RecyclerView.Adapter<WeightControlAdapter.ViewHolder>{

    Context mContext;
    List<Map<String, String>> mWeightControl= new ArrayList<>();


    // Provide a suitable constructor (depends on the kind of dataset)
    public WeightControlAdapter(Context context, List<Map<String, String>> weightControl){
        this.mWeightControl = weightControl;
        this.mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WeightControlAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weight_control, parent, false);
        return new WeightControlAdapter.ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WeightControlAdapter.ViewHolder holder, int position) {
        holder.bind(mWeightControl.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mWeightControl.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView date;
        TextView weight;


        public ViewHolder(View itemView) {
            super(itemView);
            // Get the references to every widget of the User View
            date = (TextView) itemView.findViewById(R.id.rv_weight_control_date);
            weight = (TextView) itemView.findViewById(R.id.rv_weight_control_weight);

        }

        public void bind (Map<String, String> weightControl){
            // Display each item in layout
            date.setText("Fecha: " + weightControl.get("date"));
            weight.setText("Peso: " + weightControl.get("weight") + " kg");

        }
    }

}

