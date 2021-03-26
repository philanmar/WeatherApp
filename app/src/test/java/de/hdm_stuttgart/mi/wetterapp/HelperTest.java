package de.hdm_stuttgart.mi.wetterapp;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

public class HelperTest {

    Helper helper = new Helper();

    @Test
    public void getWindDirectionSouth1() {
        assertEquals("Süden", helper.getWindDirection(190));
    }

    @Test
    public void getWindDirectionSouth2() {
        assertEquals("Süden", helper.getWindDirection(168.75f));
    }

    @Test
    public void getWindDirectionNorth1() {
        assertEquals("Norden", helper.getWindDirection(-10));
    }

    @Test
    public void getWindDirectionNorth2() {
        assertEquals("Norden", helper.getWindDirection(10));
    }

    @Test
    public void getWindDirectionEast() {
        assertEquals("Osten", helper.getWindDirection(80));
    }

    @Test
    public void getWindDirectionWest() {
        assertEquals("Westen", helper.getWindDirection(260));
    }

    @Test
    public void isNight1() {
        assertTrue(helper.isNight("01:00"));
    }

    @Test
    public void isNight2() {
        assertFalse(helper.isNight("18:00"));
    }

    @Test
    public void getCurrentTimeString() {
        assertEquals(new SimpleDateFormat("kk:mm").format(new Date()), helper.getCurrentTimeString());
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////
    // Negative Tests:
    @Test
    public void isNightNegativeTest1() {
        assertTrue(helper.isNight("23:00"));   // This Test should be negative!
    }

    @Test
    public void isNightNegativeTest2() {
        assertTrue(helper.isNight("Hallo, ich bin eine Uhrzeit"));  // This test should be negative!
    }

    @Test
    public void getWindDirectionSouthNegative() {
        assertEquals("Süden", helper.getWindDirection(234));    // Test should be Negative
    }

    @Test
    public void getWindDirectionNegative1() {
        assertEquals("Süden", helper.getWindDirection(-129));    // Test should be Negative
    }

    @Test
    public void getCurrentTimeStringNegative1() {
        assertEquals("01:61", helper.getCurrentTimeString());       // Test should be Negative
    }


}