package fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.spindia.muncherestaurantpartner.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditMenuFragment extends AppCompatActivity {


    private MaterialSpinner mCategorySpinnerEdit, mFoodVegOrNotSpinnerEdit;
    private EditText mMenuItemNameEdit, mMenuItemPriceEdit, mMenuItemDescEdit,mMenuItemDiscountEdit;
    ImageView mMenuImageview;
    private Button mUpdateMenuItemBtn, mDeleteMenuItemBtn;

    private String Ruid;
    String id,name,price,specification, image,category,category_index, description,discount;
    private ImageButton mChangeItemSpotImageBtn;

    private ImageView mItemSpotImage;

    private ProgressDialog progressDialog;
    Uri mImageUri;
    private StorageReference filePath;
    private StorageReference mMenuImageRef;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_edit_menu);
        init();
    }



    private void init() {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        mMenuImageRef = FirebaseStorage.getInstance().getReference();
        Ruid = mCurrentUser.getUid();
        progressDialog = new ProgressDialog(this);
        mCategorySpinnerEdit = findViewById(R.id.chooseCategorySpinnerEdit);
        mFoodVegOrNotSpinnerEdit = findViewById(R.id.foodVegOrNotSpinnerEdit);
        mMenuItemNameEdit = findViewById(R.id.newMenuItemEditTextEdit);
        mMenuItemPriceEdit = findViewById(R.id.menuItemPriceEdit);
        mMenuItemDiscountEdit = findViewById(R.id.menuItemEditDiscount);
        mMenuItemDescEdit = findViewById(R.id.menuItemDescriptionEdit);
        mMenuItemDescEdit = findViewById(R.id.menuItemDescriptionEdit);
        mMenuImageview = findViewById(R.id.item_spotImage);
        mDeleteMenuItemBtn = findViewById(R.id.deleteItemInfoBtnEdit);
        mUpdateMenuItemBtn = findViewById(R.id.updateItemInfoBtnEdit);
        mItemSpotImage = findViewById(R.id.item_spotImage);
        mChangeItemSpotImageBtn = findViewById(R.id.changeItemSpotImageBtn);

        mCategorySpinnerEdit.setItems("Select Category","Appetizers","Entrees","Starters","Salads",
                "Main Course","Desserts","Ice Cream","Biryani","Parathas","Pizzas","Burgers",
                "Sandwiches","Drinks","Beverages","Alcoholics","Sushi", "Pasta","Cakes","Pastries",
                "South Indian","North Indian","Thali","Dosas","Chinese", "Soups","Recommends");


        Intent intent=getIntent();
        id=intent.getStringExtra("menuUid");
        name=intent.getStringExtra("name");
        price=intent.getStringExtra("price");
        specification=intent.getStringExtra("specification");
        image=intent.getStringExtra("menu_spot_image");
        category=intent.getStringExtra("category");
        category_index=intent.getStringExtra("category_index");
        discount=intent.getStringExtra("discount");
        description=intent.getStringExtra("desc");

        mMenuItemNameEdit.setText(name);
        mMenuItemPriceEdit.setText(price);
        mCategorySpinnerEdit.setSelectedIndex(Integer.parseInt(category_index));
        mMenuItemDiscountEdit.setText(discount);
        mMenuItemDescEdit.setText(description);

        Glide.with(this)
                .load(image).error(R.drawable.restaurant_image_placeholder)
                .into(mMenuImageview);

        mCategorySpinnerEdit.setOnItemSelectedListener((view, position, id, item) -> {
            if (!item.toString().equals("Select Category")){
                category = item.toString();
            }
        });

        mFoodVegOrNotSpinnerEdit.setItems("Choose","Veg", "NonVeg");

        try{
            if (specification.equals("Veg"))
            {
                mFoodVegOrNotSpinnerEdit.setSelectedIndex(1);
            }else  if (specification.equals("NonVeg"))
            {
                mFoodVegOrNotSpinnerEdit.setSelectedIndex(2);
            }else {
                mFoodVegOrNotSpinnerEdit.setSelectedIndex(0);
            }
        }catch (Exception e){}


        mFoodVegOrNotSpinnerEdit.setOnItemSelectedListener((view, position, id, item) -> {
            if (!item.toString().equals("Choose")){
                specification = item.toString();
            }
        });



        mChangeItemSpotImageBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if(ActivityCompat.checkSelfPermission(EditMenuFragment.this,
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

        mUpdateMenuItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMenuItem();
            }
        });




    }

    private void updateMenuItem() {

        String mItemName = mMenuItemNameEdit.getText().toString();
        String mItemPrice = mMenuItemPriceEdit.getText().toString();
        String mItemDiscount = mMenuItemDiscountEdit.getText().toString();
        String mItemDesc = mMenuItemDescEdit.getText().toString();

        DocumentReference mMenuRef = db.collection("Menu")
                .document(Ruid)
                .collection("MenuItems")
                .document(mItemName);



        Map<String, String> menuItemMap = new HashMap<>();
        menuItemMap.put("name", mItemName);
        menuItemMap.put("price", mItemPrice);
        menuItemMap.put("discount", mItemDiscount);
        menuItemMap.put("category", category);
        menuItemMap.put("specification", specification);
        menuItemMap.put("description", mItemDesc);
        menuItemMap.put("is_active", "yes");
        menuItemMap.put("menuUid" , id);
        menuItemMap.put("isCompleteMenu" , "false");
        menuItemMap.put("search_menuitem", mItemName.toLowerCase());
        menuItemMap.put("category_index", String.valueOf(mCategorySpinnerEdit.getSelectedIndex()));

        mMenuRef.set(menuItemMap).addOnSuccessListener(aVoid -> {
            filePath = mMenuImageRef.child("menu_spot_image").child(id + ".jpg");

                if (mImageUri==null)
                {

                    Toast.makeText(EditMenuFragment.this, "Uploaded", Toast.LENGTH_LONG).show();

                    Fragment fragment = new MenuFragment();
                    FragmentManager fragmentManager =getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    progressDialog.dismiss();

               }else {
                    filePath.putFile(mImageUri).addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            mMenuImageRef.child("menu_spot_image").child(id + ".jpg").getDownloadUrl().addOnSuccessListener(uri -> {

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
                                                        Glide.with(EditMenuFragment.this)
                                                                .load(R.drawable.restaurant_image_placeholder)
                                                                .into(mItemSpotImage);

                                                    }else {
                                                        Glide.with(EditMenuFragment.this)
                                                                .load(validateImage)
                                                                .placeholder(R.drawable.restaurant_image_placeholder)
                                                                .into(mItemSpotImage);

                                                    }



                                                    Toast.makeText(EditMenuFragment.this, "Uploaded", Toast.LENGTH_LONG).show();

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
                }




        });

    }

    private void setImage() {
        ImagePicker.with(this)
                .crop()                    //Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)    //Final image resolution will be less than 1080 x 1080(Optional)
                .start(123);
    }


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


    }



}