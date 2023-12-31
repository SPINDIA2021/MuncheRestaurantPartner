package models;

import java.util.ArrayList;

public class RestaurantOrderItemModel {

    private ArrayList<String> ordered_items;
    private String short_time;
    private String order_id;
    private String delivery_address;
    private String total_amount;
    private String payment_method;
    private String ordered_at;
    private String extra_instructions;
    private String customer_name;
    private String customer_uid;
    private  String order_status;
    private  String latitude;
    private  String longitude;

    public RestaurantOrderItemModel() {
    }
    public RestaurantOrderItemModel(ArrayList<String> ordered_items, String short_time, String order_id, String delivery_address, String total_amount, String payment_method, String ordered_at, String extra_instructions, String customer_name, String customer_uid,String order_status,String latitude,String longitude) {
        this.ordered_items = ordered_items;
        this.short_time = short_time;
        this.order_id = order_id;
        this.delivery_address = delivery_address;
        this.customer_name = customer_name;
        this.customer_uid = customer_uid;
        this.total_amount = total_amount;
        this.payment_method = payment_method;
        this.ordered_at = ordered_at;
        this.extra_instructions = extra_instructions;
        this.order_status=order_status;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public ArrayList<String> getOrdered_items() {
        return ordered_items;
    }

    public void setOrdered_items(ArrayList<String> ordered_items) {
        this.ordered_items = ordered_items;
    }

    public String getShort_time() {
        return short_time;
    }

    public void setShort_time(String short_time) {
        this.short_time = short_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_uid() {
        return customer_uid;
    }

    public void setCustomer_uid(String customer_uid) {
        this.customer_uid = customer_uid;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getOrdered_at() {
        return ordered_at;
    }

    public void setOrdered_at(String ordered_at) {
        this.ordered_at = ordered_at;
    }

    public String getExtra_instructions() {
        return extra_instructions;
    }

    public void setExtra_instructions(String extra_instructions) {
        this.extra_instructions = extra_instructions;
    }

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
