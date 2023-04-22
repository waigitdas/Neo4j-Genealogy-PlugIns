/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.genlib;

import static gen.tgs.tg_report_lenient.tg_report1;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
import static org.parboiled.common.StringUtils.right;
import static org.parboiled.common.StringUtils.substring;


public class gedcom_date_to_gen_date {
       @UserFunction
        @Description("Converts GEDCOM date to ccyymmdd")

public String ged_to_gen_date(
        @Name("gedcom_date") 
            String gedcom_date
        
  )
    {
        
        { 
       String newDate = convert_date(gedcom_date);
        return newDate;
        }
     }
   
    
    public static void main(String args[]) {
//        convert_date("BET JUL 1945 and 10 May 1946");
//         convert_date("JAN-FEB-MAR 1970");  //RootsMagic date
    }
    
    public String convert_date(String gedcom_date) {
        String d ="";
        String y ="0000";
        String dy = "00";
        String mostr = "00";
        String newDate = "" ;
        int rtnDate=0; 
        try{ 
        d = fix_date(gedcom_date).strip();
        if (d.length() > 9)  { //full date
                y = d.substring(d.length()-4).strip();
                dy = d.substring(0,d.indexOf(" "));
                mostr =getMonthNumber(d.replace(y,"").replace(dy,"").strip());
                if (dy.length()==1){dy = "0" + dy;}
                 newDate = y + mostr + dy;
            }
        else if (d.length() < 9 && d.length() >4) { //month year only
                y = d.substring(d.length()-4).strip();
                mostr =getMonthNumber(d.replace(y,"").strip());
                newDate = y + mostr;
            }
        else  { //year only
                newDate = d;
            }
    //        System.out.println(d);
    //        System.out.println(mostr);
    //        System.out.println(dy);
    //        System.out.println(y);
        
        rtnDate=Integer.parseInt(newDate);
        if(rtnDate==0){newDate="";}
}
        
        catch (Exception e) {newDate="";}
        
               return newDate;
    }

        
    
    private static String fix_date(String d){
        String r = d.toLowerCase().replace("bef", "");
        r = r.replace("aft", "");
        r = r.replace("abt", "");
        if (r != r.replace("bet", "")) {
            r = r.replace("bet", "");
            r = r.substring(0, r.indexOf("and")).strip();
            }        
        return r;
    }
    
    private static String getMonthNumber(String MoNm) {
        String monbr ="  ";
        switch (MoNm.toLowerCase()) {
            case "jan": monbr = "01";
            break;
            case "feb": monbr = "02";
            break;
            case "mar": monbr = "03";
            break;
            case "apr": monbr = "04";
            break;
            case "may": monbr = "05";
            break;
            case "jun": monbr = "06";
            break;
            case "jul": monbr = "07";
            break;
            case "aug": monbr = "08";
            break;
            case "sep": monbr = "09";
            break;
            case "oct": monbr = "10";
            break;
            case "nov": monbr = "11";
            break;
            case "dec": monbr = "12";
            break;
            default: monbr="00";
        }
        return monbr;
    }
}
