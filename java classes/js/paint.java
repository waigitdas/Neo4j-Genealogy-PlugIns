/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.js;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.regex.Pattern;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
//import java.awt.Desktop;

public class paint {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String chr_paint(
        @Name("cq") 
            String cq
  )
   
         { 
             
        paint(cq);
         return "";
            }

    
    
    public static void main(String args[]) {
        String s = paint("02:078837866:099156098;04:021883570:047605641");
        
    }
    
     public static String paint(String segs) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "match(s:cHapMap) return s.chr,max(s.pos) as pos";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        
        String q = "\"";
        String js= "<!doctype html>\n" +
"<html>\n<head>\n<meta charset=" + q + "utf-8" + q + ">\n<title>GFG Chromosome Painter</title>\n<link rel=" + q + "stylesheet" + q + " type=" + q + "text/css" + q + " href=" + q + "https://blobswai.blob.core.windows.net/gfgpaint/gfg_paint.css" + q + ">\n</head>\n<body class=" + q + "gfg" + q + ">\n";
        Double ct = 0.0;
        int chr_max = Integer.valueOf(c[0].split(",")[1]);
        
        js = js + "<div id" + q + "chr" +  q + ">\n";
        
        for (int i=0;i<c.length; i++)
        {
        String cs[] = c[i].split(",");
        Double w = scale(Integer.valueOf(cs[1]),chr_max);
 
       js = js  + "<h4>" + cs[0] + "</h4><div class=" + q + "cp" + q + " id=" + q + "p" + cs[0] + q + " style=" + q + "width:" + w  + "%;" + q + "><span class=" + q + "nt" + q + "></span><div class=" + q + "both" + q + ">&nbsp;</div><div class=" + q + "pat" + q + "><div style=" + q + "background-position-x: center; width:15%; background-color:green" + q + ">&nbsp;</div><span style=" + q + "background-position-x: center; width:5%; background-color:purple" + q + ">&nbsp;</span></div><div class=" + q + "mat" + q + ">&nbsp;</div></div>\n\n\n";
        }
        js = js + "</div id" + q + "chr" +  q + ">\n";

        
        js = js + "\n</body>\n</html\n>";
        
        String fn =gen.neo4jlib.neo4j_info.Import_Dir + "chr.html";
        File fnc = new File(fn);
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write(js);
            //fw.flush();
            //fw.close();
        }
        catch(Exception e){}
        
        
        String seg[] = segs.split(";");
        for (int i=0; i<seg.length; i++)
        {
            String sg[] = seg[i].split(Pattern.quote(":"));
            
        }
        
//       Desktop.getDesktop().browse(svg); 
//Desktop.getDesktop().open(new File(fn));  
        
        try{
            fw.flush();
            fw.close();
        }
        catch(Exception e){}
        return js;
    }


public static String chr(int nbr,Double ct, String color,Double width)
        {
            String q = "\"";
String s ="<rect x=" + q + "100" + q + " y=" + q + 40 * ct + q + " width=" + q + width + q + " height=" + q + "30" + q + " fill=" + q + color + q + "/>\n";
return s;
}

public static Double scale(int seg_length,int max)
{
    return  75.0 * seg_length/max;
}

}
