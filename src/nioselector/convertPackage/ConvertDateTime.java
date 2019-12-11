package nioselector.convertPackage;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ConvertDateTime {
	/** 
       * 字符串转换为java.util.Date<br> 
       * 支持格式为 yyyy.MM.dd G 'at' hh:mm:ss z 如 '2002-1-1 AD at 22:10:59 PSD'<br> 
       * yy/MM/dd HH:mm:ss 如 '2002/1/1 17:55:00'<br> 
       * yy/MM/dd HH:mm:ss pm 如 '2002/1/1 17:55:00 pm'<br> 
       * yy-MM-dd HH:mm:ss 如 '2002-1-1 17:55:00' <br> 
       * yy-MM-dd HH:mm:ss am 如 '2002-1-1 17:55:00 am' <br> 
       * @param time String 字符串<br> 
       * @return Date 日期<br> 
       */
   public static Date string2Date(String str){
       SimpleDateFormat formatter;
       String time = str;
       int tempPos=time.indexOf("AD");
       time=time.trim();
       formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss z");
       if(tempPos>-1){
           time=time.substring(0,tempPos)+
                   "公元"+time.substring(tempPos+"AD".length());//china 
           formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss z");
       }

       tempPos=time.indexOf("-");
       if(tempPos>-1&&(time.indexOf(" ")<0)){
           formatter = new SimpleDateFormat ("yyyyMMddHHmmssZ");
       }
       else if((time.indexOf("/")>-1) &&(time.indexOf(" ")>-1)){
           formatter = new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss");
       }
       else if((time.indexOf("-")>-1) &&(time.indexOf(" ")>-1)){
           formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
       }
       else if((time.indexOf("/")>-1) &&(time.indexOf("am")>-1) ||(time.indexOf("pm")>-1)){
           formatter = new SimpleDateFormat ("yyyy-MM-dd KK:mm:ss a");
       }
       else if((time.indexOf("-")>-1) &&(time.indexOf("am")>-1) ||(time.indexOf("pm")>-1)){
           formatter = new SimpleDateFormat ("yyyy-MM-dd KK:mm:ss a");
       }


       ParsePosition pos = new ParsePosition(0);
       java.util.Date ctime = formatter.parse(time, pos);

       return ctime;
   }

   /**
    * 将时间字符串转换为日期格式，最终格式变为1970-01-01 + 输入 的时间
    *@param timeStr
    *@return Date
    * */
   public static Date timeString2Date(String timeStr) {
       Date date = null;
       SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
       ParsePosition pos = new ParsePosition(0);
       date = formatter.parse(timeStr,pos);
       return date;
   }

   /**
    * 将日期转换为字符串
    * 转换出的格式为:yyyy-MM-dd HH:mm:ss
    * @param date
    * @return String
    * */
   public static String date2String(Date date) {
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       String dateStr = format.format(date);
       return dateStr;
   }

   /**
    * 将日期转为时间字符串
    * @param date
    * @return String
    * */
   public static String date2TimeString(Date date) {
       String str = date2String(date);
       str = str.substring(str.indexOf(" ") + 1);
       return str;
   }
   
   /**
    * 	计算两个时间相差多少秒
    * */
   public static int calcTime2Sec(Date nowTime,Date lastTime) {
	   long a = nowTime.getTime(); 
	   long b = lastTime.getTime(); 
	   int c = (int)((a - b) / 1000); 
	   return c; 
   }
   
   public static Date addSec2Datd(Integer sec) {
	   //SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   Calendar c = new GregorianCalendar();
	   Date date = new Date();
	   c.setTime(date);//设置参数时间
	   c.add(Calendar.SECOND,sec);//把日期往后增加SECOND 秒.整数往后推,负数往前移动
	   date=c.getTime(); //这个时间就是日期往后推一天的结果
	   return date;
   }
   
}
