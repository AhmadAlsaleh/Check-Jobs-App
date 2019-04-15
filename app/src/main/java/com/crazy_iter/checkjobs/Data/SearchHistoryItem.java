package com.crazy_iter.checkjobs.Data;

/**
 * Created by CrazyITer on 4/10/2018.
 */

public class SearchHistoryItem {

    private String id, searchWord, place, salaryCurrency, hours, days, gender;

    public SearchHistoryItem(String id, String searchWord, String place, String salaryCurrency, String hours, String days, String gender) {
        this.id = id;
        this.searchWord = searchWord;
        this.place = place;
        this.salaryCurrency = salaryCurrency;
        this.hours = hours;
        this.days = days;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearchWord() {
        return searchWord;
    }

    public void setSearchWord(String searchWord) {
        this.searchWord = searchWord;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getSalaryCurrency() {
        return salaryCurrency;
    }

    public void setSalaryCurrency(String salaryCurrency) {
        this.salaryCurrency = salaryCurrency;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
