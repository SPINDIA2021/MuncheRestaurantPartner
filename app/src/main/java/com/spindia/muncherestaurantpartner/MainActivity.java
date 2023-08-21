package com.spindia.muncherestaurantpartner;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.spindia.muncherestaurantpartner.chat.notification.Token;

import java.util.HashMap;

import fragments.MenuFragment;
import fragments.OrdersFragment;
import fragments.SalesFragment;
import ui.auth.LoginActivity;
import ui.profile.MyProfileActivity;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    String token;
    private void init() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                         token = task.getResult();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        HashMap<String,Object> tokenData = new HashMap<>();
                        tokenData.put("token", token);
                        db.collection("Tokens").document(  mCurrentUser.getUid()).set(tokenData)
                                .addOnSuccessListener(aVoid -> {
                                }).addOnFailureListener(e -> {
                                });


                      //  Toast.makeText(MainActivity.this, "msg", Toast.LENGTH_SHORT).show();
                    }
                });



    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_orders:
                        selectedFragment = new OrdersFragment();
                        break;
                    case R.id.nav_menu:
                        selectedFragment = new MenuFragment();
                        break;
                    case R.id.nav_sales:
                        selectedFragment = new SalesFragment();
                        break;
                    case R.id.nav_profile:
                        Intent intent = new Intent(this, MyProfileActivity.class);
                        startActivity(intent);
                        this.overridePendingTransition(0,0);
                        break;
                }
                if (selectedFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                            selectedFragment).commit();
                }
                return true;
            };

    private void sendUserToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            sendUserToLogin();
        }else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new OrdersFragment())
                    .commit();
        }
    }

}