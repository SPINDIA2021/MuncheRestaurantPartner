package fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spindia.muncherestaurantpartner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CreateNewMenuFragment extends AppCompatActivity implements View.OnClickListener{

   // private View view;
    private MaterialSpinner mCategorySpinner;
    private EditText mMenuItemName, mMenuItemPrice, mMenuItemDiscount, mMenuItemDesc;
    private String Ruid, mItemImageUrl;
    private String mMenuCategory;
    private String mItemVegOrNot;
    private FirebaseFirestore db;
    private ImageButton mChangeItemSpotImageBtn;

    private ImageView mItemSpotImage;

    public CreateNewMenuFragment() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_create_new_menu);
        init();

    }


    private void init() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        assert mCurrentUser != null;
        Ruid = mCurrentUser.getUid();
        progressDialog = new ProgressDialog(this);
        mMenuImageRef = FirebaseStorage.getInstance().getReference();

        mCategorySpinner = findViewById(R.id.chooseCategorySpinner);
        MaterialSpinner mFoodVegOrNotSpinner = findViewById(R.id.foodVegOrNotSpinner);
        Button mSaveMenuItemInfo = findViewById(R.id.saveItemInfoBtn);
        mMenuItemName = findViewById(R.id.newMenuItemEditText);
        mMenuItemPrice = findViewById(R.id.menuItemPrice);
        mMenuItemDiscount = findViewById(R.id.menuItemDiscount);
        mItemSpotImage = findViewById(R.id.item_spotImage);
        mChangeItemSpotImageBtn = findViewById(R.id.changeItemSpotImageBtn);
        mMenuItemDesc = findViewById(R.id.menuItemDescription);
        mCategorySpinner.setItems("Select Category","Appetizers","Entrees","Starters","Salads",
                "Main Course","Desserts","Ice Cream","Biryani","Parathas","Pizzas","Burgers",
                "Sandwiches","Drinks","Beverages","Alcoholics","Sushi", "Pasta","Cakes","Pastries",
                "South Indian","North Indian","Thali","Dosas","Chinese", "Soups","Recommends");
        mCategorySpinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (!item.toString().equals("Select Category")){
                mMenuCategory = item.toString();
            }
        });
        mFoodVegOrNotSpinner.setItems("Choose","Veg", "NonVeg");
        mFoodVegOrNotSpinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (!item.toString().equals("Choose")){
                mItemVegOrNot = item.toString();
            }
        });

        mChangeItemSpotImageBtn.setOnClickListener(this);



        mSaveMenuItemInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mItemName = mMenuItemName.getText().toString();
                String mItemPrice = mMenuItemPrice.getText().toString();
                String mItemDiscount = mMenuItemDiscount.getText().toString();
                String mItemDesc = mMenuItemDesc.getText().toString();

                DocumentReference mMenuRef = db.collection("Menu")
                        .document(Ruid)
                        .collection("MenuItems")
                        .document(mItemName);


                String menuItemId = String.valueOf(UUID.randomUUID()).replace("-","");
                Map<String, String> menuItemMap = new HashMap<>();
                menuItemMap.put("name", mItemName);
                menuItemMap.put("price", mItemPrice);
                menuItemMap.put("discount", mItemDiscount);
                menuItemMap.put("category", mMenuCategory);
                menuItemMap.put("specification", mItemVegOrNot);
                menuItemMap.put("description", mItemDesc);
                menuItemMap.put("is_active", "yes");
                menuItemMap.put("menuUid" , menuItemId);
                menuItemMap.put("isCompleteMenu" , "false");
                menuItemMap.put("search_menuitem", mItemName.toLowerCase());
                menuItemMap.put("category_index", String.valueOf(mCategorySpinner.getSelectedIndex()));

                mMenuRef.set(menuItemMap).addOnSuccessListener(aVoid -> {
                    filePath = mMenuImageRef.child("menu_spot_image").child(menuItemId + ".jpg");
                    filePath.putFile(mImageUri).addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            mMenuImageRef.child("menu_spot_image").child(menuItemId + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {

                                final String downloadUrl = uri.toString();

                                UploadTask uploadTask = filePath.putFile(mImageUri);

                                uploadTask.addOnSuccessListener(taskSnapshot -> {

                                    if (task.isSuccessful()){
                                        HashMap<String,Object> updateHashmap = new HashMap<>();
                                        updateHashmap.put("menu_spot_image", downloadUrl);

                                        mMenuRef.update(updateHashmap).addOnSuccessListener(o -> {

                                            mMenuRef.get().addOnCompleteListener(task1 -> {

                                                if (task1.isSuccessful()){
                                                    DocumentSnapshot documentSnapshot = task1.getResult();
                                                    assert documentSnapshot != null;


                                                    String validateImage = String.valueOf(documentSnapshot.get("menu_spotimage"));



                                                    if (validateImage.equals("empty")){
                                                        Glide.with(CreateNewMenuFragment.this)
                                                                .load(R.drawable.restaurant_image_placeholder)
                                                                .into(mItemSpotImage);

                                                    }else {
                                                        Glide.with(CreateNewMenuFragment.this)
                                                                .load(validateImage)
                                                                .placeholder(R.drawable.restaurant_image_placeholder)
                                                                .into(mItemSpotImage);

                                                    }



                                                    Toast.makeText(CreateNewMenuFragment.this, "Uploaded", Toast.LENGTH_LONG).show();

                                                     Fragment fragment = new MenuFragment();
                FragmentManager fragmentManager =getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                                                    progressDialog.dismiss();
                                                }

                                            });

                                        });

                                    }

                                });

                            });
                        }

                    });


                });




            }
        });

        mChangeItemSpotImageBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(CreateNewMenuFragment.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            2000);
                }
                else {
                    setImage();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {


    }

    private void setImage() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(123);
    }

    private ProgressDialog progressDialog;
    Uri mImageUri;
    private StorageReference filePath;
    private StorageReference mMenuImageRef;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        progressDialog.setMessage("Uploading, Please Wait...");
        progressDialog.show();

        if (requestCode==123 && resultCode == RESULT_OK) {
            //Image Uri will not be null for RESULT_OK

            if (data != null) {
                mImageUri = data.getData();
                Bitmap bitmapImage = null;
                try {
                    bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mItemSpotImage.setImageBitmap(bitmapImage);
            } else {
                Toast.makeText(this, "Try Again!!", Toast.LENGTH_SHORT)
                        .show();
            }


            progressDialog.dismiss();
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }

     /*   if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {

                assert data != null;
                mImageUri = data.getData();

                try {

                    // Setting image on image view using Bitmap
                    Bitmap bitmap = MediaStore
                            .Images
                            .Media
                            .getBitmap(
                                    this.getContentResolver(),
                                    mImageUri);
                    mItemSpotImage.setImageBitmap(bitmap);
                    progressDialog.dismiss();
                }

                catch (IOException e) {
                    // Log the exception
                    e.printStackTrace();
                }
             *//*   filePath = mRestaurantImageRef.child("item_spot_image").child(Ruid + ".jpg");
                filePath.putFile(mImageUri).addOnCompleteListener(task -> {

                    if (task.isSuccessful()){
                        mRestaurantImageRef.child("item_spot_image").child(Ruid + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {

                            final String downloadUrl = uri.toString();

                            UploadTask uploadTask = filePath.putFile(mImageUri);

                            uploadTask.addOnSuccessListener(taskSnapshot -> {

                                if (task.isSuccessful()){
                                    HashMap<String,Object> updateHashmap = new HashMap<>();
                                    updateHashmap.put("item_spotimage", downloadUrl);

                                    mMenuRef.update(updateHashmap).addOnSuccessListener(o -> {

                                        mMenuRef.get().addOnCompleteListener(task1 -> {

                                            if (task1.isSuccessful()){
                                                DocumentSnapshot documentSnapshot = task1.getResult();
                                                assert documentSnapshot != null;
                                                mItemImageUrl = String.valueOf(documentSnapshot.get("item_spotimage"));
                                                Glide.with(this)
                                                        .load(mItemImageUrl)
                                                        .into(mItemSpotImage);
                                                progressDialog.dismiss();
                                            }

                                        });

                                    });

                                }

                            });

                        });
                    }

                });*//*

            }else {
                progressDialog.dismiss();
            }

        }*/
    }

}