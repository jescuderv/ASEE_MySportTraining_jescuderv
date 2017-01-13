package asee.giiis.unex.es.mysporttraining;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import asee.giiis.unex.es.mysporttraining.Objects.User;
import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_GET = 1;

    // Reference root JSON database
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mUsersRef;
    // FirebaseAuth Object
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // Firebase User
    FirebaseUser mUser = mFirebaseAuth.getCurrentUser();
    // FirebaseStorage Reference
    private StorageReference mStorageImage = FirebaseStorage.getInstance().getReference();
    private Uri mFilePath;

    // Reference from layout
    private TextView mUsername;
    private TextView mScore;
    private TextView mFirstName;
    private TextView mLastName;
    private TextView mEmail;
    private TextView mHeight;
    private TextView mWeight;
    private TextView mAge;
    private TextView mSex;
    private TextView mPhysicalCondition;
    private CircleImageView mImageProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (mUser != null) {
            // Retrieve user from Firebase: /root/users/"userID"
            mUsersRef = mRootRef.child("users").child(mUser.getUid());

            mUsername = (TextView) findViewById(R.id.usr_prof_username);
            mScore = (TextView) findViewById(R.id.usr_prof_score);
            mFirstName = (TextView) findViewById(R.id.usr_prof_first_name_value);
            mLastName = (TextView) findViewById(R.id.usr_prof_last_name_value);
            mEmail = (TextView) findViewById(R.id.usr_prof_email_value);
            mHeight = (TextView) findViewById(R.id.usr_prof_height_value);
            mWeight = (TextView) findViewById(R.id.usr_prof_weight_value);
            mAge = (TextView) findViewById(R.id.usr_prof_age_value);
            mSex = (TextView) findViewById(R.id.usr_prof_sex_value);
            mPhysicalCondition = (TextView) findViewById(R.id.usr_prof_physical_condition_value);

            loadData();

            // ===== IMAGE PROFILE ==== //

            mImageProfile = (CircleImageView) findViewById(R.id.usr_prof_profile_image);
            mImageProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGetImageProfile();
                }
            });


        } else {
            Toast.makeText(this, "Error en la recuperación del usuario de la base de" +
                    " datos", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadData() {

        if (mUser != null) {
            // Get user information profile and show
            mUsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user data
                    User user = dataSnapshot.getValue(User.class);

                    mScore.setText(user.getScore().toString() + " puntos");


                    mUsername.setText(user.getUsername());
                    mScore.setText(user.getScore().toString() + " puntos");
                    mFirstName.setText(user.getFirstName());
                    mLastName.setText(user.getLastName());
                    mEmail.setText(user.getEmail());
                    mHeight.setText(user.getHeight().toString() + " cm");
                    mWeight.setText(user.getWeight().toString() + " kg");
                    mAge.setText(user.getAge().toString() + " años");
                    mSex.setText(user.getSex());
                    mPhysicalCondition.setText(user.getPhysicalCondition());

                    // If image profile URL don't is default
                    if (!user.getUriImageProfile().equals("default")) {
                        // Picasso library to get image from URL profile image user
                        Picasso.with(UserProfileActivity.this).load(user.getUriImageProfile()).into(mImageProfile);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    //========================================//
            // SET IMAGE PROFILE //
    //========================================//

    private void startGetImageProfile() {
        // Intent to get image from storage
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK && data != null) {
            // If activity result is OK, then get file uri
            mFilePath = data.getData();
            uploadFile();
        }
    }

    // Upload file to Firebase storage
    private void uploadFile() {
        if (mFilePath != null && mUser != null) {

            // Progress dialog
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo imagen...");
            progressDialog.show();

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .build();

            // Reference to Firebase storage. Save as "uID.jpg"
            UploadTask storageReference = mStorageImage.child("images/" + mUser.getUid()).putFile(mFilePath, metadata);

            storageReference
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, "Imagen subida correctamente",
                                    Toast.LENGTH_SHORT).show();

                            // Retrieve user from Firebase: /root/users/"userID"
                            mUsersRef = mRootRef.child("users").child(mUser.getUid());
                            // New map to set URL download
                            Map<String, Object> taskMap = new HashMap<>();
                            taskMap.put("uriImageProfile", taskSnapshot.getDownloadUrl().toString());
                            // Update usr image profile url
                            mUsersRef.updateChildren(taskMap);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(UserProfileActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress =
                                    (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage((int) progress + "% completado");
                        }
                    });
        } else {
            Toast.makeText(this, "Error en la subida de imagen", Toast.LENGTH_SHORT).show();
        }
    }

}
