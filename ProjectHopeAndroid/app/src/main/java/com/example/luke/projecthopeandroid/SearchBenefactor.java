package com.example.luke.projecthopeandroid;

/**
 * Created by Luke on 2017-09-19.
 */

public class SearchBenefactor {

    private String fullName;
    private String date;
    private int lastDonateAmount;
    private String beneID;

    public SearchBenefactor(String fullName, String date, int lastDonateAmount, String beneID){
        this.fullName = fullName;
        this.date = date;
        this.lastDonateAmount = lastDonateAmount;
        this.beneID = beneID;
    }

    public SearchBenefactor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class) for firebase
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getLastDonateAmount() {
        return lastDonateAmount;
    }

    public void setLastDonateAmount(int lastDonateAmount) {
        this.lastDonateAmount = lastDonateAmount;
    }

    public String getBeneID() {
        return beneID;
    }

    public void setBeneID(String beneID) {
        this.beneID = beneID;
    }
}
