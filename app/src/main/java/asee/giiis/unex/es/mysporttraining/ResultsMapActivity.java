package asee.giiis.unex.es.mysporttraining;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class ResultsMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
    }
}
