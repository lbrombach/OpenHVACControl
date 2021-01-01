package org.OpenHVACControl.Zones;

public class Request {
    private int heatingStages;
    private int coolingStages;
    private boolean fanRequest;
    private long timeStateStarted;

    public Request() {
        heatingStages = 0;
        coolingStages = 0;
        fanRequest = false;
        timeStateStarted = System.currentTimeMillis();
    }

    public Request(Request request) {
        this.heatingStages = request.getHeatingStages();
        this.coolingStages = request.getCoolingStages();
        this.fanRequest = request.isFanRequested();
        this.timeStateStarted = request.getTimeStateStarted();
    }

    public Request(int heatingStages, int coolingStages, boolean fanRequest) {
        this.heatingStages = heatingStages;
        this.coolingStages = coolingStages;
        this.fanRequest = fanRequest;
        timeStateStarted = System.currentTimeMillis();
    }

    public int getHeatingStages() {
        return heatingStages;
    }

    public void setHeatingStages(int heatingStages) {
        this.heatingStages = heatingStages;
    }

    public int getCoolingStages() {
        return coolingStages;
    }

    public void setCoolingStages(int coolingStages) {
        this.coolingStages = coolingStages;
    }

    public boolean isFanRequested() {
        return fanRequest;
    }

    public void setFanRequest(boolean fanRequest) {
        this.fanRequest = fanRequest;
    }

    public long getTimeStateStarted() {
        return timeStateStarted;
    }

    public void setTimeStateStarted(long timeStateStarted) {
        this.timeStateStarted = timeStateStarted;
    }

    @Override
    public String toString() {
        return "Request{" +
                "heatingStages=" + heatingStages +
                ", coolingStages=" + coolingStages +
                ", fanRequest=" + fanRequest +
                ", timeStateStarted=" + timeStateStarted +
                '}';
    }
}
