package com.hm.almundo.callcenter.model;

import javax.validation.constraints.NotNull;
import java.util.Date;


/**
 * Clase Call que representa el modelo de una llamada
 *
 */
public class Call {

  private long id;
    @NotNull
    private String phoneNumber;
    private long duration;
    private long holdOnTime;
    private Date dateTimeStared;
    private Date dateTimeEnd;
    private String status;
    private String msg;
    private Employee employee;
    private boolean assigned;


    public Call(){
        this.assigned = false;
    }

    public Call(String phoneNumber, Employee employee){
        this.phoneNumber = phoneNumber;
        this.employee = employee;
        this.assigned = true;
        this.setStatus("Assigned");
        this.dateTimeStared = new Date();
    }

    public Call(String phoneNumber){
        this.phoneNumber = phoneNumber;
        this.assigned = false;
        this.dateTimeStared = new Date();
        this.setStatus("Unassigned");

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Date getDateTimeStared() {
        return dateTimeStared;
    }

    public void setDateTimeStared(Date dateTimeStared) {
        this.dateTimeStared = dateTimeStared;
    }

    public Date getDateTimeEnd() {
        return dateTimeEnd;
    }

    public void setDateTimeEnd(Date dateTimeEnd) {
        this.dateTimeEnd = dateTimeEnd;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.assigned = true;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setAssigned(boolean assigned) {
        this.assigned = assigned;
    }

    public long getHoldOnTime() {
        return holdOnTime;
    }

    public void setHoldOnTime(long holdOnTime) {
        this.holdOnTime = holdOnTime;
    }
}
