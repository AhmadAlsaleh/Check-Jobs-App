package com.crazy_iter.checkjobs.Data;

/**
 * Created by CrazyITer on 4/20/2018.
 */

public class User {
    private String id, name, imageURL, details;
    private boolean isPro, isFacebook;

    public User(String id, String name, String imageURL, String details, boolean isPro, boolean isFacebook) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        if ("null".equals(details)) {
            this.details = "";
        } else {
            this.details = details;
        }
        this.isPro = isPro;
        this.isFacebook = isFacebook;
    }

    public boolean isFacebook() {
        return isFacebook;
    }

    public void setFacebook(boolean facebook) {
        isFacebook = facebook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public boolean isPro() {
        return isPro;
    }

    public void setPro(boolean pro) {
        isPro = pro;
    }
}
