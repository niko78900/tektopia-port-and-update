/*
 * Decompiled with CFR 0.152.
 */
package com.websina.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {
    private Calendar calendar = Calendar.getInstance();

    public DateParser(Date date) {
        this.calendar.setTime(date);
    }

    public DateParser(String dateStr) {
        this(DateParser.toUtilDate(dateStr));
    }

    public static Date toUtilDate(String dateStr) {
        Date date = null;
        try {
            SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
            date = formater.parse(dateStr);
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                SimpleDateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
                date = formater.parse(dateStr);
            }
            catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return date;
    }

    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    public static java.sql.Date toSqlDate(String dateStr) {
        return DateParser.toSqlDate(DateParser.toUtilDate(dateStr));
    }

    public String getYear() {
        int year = this.calendar.get(1);
        return Integer.toString(year);
    }

    public String getXXYear() {
        return this.getYear().substring(2, 4);
    }

    public int getMonth() {
        return this.calendar.get(2) + 1;
    }

    public int getDayOfMonth() {
        return this.calendar.get(5);
    }

    public int getDayOfWeek() {
        return this.calendar.get(7);
    }

    public static void main(String[] args) {
        DateParser parser = new DateParser(args[0]);
        System.out.println("year=" + parser.getYear());
        System.out.println("year2=" + parser.getXXYear());
        System.out.println("month=" + parser.getMonth());
        System.out.println("day=" + parser.getDayOfMonth());
        System.out.println("week=" + parser.getDayOfWeek());
    }
}

