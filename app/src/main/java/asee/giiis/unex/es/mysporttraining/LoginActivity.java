package asee.giiis.unex.es.mysporttraining;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Focus for input disabled
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        // If currentUser is not null means user is already logged in
        if (mFirebaseAuth.getCurrentUser() != null){
            // Close activity
            finish();
            // Opening MainActivity
            startMainActivity();
        }


        Button loginButton = (Button) findViewById(R.id.log_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        Button registeButton = (Button) findViewById(R.id.log_register_button);
        registeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        });
    }

    //========================================//
                    // LOGIN//
    //========================================//

    private void loginUser(){
        // boolean to detect any error
        boolean error = false;

        // Get input from activity
        TextInputLayout email = (TextInputLayout) findViewById(R.id.log_input_layout_email);
        String usrEmail = email.getEditText().getText().toString();

        TextInputLayout password = (TextInputLayout) findViewById(R.id.log_input_layout_password);
        String usrPassword = password.getEditText().getText().toString();

        // check empty email and password
        if (usrEmail.isEmpty() || usrPassword.isEmpty()){
            Toast.makeText(this, "Faltan algunos campos por rellenar", Toast.LENGTH_SHORT).show();
            error = true;
        }

        if (!error) {
            // Dialog with progres waiting to register user
            final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setMessage("Iniciando sesión...");
            progressDialog.show();
            // SingIn with user and password input
            mFirebaseAuth.signInWithEmailAndPassword(usrEmail, usrPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // if task is succesfull
                            if (task.isSuccessful()) {
                                // Dismiss progress
                                Toast.makeText(LoginActivity.this, "Sesión iniciada",
                                        Toast.LENGTH_SHORT).show();
                                // Start MainActivity
                                startMainActivity();
                            } else {
                                Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos",
                                        Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    //========================================//
                    // INTENTS//
    //========================================//

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startRegisterActivity(){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }



}
