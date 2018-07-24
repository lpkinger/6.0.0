package com.uas.erp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import com.uas.erp.core.DateUtil;
import com.uas.erp.core.bind.Constant;

public class CalcDay {
	public static String begin = "" ;
    public static String end = "" ;
    public static String now = new java.sql.Date( new Date().getTime()).toString();
    public static String group = null;
    public CalcDay(){
    	
    }
    public static String getCalcDay(Date date) throws ParseException{
    	group = null;
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	long otherTime = sdf.parse(sdf.format(date)).getTime();
    	calcToday(otherTime);
    	calcYesterday(otherTime);
    	calcThisWeek(otherTime);
    	calcLastWeek(otherTime);
    	calcThisMonth(otherTime);
    	calcLastMonth(otherTime);
    	return group == null ? "VII.其它" : group;
    }
    public static void calcToday(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	    begin = now;
    	    end = now;
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "I.今天";
        	}
    	}
    }
    public static void calcYesterday(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	GregorianCalendar calendar = new GregorianCalendar();
    	    calendar.add(GregorianCalendar.DATE, - 1 );
    	    begin = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    end = begin;
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "II.昨天";
        	}
    	}
    }
    public static void calcThisWeek(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	GregorianCalendar calendar = new GregorianCalendar();
    	    end = now;
    	    int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK) - 2 ;
    	    if (minus > 0 ){
    		    calendar.add(GregorianCalendar.DATE, - minus);
    		    begin = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    }
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "III.本周";
        	}
    	}
    }
    public static void calcLastWeek(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	GregorianCalendar calendar = new GregorianCalendar();
    	    int minus = calendar.get(GregorianCalendar.DAY_OF_WEEK) - 1 ;
    	    calendar.add(GregorianCalendar.DATE, - minus);
    	    end = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    calendar.add(GregorianCalendar.DATE, -6 );
    	    begin = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "IV.上周";
        	}
    	}
    }
    public static void calcThisMonth(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	GregorianCalendar calendar = new GregorianCalendar();
    	    end = now;
    	    int dayOfMonth = calendar.get(GregorianCalendar.DATE);
    	    calendar.add(GregorianCalendar.DATE, - dayOfMonth + 1 );
    	    begin = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "V.本月";
        	}
    	}
    }
    public static void calcLastMonth(long otherTime) throws ParseException {
    	if(group == null){
    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        	GregorianCalendar calendar = new GregorianCalendar();
    	    calendar.set(calendar.get(GregorianCalendar.YEAR),calendar.get(GregorianCalendar.MONTH), 1 );
    	    calendar.add(GregorianCalendar.DATE, - 1 );
    	    end = new java.sql.Date(calendar.getTime().getTime()).toString();
    	    int month = calendar.get(GregorianCalendar.MONTH) + 1 ;
    	    begin = calendar.get(GregorianCalendar.YEAR) + "-" + month + "-01" ;
    	    long beginTime = sdf.parse(begin).getTime();
        	long endTime = sdf.parse(end).getTime();
        	if(otherTime >= beginTime && otherTime <= endTime){
        		group =  "VI.上月";
        	}
    	}
    }
    static final String[] weeks = new String[]{"日", "一", "二", "三", "四", "五", "六"};
    @SuppressWarnings("deprecation")
	public static String calcDate(String date){
    	Date d = DateUtil.parseStringToDate(date, Constant.YMD_HMS);
    	return "星期" + weeks[d.getDay()];
    }
}
