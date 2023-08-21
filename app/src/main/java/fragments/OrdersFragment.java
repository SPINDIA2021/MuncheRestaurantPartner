package fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.spindia.muncherestaurantpartner.CurrentOrderActivity;
import com.spindia.muncherestaurantpartner.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.spindia.muncherestaurantpartner.chat.MessageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import models.RestaurantOrderItemModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import ir.samanjafari.easycountdowntimer.CountDownInterface;
import ir.samanjafari.easycountdowntimer.EasyCountDownTextview;

public class OrdersFragment extends Fragment {

    private View view;
    private FirebaseFirestore db;
    private String ruid;
    LinearLayoutManager linearLayoutManager;

    private String RES_LIST = "RestaurantList";
    private String RES_ORDERS = "RestaurantOrders";
    RelativeLayout layReceived,layAccepted,layPreparing,layOnTheWay,layCompleted,layRejected;
    TextView textReceived,textAccepted,textPreparing,textOnTheWay,textCompleted,textRejected;
    View viewReceived,viewAccepted,viewPreparing,viewOnTheWay,viewCompleted,viewRejected;
    RecyclerView recyclerviewReceived,recyclerviewAccepted,recyclerviewPreparing,recyclerviewOnTheWay,recyclerviewCompleted,recyclerviewRejected;



    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);

        init();


        return view;
    }

    private void init() {
        db = FirebaseFirestore.getInstance();
        ruid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);


        layReceived=view.findViewById(R.id.lay_received);
        layAccepted=view.findViewById(R.id.lay_accepted);
        layPreparing=view.findViewById(R.id.lay_preparing);
        layOnTheWay=view.findViewById(R.id.lay_ontheway);
        layCompleted=view.findViewById(R.id.lay_completed);
        layRejected=view.findViewById(R.id.lay_rejected);

        textReceived=view.findViewById(R.id.text_received);
        textAccepted=view.findViewById(R.id.text_accepted);
        textPreparing=view.findViewById(R.id.text_preparing);
        textOnTheWay=view.findViewById(R.id.text_ontheway);
        textCompleted=view.findViewById(R.id.text_completed);
        textRejected=view.findViewById(R.id.text_rejected);

        viewReceived=view.findViewById(R.id.view_received);
        viewAccepted=view.findViewById(R.id.view_accepted);
        viewPreparing=view.findViewById(R.id.view_preparing);
        viewOnTheWay=view.findViewById(R.id.view_ontheway);
        viewCompleted=view.findViewById(R.id.view_completed);
        viewRejected=view.findViewById(R.id.view_rejected);


        recyclerviewReceived=view.findViewById(R.id.receivedRecyclerView);
        recyclerviewAccepted=view.findViewById(R.id.acceptedRecyclerView);
        recyclerviewPreparing=view.findViewById(R.id.preparingRecyclerView);
        recyclerviewOnTheWay=view.findViewById(R.id.onthewayRecyclerView);
        recyclerviewCompleted=view.findViewById(R.id.completedRecyclerView);
        recyclerviewRejected=view.findViewById(R.id.rejectedRecyclerView);

        recyclerviewReceived.setLayoutManager(linearLayoutManager);
        recyclerviewAccepted.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerviewPreparing.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerviewOnTheWay.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerviewCompleted.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerviewRejected.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchReceivedOrders();
        fetchAcceptedOrders();
        fetchPrepareOrders();
        fetchOnTheWayOrders();
        fetchCompletedOrders();
        fetchRejectedOrders();


        layReceived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.white));
                textReceived.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textAccepted.setTextColor(getResources().getColor(R.color.white));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.white));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPreparing.setTextColor(getResources().getColor(R.color.white));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.white));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textOnTheWay.setTextColor(getResources().getColor(R.color.white));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textCompleted.setTextColor(getResources().getColor(R.color.white));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.white));

                layRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textRejected.setTextColor(getResources().getColor(R.color.white));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.white));



                recyclerviewReceived.setVisibility(View.VISIBLE);
                recyclerviewAccepted.setVisibility(View.GONE);
                recyclerviewPreparing.setVisibility(View.GONE);
                recyclerviewOnTheWay.setVisibility(View.GONE);
                recyclerviewCompleted.setVisibility(View.GONE);
                recyclerviewRejected.setVisibility(View.GONE);
            }
        });


        layAccepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textReceived.setTextColor(getResources().getColor(R.color.white));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.white));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.white));
                textAccepted.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPreparing.setTextColor(getResources().getColor(R.color.white));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.white));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textOnTheWay.setTextColor(getResources().getColor(R.color.white));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textCompleted.setTextColor(getResources().getColor(R.color.white));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.white));

                layRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textRejected.setTextColor(getResources().getColor(R.color.white));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.white));


                recyclerviewReceived.setVisibility(View.GONE);
                recyclerviewAccepted.setVisibility(View.VISIBLE);
                recyclerviewPreparing.setVisibility(View.GONE);
                recyclerviewOnTheWay.setVisibility(View.GONE);
                recyclerviewCompleted.setVisibility(View.GONE);
                recyclerviewRejected.setVisibility(View.GONE);
            }
        });


        layPreparing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textReceived.setTextColor(getResources().getColor(R.color.white));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.white));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textAccepted.setTextColor(getResources().getColor(R.color.white));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.white));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.white));
                textPreparing.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textOnTheWay.setTextColor(getResources().getColor(R.color.white));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textCompleted.setTextColor(getResources().getColor(R.color.white));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.white));

                layRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textRejected.setTextColor(getResources().getColor(R.color.white));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.white));

                recyclerviewReceived.setVisibility(View.GONE);
                recyclerviewAccepted.setVisibility(View.GONE);
                recyclerviewPreparing.setVisibility(View.VISIBLE);
                recyclerviewOnTheWay.setVisibility(View.GONE);
                recyclerviewCompleted.setVisibility(View.GONE);
                recyclerviewRejected.setVisibility(View.GONE);
            }
        });


        layOnTheWay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textReceived.setTextColor(getResources().getColor(R.color.white));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.white));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textAccepted.setTextColor(getResources().getColor(R.color.white));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.white));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPreparing.setTextColor(getResources().getColor(R.color.white));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.white));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));
                textOnTheWay.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textCompleted.setTextColor(getResources().getColor(R.color.white));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.white));

                layRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textRejected.setTextColor(getResources().getColor(R.color.white));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.white));

                recyclerviewReceived.setVisibility(View.GONE);
                recyclerviewAccepted.setVisibility(View.GONE);
                recyclerviewPreparing.setVisibility(View.GONE);
                recyclerviewOnTheWay.setVisibility(View.VISIBLE);
                recyclerviewCompleted.setVisibility(View.GONE);
                recyclerviewRejected.setVisibility(View.GONE);
            }
        });

        layCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textReceived.setTextColor(getResources().getColor(R.color.white));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.white));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textAccepted.setTextColor(getResources().getColor(R.color.white));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.white));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPreparing.setTextColor(getResources().getColor(R.color.white));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.white));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textOnTheWay.setTextColor(getResources().getColor(R.color.white));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.white));
                textCompleted.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                layRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textRejected.setTextColor(getResources().getColor(R.color.white));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.white));


                recyclerviewReceived.setVisibility(View.GONE);
                recyclerviewAccepted.setVisibility(View.GONE);
                recyclerviewPreparing.setVisibility(View.GONE);
                recyclerviewOnTheWay.setVisibility(View.GONE);
                recyclerviewCompleted.setVisibility(View.VISIBLE);
                recyclerviewRejected.setVisibility(View.GONE);
            }
        });

        layRejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layReceived.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textReceived.setTextColor(getResources().getColor(R.color.white));
                viewReceived.setBackgroundColor(getResources().getColor(R.color.white));

                layAccepted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textAccepted.setTextColor(getResources().getColor(R.color.white));
                viewAccepted.setBackgroundColor(getResources().getColor(R.color.white));

                layPreparing.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textPreparing.setTextColor(getResources().getColor(R.color.white));
                viewPreparing.setBackgroundColor(getResources().getColor(R.color.white));

                layOnTheWay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textOnTheWay.setTextColor(getResources().getColor(R.color.white));
                viewOnTheWay.setBackgroundColor(getResources().getColor(R.color.white));

                layCompleted.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                textCompleted.setTextColor(getResources().getColor(R.color.white));
                viewCompleted.setBackgroundColor(getResources().getColor(R.color.white));

                layRejected.setBackgroundColor(getResources().getColor(R.color.white));
                textRejected.setTextColor(getResources().getColor(R.color.colorPrimary));
                viewRejected.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                recyclerviewReceived.setVisibility(View.GONE);
                recyclerviewAccepted.setVisibility(View.GONE);
                recyclerviewPreparing.setVisibility(View.GONE);
                recyclerviewOnTheWay.setVisibility(View.GONE);
                recyclerviewCompleted.setVisibility(View.GONE);
                recyclerviewRejected.setVisibility(View.VISIBLE);
            }
        });


    }


    private void fetchReceivedOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "Placed");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                    holder.mTextAcceptOrder.setText("ACCEPT ORDER");



                    holder.layTrackOrder.setVisibility(View.VISIBLE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });

                holder.mOrderDeclineBtn.setOnClickListener(view -> {
                    //Toast.makeText(getContext(), "Order is Declined", Toast.LENGTH_SHORT).show();
                    declineOrder(model.getOrder_id(),model.getCustomer_uid());
                    holder.countDownTextview.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);
                    holder.timeFormatText.setVisibility(View.GONE);
                });

                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                        acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"Accepted");
                        holder.mAcceptOrderBtn.setVisibility(View.GONE);
                        holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewReceived.setAdapter(orderAdapter);

    }


    private void fetchAcceptedOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "Accepted");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                holder.mTextAcceptOrder.setText("PREPARE ORDER");



                holder.layTrackOrder.setVisibility(View.VISIBLE);
                holder.mOrderDeclineBtn.setVisibility(View.GONE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });



                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                    acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"Preparing");
                    holder.mAcceptOrderBtn.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewAccepted.setAdapter(orderAdapter);
    }


    private void fetchPrepareOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "Preparing");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                holder.mTextAcceptOrder.setText("ON THE WAY");



                holder.layTrackOrder.setVisibility(View.VISIBLE);
                holder.mOrderDeclineBtn.setVisibility(View.GONE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });



                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                    acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"On The Way");
                    holder.mAcceptOrderBtn.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewPreparing.setAdapter(orderAdapter);
    }

    private void fetchOnTheWayOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "On The Way");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                holder.mTextAcceptOrder.setText("DELIVERED");



                holder.layTrackOrder.setVisibility(View.VISIBLE);
                holder.mOrderDeclineBtn.setVisibility(View.GONE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });



                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                    acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"Completed");
                    holder.mAcceptOrderBtn.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewOnTheWay.setAdapter(orderAdapter);
    }


    private void fetchCompletedOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "Completed");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                holder.mAcceptOrderBtn.setVisibility(View.GONE);



                holder.layTrackOrder.setVisibility(View.GONE);
                holder.mOrderDeclineBtn.setVisibility(View.GONE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });



                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                    acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"Completed");
                    holder.mAcceptOrderBtn.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewCompleted.setAdapter(orderAdapter);
    }



    private void fetchRejectedOrders() {
        Query query = db.collection(RES_LIST).document(ruid).collection(RES_ORDERS).whereEqualTo("order_status",  "Declined");

        FirestoreRecyclerOptions<RestaurantOrderItemModel> orderedItemModel = new FirestoreRecyclerOptions.Builder<RestaurantOrderItemModel>()
                .setQuery(query, RestaurantOrderItemModel.class)
                .build();

        FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder> orderAdapter = new FirestoreRecyclerAdapter<RestaurantOrderItemModel, RestaurantItemHolder>(orderedItemModel) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull RestaurantItemHolder holder, int position, @NonNull RestaurantOrderItemModel model) {


                ArrayList<String> orderedItems = model.getOrdered_items();
                for (int i = 0; i < orderedItems.size(); i++) {
                    TextView tv = new TextView(getContext());
                    final Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.open_sans);
                    tv.setText(orderedItems.get(i));
                    tv.setTypeface(typeface);
                    tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    tv.setTextSize(15);
                    holder.orderItemLayout.addView(tv);
                }

                if (model.getPayment_method().equals("COD")) {
                    holder.mCodMethod.setVisibility(View.VISIBLE);
                } else if (model.getPayment_method().equals("PAID")) {
                    holder.mPaidMethod.setVisibility(View.VISIBLE);
                }

                holder.mOrderID.setText("Order Id: " + model.getOrder_id());
                holder.mCustomerName.setText(model.getCustomer_name() + "'s Order");
                holder.mDeliveryAddress.setText("Address: " + model.getDelivery_address());
                holder.mOrderTime.setText("Placed on:"+model.getShort_time());
                holder.mTotalAmount.setText("Total Amount: " + model.getTotal_amount());
                holder.mtvOrderStatus.setText(model.getOrder_status());

                holder.mAcceptOrderBtn.setVisibility(View.GONE);



                holder.layTrackOrder.setVisibility(View.GONE);
                holder.mOrderDeclineBtn.setVisibility(View.GONE);


                holder.countDownTextview.setTime(0, 0, 0, 30);
                holder.countDownTextview.startTimer();
                final Typeface typeface2 = ResourcesCompat.getFont(requireContext(), R.font.open_sans_semibold);
                holder.countDownTextview.setTypeFace(typeface2);
                holder.countDownTextview.setOnTick(new CountDownInterface() {
                    @Override
                    public void onTick(long time) {

                        if (time > 60000) {
                            holder.timeFormatText.setText("minutes left");
                        } else {
                            holder.timeFormatText.setText("seconds left");
                        }

                    }

                    @Override
                    public void onFinish() {
                    }
                });



                holder.mAcceptOrderBtn.setOnClickListener(view -> {


                    acceptOrder(model.getOrder_id(),model.getCustomer_uid(),"Completed");
                    holder.mAcceptOrderBtn.setVisibility(View.GONE);
                    holder.mOrderDeclineBtn.setVisibility(View.GONE);



                });


                holder.layTrackOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", model.getLatitude(), model.getLongitude());
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        startActivity(intent);
                        /*Intent intent = new Intent(getActivity(), CurrentOrderActivity.class);
                        intent.putExtra("RES_UID", model.getCustomer_uid());
                        intent.putExtra("orderid", model.getOrder_id());
                        startActivity(intent);*/
                    }
                });

                holder.layChat.setVisibility(View.GONE);

                holder.layChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), MessageActivity.class);
                        intent.putExtra("UserId", model.getCustomer_uid());
                        intent.putExtra("UserName", model.getCustomer_name());
                        startActivity(intent);
                    }
                });

                if (model.getExtra_instructions().equals("none")) {
                    holder.extraInstructionsText.setText("\u2022 No additional instruction");
                } else {
                    holder.extraInstructionsText.setText("\u2022 " + model.getExtra_instructions());
                }
            }

            @NonNull
            @Override
            public RestaurantItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ordered_items_layout, parent, false);

                return new RestaurantItemHolder(view);

            }
        };
        orderAdapter.startListening();
        orderAdapter.notifyDataSetChanged();
        recyclerviewRejected.setAdapter(orderAdapter);
    }

    private void acceptOrder(String order_id, String customer_uid, String status) {

        Map<String, Object> orderedItemsMap = new HashMap<>();

        orderedItemsMap.put("order_status",status);
        db.collection("UserList").document(customer_uid).collection("UserOrders").document(order_id).update(orderedItemsMap).addOnCompleteListener(task -> {
        });

        Map<String, Object> orderedRestaurantName = new HashMap<>();

        orderedRestaurantName.put("order_status",status);
        db.collection("RestaurantList").document("fBG9p6jlfbSu1HEaV7vs9LphJ3g1").collection("RestaurantOrders").document(order_id).update(orderedRestaurantName).addOnCompleteListener(task -> {

            Toast.makeText(getContext(), "Order is "+status, Toast.LENGTH_SHORT).show();
        });
    }


    private void declineOrder(String order_id, String customer_uid) {

        Map<String, Object> orderedItemsMap = new HashMap<>();

        orderedItemsMap.put("order_status", "Declined");
        db.collection("UserList").document(customer_uid).collection("UserOrders").document(order_id).update(orderedItemsMap).addOnCompleteListener(task -> {
        });

        Map<String, Object> orderedRestaurantName = new HashMap<>();

        orderedRestaurantName.put("order_status", "Declined");
        db.collection("RestaurantList").document("fBG9p6jlfbSu1HEaV7vs9LphJ3g1").collection("RestaurantOrders").document(order_id).update(orderedRestaurantName).addOnCompleteListener(task -> {

            Toast.makeText(getContext(), "Order is Declined", Toast.LENGTH_SHORT).show();
        });
    }

    public static class RestaurantItemHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.orderId)
        TextView mOrderID;
        @BindView(R.id.orderTime)
        TextView mOrderTime;
        @BindView(R.id.deliveryAddress)
        TextView mDeliveryAddress;
        @BindView(R.id.codMethod)
        TextView mCodMethod;
        @BindView(R.id.paidMethod)
        TextView mPaidMethod;
        @BindView(R.id.totAmount)
        TextView mTotalAmount;
        @BindView(R.id.declineBtn)
        RelativeLayout mOrderDeclineBtn;
        @BindView(R.id.acceptBtn)
        RelativeLayout mAcceptOrderBtn;
        @BindView(R.id.text_accept)
        TextView mTextAcceptOrder;
        @BindView(R.id.orderedItemContainer)
        LinearLayout orderItemLayout;
        @BindView(R.id.easyCountDownTextview)
        EasyCountDownTextview countDownTextview;
        @BindView(R.id.timeFormatText)
        TextView timeFormatText;
        @BindView(R.id.extraInstructionsText)
        TextView extraInstructionsText;
        @BindView(R.id.customerNameText)
        TextView mCustomerName;
        @BindView(R.id.statusText)
        TextView mtvOrderStatus;
        @BindView(R.id.lay_track_order)
        RelativeLayout layTrackOrder;
        @BindView(R.id.lay_chat)
        RelativeLayout layChat;

        public RestaurantItemHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}