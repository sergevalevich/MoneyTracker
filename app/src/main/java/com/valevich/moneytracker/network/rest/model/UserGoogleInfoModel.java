package com.valevich.moneytracker.network.rest.model;

import com.google.gson.annotations.SerializedName;

public class UserGoogleInfoModel {

    @SerializedName("id")
    private String mId;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("verified_email")
    private Boolean mVerifiedEmail;

    @SerializedName("name")
    private String mName;

    @SerializedName("given_name")
    private String mGivenName;

    @SerializedName("family_name")
    private String mFamilyName;

    @SerializedName("link")
    private String mLink;

    @SerializedName("picture")
    private String mPicture;

    @SerializedName("locale")
    private String mLocale;


    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getPicture() {
        return mPicture;
    }

}


