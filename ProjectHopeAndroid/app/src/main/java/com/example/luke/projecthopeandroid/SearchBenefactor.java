package com.example.luke.projecthopeandroid;

/**
 * Created by Luke on 2017-09-19.
 */
//Search benefactor class
public class SearchBenefactor {
    //Declaration and Initialisation
    private String fullName;                        //Full name
    private String date;                            //Date
    private int lastDonateAmount;                   //Last donated amount
    private String beneID;                          //Benefactor id

    public SearchBenefactor(String fullName, String date, int lastDonateAmount, String beneID){ //Constructor
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
    }                           //Getters and setters

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
