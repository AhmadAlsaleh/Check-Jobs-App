package com.crazy_iter.checkjobs.Data;

/**
 * Created by CrazyITer on 3/21/2018.
 */

public class JobItem {
    private String jobId, position, time, days, company, phone, address, description, placeID;
    private boolean isStared, isDone = false;
    private double lowSalary, highSalary;
    private String gender = "Not Important";


    public JobItem(String jobId, String position, double lowSalary, double highSalary, String time, String days,
                   String company, String address, String phone, boolean isStared, String description, boolean isDone, String gender) {
        this.jobId = jobId;
        this.position = position;
        this.lowSalary = lowSalary;
        this.highSalary = highSalary;
        this.time = time;
        this.days = days;
        this.company = company;
        this.address = address;
        this.isDone = isDone;
        this.phone = phone;
        this.isStared = isStared;
        this.description = description;
        this.gender = gender;
    }

    public JobItem(String jobId, String position, double lowSalary, double highSalary, String time, String days,
                   String company, String address, String phone, boolean isStared, String description) {
        this.jobId = jobId;
        this.position = position;
        this.lowSalary = lowSalary;
        this.highSalary = highSalary;
        this.time = time;
        this.days = days;
        this.company = company;
        this.address = address;
        this.phone = phone;
        this.isStared = isStared;
        this.description = description;
    }

    public JobItem(String jobId, String position, double lowSalary, double highSalary, String time, String days,
                   String company, String address, String phone, boolean isStared, String description, boolean isDone) {
        this.jobId = jobId;
        this.position = position;
        this.lowSalary = lowSalary;
        this.highSalary = highSalary;
        this.time = time;
        this.days = days;
        this.company = company;
        this.address = address;
        this.phone = phone;
        this.isStared = isStared;
        this.description = description;
        this.isDone = isDone;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSalary() {
        return this.lowSalary + " - " + this.highSalary + " S.P";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isStared() {
        return isStared;
    }

    public void setStared(boolean stared) {
        isStared = stared;
    }

    public double getLowSalary() {
        return lowSalary;
    }

    public void setLowSalary(double lowSalary) {
        this.lowSalary = lowSalary;
    }

    public double getHighSalary() {
        return highSalary;
    }

    public void setHighSalary(double highSalary) {
        this.highSalary = highSalary;
    }
}
