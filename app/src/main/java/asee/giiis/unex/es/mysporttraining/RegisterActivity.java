package asee.giiis.unex.es.mysporttraining;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import asee.giiis.unex.es.mysporttraining.Objects.User;
import de.hdodenhof.circleimageview.CircleImageView;
import fr.ganfra.materialspinner.MaterialSpinner;

public class RegisterActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1;
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$";
    private static final String NAME_PATTERN = "[^0-9]+";


    // FirebaseAuth object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    // Our own user
    private User mUser;
    private String mCond, mSex; // Variable for spinner


    // Reference root JSON database
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mUsersRef = mRootRef.child("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Focus for input disabled
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // ===== IMAGE PROFILE ==== //

        CircleImageView imageProfile = (CircleImageView) findViewById(R.id.reg_usr_profile_image);
        imageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGetImageProfile();
            }
        });


        // ===== SPINNERS ==== //

        // Adapter for spinner sex (take array from resources)
        ArrayAdapter<String> adapterSex = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.sex_array));
        adapterSex.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinnerSex = (MaterialSpinner) findViewById(R.id.reg_spinner_sex);
        spinnerSex.setAdapter(adapterSex);


        // Adapter for spinner weight condition (take array from resources)
        ArrayAdapter<String> adapterWeightCond = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.weight_condition_array));
        adapterWeightCond.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MaterialSpinner spinnerWeightCond = (MaterialSpinner) findViewById(R.id.reg_spinner_weight_condition);
        spinnerWeightCond.setAdapter(adapterWeightCond);

        spinnerWeightCond.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCond = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Faltan algunos campos por rellenar", Toast.LENGTH_SHORT).show();
            }
        });
        spinnerSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSex = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(parent.getContext(), "Faltan algunos campos por rellenar", Toast.LENGTH_SHORT).show();
            }
        });



        // ===== REGISTER BUTTON ==== //

        Button registerButton = (Button) findViewById(R.id.reg_register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            registerUser();
            }
        });
    }


    private void startMainActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }



    //========================================//
        // REGISTER AN NEW USER IN FIREBASE //
    //========================================//

    private void registerUser(){
        boolean error = false;

        // Get from Activity input
        TextInputLayout username = (TextInputLayout) findViewById(R.id.reg_input_layout_username);
        final String usrUsername = username.getEditText().getText().toString();

        TextInputLayout firstname = (TextInputLayout) findViewById(R.id.reg_input_layout_first_name);
        final String usrFirstname = firstname.getEditText().getText().toString();

        TextInputLayout lastname = (TextInputLayout) findViewById(R.id.reg_input_layout_last_name);
        final String usrLastname = lastname.getEditText().getText().toString();

        TextInputLayout email = (TextInputLayout) findViewById(R.id.reg_input_layout_email);
        final String usrEmail = email.getEditText().getText().toString();

        TextInputLayout password = (TextInputLayout) findViewById(R.id.reg_input_layout_password);
        final String usrPasswrod = password.getEditText().getText().toString();

        TextInputLayout height = (TextInputLayout) findViewById(R.id.reg_input_layout_height);
        final String usrHeight = height.getEditText().getText().toString();

        TextInputLayout weight = (TextInputLayout) findViewById(R.id.reg_input_layout_weight);
        final String usrWeight = weight.getEditText().getText().toString();

        TextInputLayout age = (TextInputLayout) findViewById(R.id.reg_input_layout_age);
        final String usrAge = age.getEditText().getText().toString();


        // Check null
        if (usrUsername.isEmpty() || usrFirstname.isEmpty() || usrLastname.isEmpty() ||
                usrEmail.isEmpty() || usrPasswrod.isEmpty() || usrHeight.isEmpty()
                || usrWeight.isEmpty() || usrAge.isEmpty() || mSex.equals("Sexo") || mCond.equals("Condición física")){
            error = true;
            Toast.makeText(this, "Faltan algunos campos por rellenar", Toast.LENGTH_SHORT).show();
        }

        // Check syntax
        if (!error){
            error = false;
            // username
            if (!checkLenght(usrUsername)){
                username.setErrorEnabled(true);
                username.setError("Introduce un nombre de usuario de al menos 6 caracteres");
                error = true;
            } else { username.setErrorEnabled(false); }
            // first name
            if (!checkName(usrFirstname)) {
                firstname.setErrorEnabled(true);
                firstname.setError("Introduce un nombre válido");
                error = true;
            } else { firstname.setErrorEnabled(false); }
            // last name
            if(!checkName(usrLastname)){
                lastname.setErrorEnabled(true);
                lastname.setError("Introduce un apellido válido");
                error = true;
            } else { lastname.setErrorEnabled(false); }
            // email
            if (!checkEmail(usrEmail)){
                email.setErrorEnabled(true);
                email.setError("Introduce un email válido");
                error = true;
            } else { email.setErrorEnabled(false); }
            // password
            if (!checkLenght(usrPasswrod)){
                password.setErrorEnabled(true);
                password.setError("Introduce al menos 5 caracteres");
                error = true;
            } else { password.setErrorEnabled(false); }
            // height
            if (!checkHeight(usrHeight)){
                height.setErrorEnabled(true);
                height.setError("Introduce un valor entre 100 cm y 220 cm");
                error = true;
            } else { height.setErrorEnabled(false); }
            // weight
            if (!checkWeight(usrWeight)) {
                weight.setErrorEnabled(true);
                weight.setError("Introduce un valor válido para el peso");
                error = true;
            } else { weight.setErrorEnabled(false); }
            // age
            if (!checkAge(usrAge)){
                age.setErrorEnabled(true);
                age.setError("Introduce un valor válido para la edad");
                error = true;
            } else { age.setErrorEnabled(false); }
        }

        // Create a new user with data input
        if (!error) {
            // First, register email and password if there aren't errors
            mFirebaseAuth.createUserWithEmailAndPassword(usrEmail, usrPasswrod)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If don't exit email in database previously
                            if (task.isSuccessful()) {
                                // SignIn with new email and password user
                                mFirebaseAuth.signInWithEmailAndPassword(usrEmail, usrPasswrod)
                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                if (task.isSuccessful()) {
                                                    // Set values for new user register
                                                    mUser = new User(Integer.parseInt(usrAge), usrEmail, usrFirstname,
                                                            Integer.parseInt(usrHeight), usrLastname,
                                                            usrPasswrod, 0, usrUsername, Integer.parseInt(usrWeight));
                                                    mUser.setSex(mSex);
                                                    mUser.setPhysicalCondition(mCond);

                                                    // Get FirebaseUser reference (just created)
                                                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                                    // Insert into Firebase databases: /root/users/"new user"
                                                    if (user != null) {
                                                        mUsersRef.child(user.getUid()).setValue(mUser);
                                                    }
                                                    // Start MainActivity
                                                    startMainActivity();
                                                }
                                            }
                                        });
                            } // Else if email exist in dtabase
                            else {
                                Toast.makeText(RegisterActivity.this, "Usuario ya existente",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }


    //========================================//
                // CHECKING RULES //
    //========================================//

    // Check Length for username and password
    private boolean checkLenght(String username){
        return username.length() > 5;
    }

    // Check name for firstname and lastname
    private boolean checkName(String name){
        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // Check email
    private boolean checkEmail(String email){
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher;
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Check height
    private boolean checkHeight(String height){
        Integer heightAux = Integer.parseInt(height);
        return heightAux < 220 && heightAux > 100;
    }

    // check weight
    private boolean checkWeight(String weight){
        Integer weightAux = Integer.parseInt(weight);
        return weightAux < 300 && weightAux > 25;
    }

    // check age
    private boolean checkAge(String age){
        Integer ageAux = Integer.parseInt(age);
        return ageAux < 120 && ageAux > 0;
    }



    //========================================//
            // SET IMAGE PROFILE //
    //========================================//

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
