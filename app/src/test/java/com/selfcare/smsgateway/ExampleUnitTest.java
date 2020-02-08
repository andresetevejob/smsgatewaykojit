package com.selfcare.smsgateway;

import org.junit.Test;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        long timelong = 1579352420578l;
        long ctn = System.currentTimeMillis();
        long ct = System.currentTimeMillis() - 40000;
        String current = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(ct));
        String c = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(ctn));
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Timestamp(timelong));
        System.out.println(timeStamp);
        System.out.println(current);
        System.out.println(c);
        assertEquals(4, 2 + 2);
    }
}