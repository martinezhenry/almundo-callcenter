package com.hm.almundo.callcenter.util;

import com.hm.almundo.callcenter.config.Constants;

import java.util.Random;

public class Utils {


    public static int getRandomBeetwen(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();

        return r.nextInt((max - min) + 1) + min;

    }


    public static long convertSecondsToMiliseconds(long secs){

        return secs * Constants.MILISECONDS_UNIT;
    }




}
