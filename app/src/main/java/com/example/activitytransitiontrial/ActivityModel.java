package com.example.activitytransitiontrial;

public class ActivityModel {

    int _ID;
    String activity;
    String transition;
    String startTime;
    String date;
    String dayOfWeek;
    Double batteryPercentageStart;
    Double batteryPercentageEnd;
    Double batteryPercentageConsumed;
    Double latitude;
    Double longitude;

    public ActivityModel(String activity, String transition, String startTime, String date, String dayOfWeek,
                            Double batteryPercentageStart, Double batteryPercentageEnd, Double batteryPercentageConsumed, Double latitude, Double longitude){
        this.activity = activity;
        this.transition = transition;
        this.startTime = startTime;
        this.date = date;
        this.dayOfWeek = dayOfWeek;
        this.batteryPercentageStart = batteryPercentageStart;
        this.batteryPercentageEnd = batteryPercentageEnd;
        this.batteryPercentageConsumed = batteryPercentageConsumed;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ActivityModel(Double batteryPercentageEnd, Double batteryPercentageConsumed){
        this.batteryPercentageEnd = batteryPercentageEnd;
        this.batteryPercentageConsumed = batteryPercentageConsumed;
    }

    public void set_ID(int _ID){
        this._ID = _ID;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setTransition(String transition){
        this.transition = transition;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setBatteryPercentageStart(Double batteryPercentageStart) {
        this.batteryPercentageStart = batteryPercentageStart;
    }

    public void setBatteryPercentageEnd(Double batteryPercentageEnd) {
        this.batteryPercentageEnd = batteryPercentageEnd;
    }

    public void setBatteryPercentageConsumed(Double batteryPercentageConsumed) {
        this.batteryPercentageConsumed = batteryPercentageConsumed;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int get_ID() {
        return _ID;
    }

    public String getActivity() {
        return activity;
    }

    public String getTransition() {
        return transition;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getDate() {
        return date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public Double getBatteryPercentageStart() {
        return batteryPercentageStart;
    }

    public Double getBatteryPercentageEnd() {
        return batteryPercentageEnd;
    }

    public Double getBatteryPercentageConsumed() {
        return batteryPercentageConsumed;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
