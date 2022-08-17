/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.svg;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
//import java.awt.Desktop;

public class chromosome_painter {
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
        String s = paint("");
        
    }
    
     public static String paint(String qry) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "match(s:cHapMap) return s.chr,max(s.pos) as pos";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        
        String q = "\"";
        String svg = "<svg xmlns=\"http://www.w3.org/2000/svg" + q + " width=" + q + "1200" + q + " height=" + q + "3500" + q + " viewBox=" + q + "0 2700 2500 350" + q + ">\n";
        Double ct = 0.0;
        int chr_max = Integer.valueOf(c[0].split(",")[1]);
        for (int i=0;i<c.length; i++)
        {
        String cs[] = c[i].split(",");
        Double w = scale(Integer.valueOf(cs[1]),chr_max);
        //svg = svg + "<text x=100 y=" + q + 40 * ct + q + ">" + cs[0] + "</text>";
        //svg = svg + "<text x=" + q + "20 y=" + q + " ct " + q + " class=" + q + "small" + q + ">" + cs[0] +  "</text>";
        ct = ct + 1;
        svg = svg + chr(i,ct,"lightgray",w);
        ct = ct + 1;
        svg = svg + chr(i,ct,"lightpink",w);
        ct = ct + 1;
        svg = svg + chr(i,ct,"lightblue",w);
        ct = ct + 1.5;
        }
        svg = svg + "\n</svg>";
        
        String fn =gen.neo4jlib.neo4j_info.Import_Dir + "chr.svg";
        File fnc = new File(fn);
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write(svg);
            fw.flush();
            fw.close();
        }
        catch(Exception e){}
//       Desktop.getDesktop().browse(svg); 
//Desktop.getDesktop().open(new File(fn));  
        return svg;
    }


public static String chr(int nbr,Double ct, String clr,Double width)
        {
            String q = "\"";
String s ="<rect x=" + q + "100" + q + " y=" + q + 40 * ct + q + " width=" + q + width + q + " height=" + q + "30" + q + " fill=" + q + clr + q + "/>\n";
return s;
}

public static Double scale(int seg_length,int max)
{
    return  2000.0 * seg_length/max;
}

}
