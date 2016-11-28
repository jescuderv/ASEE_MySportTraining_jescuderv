package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.ganfra.materialspinner.MaterialSpinner;

public class RegisterActivity extends AppCompatActivity {

    static private final int REQUEST_IMAGE_GET = 1;

    // Reference root JSON database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef = mRootRef.child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Focus for input disabled
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Button registerButton = (Button) findViewById(R.id.reg_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginActivity();
            }
        });

        CircleImageView imageProfile = (CircleImageView) findViewById(R.id.reg_usr_profile_image);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGetImageProfile();
            }
        });

        // Adapter for spinner sex (take array from resources)
        ArrayAdapter<String> adapterSex = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sex_array));
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinnerSex = (MaterialSpinner) findViewById(R.id.reg_spinner_sex);
        spinnerSex.setAdapter(adapterSex);
        spinnerSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Adapter for spinner weight condition (take array from resources)
        ArrayAdapter<String> adapterWeightCond = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.weight_condition_array));
        adapterWeightCond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinnerWeightCond = (MaterialSpinner) findViewById(R.id.reg_spinner_weight_conditino);
        spinnerWeightCond.setAdapter(adapterWeightCond);
        spinnerWeightCond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void startLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void startGetImageProfile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK){
            Uri uri = data.getData();
            try{
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);

                CircleImageView imageProfile = (CircleImageView) findViewById(R.id.reg_usr_profile_image);
                imageProfile.setImageBitmap(bitmap);
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }


}
