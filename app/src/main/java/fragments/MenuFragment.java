package fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.Lottie;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.spindia.muncherestaurantpartner.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import models.MenuItemModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuFragment extends Fragment implements View.OnClickListener {

    private View view;
    private FirebaseFirestore db;
    private String Ruid;
    private TextView mCreateNewMenuBtn;
    private FirestoreRecyclerAdapter<MenuItemModel, MenuItemHolder> adapter;
    LinearLayoutManager linearLayoutManager;
    private RecyclerView mMenuItemRecyclerView;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu,container,false);

        init();
        getMenuItems();
        mCreateNewMenuBtn.setOnClickListener(this);

        return view;
    }

    private void init() {
        mCreateNewMenuBtn = view.findViewById(R.id.createNewMenuBtn);
        mCreateNewMenuBtn.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        assert mCurrentUser != null;
        Ruid = mCurrentUser.getUid();
        db = FirebaseFirestore.getInstance();
        mMenuItemRecyclerView = view.findViewById(R.id.menuItemRecyclerView);
        linearLayoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        mMenuItemRecyclerView.setLayoutManager(linearLayoutManager);
    }


    private void getMenuItems() {

        Query query = db.collection("Menu").document(Ruid).collection("MenuItems");
        FirestoreRecyclerOptions<MenuItemModel> menuItemModel = new FirestoreRecyclerOptions.Builder<MenuItemModel>()
                .setQuery(query, MenuItemModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<MenuItemModel, MenuItemHolder>(menuItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onBindViewHolder(@NotNull MenuItemHolder holder, int position, @NotNull MenuItemModel model) {

                holder.mItemName.setText(model.getName());
                holder.mItemCategory.setText(model.getCategory());
                String isActive = model.getIs_active();
                if (isActive.equals("yes")){
                    holder.isActiveSwitch.setChecked(true);
                }else if(isActive.equals("no")){
                    holder.isActiveSwitch.setChecked(false);
                }
                String specImage = String.valueOf(model.getSpecification());
                if (specImage.equals("Veg")){
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(R.drawable.veg_symbol).into(holder.foodSpecification);
                }else {
                    Glide.with(Objects.requireNonNull(requireActivity()))
                            .load(R.drawable.non_veg_symbol).into(holder.foodSpecification);
                }
                holder.mItemPrice.setText("\u20B9 " + model.getPrice());

                holder.isActiveSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
                    Map<String, Object> updateItemStatusNotAvailable = new HashMap<>();
                    updateItemStatusNotAvailable.put("is_active", "no");
                    if (!b){
                        db.collection("Menu").document(Ruid).collection("MenuItems")
                                .document(holder.mItemName.getText().toString()).update(updateItemStatusNotAvailable).addOnCompleteListener(task -> {
                        });
                    }
                    else {
                        Map<String, Object> updateItemStatusAvailable = new HashMap<>();
                        updateItemStatusAvailable.put("is_active", "yes");
                        db.collection("Menu").document(Ruid).collection("MenuItems").document(holder.mItemName.getText().toString()).update(updateItemStatusAvailable).addOnCompleteListener(task -> {
                        });
                    }
                });

                holder.mFavoriteAnim.setOnClickListener(view -> {
                    if (holder.mFavoriteAnim.getProgress() >= 0.1f){
                        holder.mFavoriteAnim.setSpeed(-1);
                        holder.mFavoriteAnim.playAnimation();
                        delFavRes(model);
                    }else if(holder.mFavoriteAnim.getProgress() == 0.0f){
                        holder.mFavoriteAnim.setSpeed(1);
                        holder.mFavoriteAnim.playAnimation();
                        favRes(model);
                    }
                });



                db.collection("RestaurantList")
                        .document(Ruid)
                        .collection("CompleteItems")
                        .document(model.getMenuUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                DocumentSnapshot docRef = task.getResult();
                                if (Objects.requireNonNull(docRef).exists()){

                                    holder.mCompleteMenuItem.setImageResource(R.drawable.complete);
                                }else {

                                    holder.mCompleteMenuItem.setImageResource(R.drawable.certificate);
                                }
                            }
                        });


                holder.mCompleteMenuItem.setOnClickListener(view -> {

                    db.collection("RestaurantList")
                            .document(Ruid)
                            .collection("CompleteItems")
                            .document(model.getMenuUid())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    DocumentSnapshot docRef = task.getResult();
                                    if (Objects.requireNonNull(docRef).exists()){
                                        holder.mCompleteMenuItem.setImageResource(R.drawable.certificate);
                                        delCompleteRes(model);
                                    }else {
                                        holder.mCompleteMenuItem.setImageResource(R.drawable.complete);
                                        completeRes(model);
                                    }
                                }
                            });


                });

                holder.mEditMenuView.setOnClickListener(view1 -> {
                   /* Fragment fragment = new EditMenuFragment();
                    Bundle args = new Bundle();
                    args.putString("name", holder.mItemName.getText().toString());
                    args.putString("price", holder.mItemPrice.getText().toString());
                    args.putString("specification", model.getSpecification());
                    args.putString("category", holder.mItemCategory.getText().toString());
                    fragment.setArguments(args);
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();*/

                    Intent intent=new Intent(getActivity(),EditMenuFragment.class);
                    intent.putExtra("menuUid", model.getMenuUid());
                    intent.putExtra("name", model.getName());
                    intent.putExtra("price", model.getPrice());
                    intent.putExtra("specification", model.getSpecification());
                    intent.putExtra("menu_spot_image", model.getMenu_spot_image());
                    intent.putExtra("category", model.getCategory());
                    intent.putExtra("discount", model.getDiscount());
                    intent.putExtra("desc", model.getDescription());
                    intent.putExtra("category_index", model.getCategory_index());
                    startActivity(intent);
                });

                db.collection("RestaurantList")
                        .document(Ruid)
                        .collection("FavoriteItems")
                        .document(model.getMenuUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                DocumentSnapshot docRef = task.getResult();
                                if (Objects.requireNonNull(docRef).exists()){
                                    holder.mFavoriteAnim.setProgress(0.1f);
                                    holder.mFavoriteAnim.resumeAnimation();
                                }else {
                                    holder.mFavoriteAnim.setProgress(0.0f);
                                }
                            }
                        });

            }
            @NotNull
            @Override
            public MenuItemHolder onCreateViewHolder(@NotNull ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.menu_item_details, group, false);
                return new MenuItemHolder(view);
            }
            @Override
            public void onError(@NotNull FirebaseFirestoreException e) {
                Log.e("error", Objects.requireNonNull(e.getMessage()));
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        mMenuItemRecyclerView.setAdapter(adapter);

    }

    public static class MenuItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.itemName)
        TextView mItemName;
        @BindView(R.id.foodMark)
        ImageView foodSpecification;
        @BindView(R.id.itemPrice)
        TextView mItemPrice;
        @BindView(R.id.itemCategory)
        TextView mItemCategory;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        @BindView(R.id.itemActiveSwitch)
        Switch isActiveSwitch;
        @BindView(R.id.editMenuItem)
        ImageView mEditMenuView;
        @BindView(R.id.favoriteAnim)
        LottieAnimationView mFavoriteAnim;
        @BindView(R.id.completeMenuItem)
        ImageView mCompleteMenuItem;

        public MenuItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    private void favRes(MenuItemModel model) {
        Map<String, Object> favResMap = new HashMap<>();
        favResMap.put("item_uid" , model.getMenuUid());
        favResMap.put("item_name", model.getName());
        favResMap.put("item_image", model.getMenu_spot_image());
        favResMap.put("item_price", model.getPrice());
        favResMap.put("specification", model.getSpecification());
        favResMap.put("discount", model.getDiscount());
        favResMap.put("category", model.getCategory());

        db.collection("RestaurantList")
                .document(Ruid)
                .collection("FavoriteItems")
                .document(model.getMenuUid()).set(favResMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), model.getName() + " has marked as favorite", Toast.LENGTH_SHORT).show();
                });
    }

    private void completeRes(MenuItemModel model) {
        Map<String, Object> favResMap = new HashMap<>();
        favResMap.put("item_uid" , model.getMenuUid());
        favResMap.put("item_name", model.getName());
        favResMap.put("item_image", model.getMenu_spot_image());
        favResMap.put("item_price", model.getPrice());
        favResMap.put("specification", model.getSpecification());
        favResMap.put("discount", model.getDiscount());
        favResMap.put("category", model.getCategory());

        db.collection("RestaurantList")
                .document(Ruid)
                .collection("CompleteItems")
                .document(model.getMenuUid()).set(favResMap)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), model.getName() + " has marked as Complete menu Item", Toast.LENGTH_SHORT).show();
                });
    }



    private void delFavRes(MenuItemModel model) {
        db.collection("RestaurantList")
                .document(Ruid)
                .collection("FavoriteItems")
                .document(model.getMenuUid()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Removed as favorite", Toast.LENGTH_SHORT).show();
                });
    }


    private void delCompleteRes(MenuItemModel model) {
        db.collection("RestaurantList")
                .document(Ruid)
                .collection("CompleteItems")
                .document(model.getMenuUid()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Removed as Complete menu Item", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.createNewMenuBtn:
                startActivity(new Intent(getActivity(),CreateNewMenuFragment.class));
           /*     Fragment fragment = new CreateNewMenuFragment();
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.stopListening();
    }
}