package com.hm.almundo.callcenter.model;

import com.hm.almundo.callcenter.config.Constants;
import com.hm.almundo.callcenter.util.Utils;

import java.util.Date;
import java.util.Random;

public class CallImpl implements ICall {

    private Random random;
    private Call call;

    public CallImpl() {
        random = new Random();

    }


    public CallImpl(Call call) {
        random = new Random();
        this.call = call;

    }

    @Override
    public void processCall() {

        int time = Utils.getRandomBeetwen(Constants.TIME_MIN, Constants.TIME_MAX);
        long timeInMili = Utils.convertSecondsToMiliseconds(time);
        call.setDuration(timeInMili);
        call.setStatus("Online");
        waitCallTime(call);
        call.setStatus("Finished");

    }

    private void waitCallTime(Call call){
        try {
            Thread.sleep(call.getDuration());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            call.setDateTimeEnd(new Date());
        }

    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }


}
