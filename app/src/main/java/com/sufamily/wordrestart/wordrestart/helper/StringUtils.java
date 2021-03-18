package com.sufamily.wordrestart.wordrestart.helper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by N1245 on 2015-03-09.
 */
public class StringUtils {
    /**
     * 패턴 제거
     */
    public static String removeRex(String rex, String inp){
        Pattern numP = Pattern.compile(rex);
        Matcher mat = numP.matcher("");
        mat.reset(inp);
        inp = mat.replaceAll("");
        return inp ;
    }

    /**
     * 날짜 비교
     */
    public static boolean compareTodayDate(String comp1, String comp2) throws ParseException {
        boolean returnValue = false;

        if (comp1 == null || comp2 == null) return false;

        if (!comp1.isEmpty() && !comp2.isEmpty()) {
            if (comp1.equals(comp2)) {
                returnValue = true;
            }
        }
        return (returnValue);
    }


    /**
     * Date to String
     */
    public static String getDate(Date date, String format) {
        DateFormat sdFormat = new SimpleDateFormat(format);
        String convertDate = sdFormat.format(date);
        return convertDate;
    }

    /**
     * String to Date
     */
    public static Date getDate(String input, String format) throws ParseException {
        DateFormat sdFormat = new SimpleDateFormat(format);
        Date convertDate = sdFormat.parse(input);
        return convertDate;
    }


}
