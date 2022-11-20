/**
 * Copyright 2022-2023 
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
import java.awt.Desktop;

public class chromosome_painter {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String chr_paint(
        @Name("rn") 
            Long rn,
       @Name("file") 
            String file,
        @Name("title") 
            String title
  )
   
         { 
             
        paint(rn,file,title);
         return "";
            }

    
    
    public static void main(String args[]) {
        String s = paint(27L, "avatar_3_dna_painter_20220819_003713.csv","Avatar of Aubrey M Davis Sr");
        
    }
    
     public static String paint(Long rn,String file, String title) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cp[] = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding(gen.neo4jlib.neo4j_info.Import_Dir + file).split("\n");
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq = "match(s:cHapMap) return s.chr,max(s.pos) as pos";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
       
        int chr_lmargin=250;
        
        String q = "\"";
        String svg =  "<svg xmlns=\"http://www.w3.org/2000/svg" + q + " width=" + q + "100%" + q + " height=" + q + "2500" + q + " viewBox=" + q + "0 -20 2100 5500" + q + ">\n";
        //String svg =  "<svg xmlns=\"http://www.w3.org/2000/svg" + q + " width=" + q + "100%" + q + " height=" + q + "4500" + q + ">\n";
        //<html><head></html><body width='100%'>
        
         svg = svg + "<style>\n.small { font: italic 13px sans-serif; }\n.title { font: bold 60px sans-serif; }\n.chr { font: bold 40px sans-serif; ; }\n</style>\n";
                
                //
                
        int y = 40;
        int inc = 40;
             
        svg = svg + "<text x='0' y='" + y + "' class='title' fill='blue' transform='rotate(0,0,0)'>" + title + "</text>\n";
         
        Double chr1_width = 0.0;
        int chr_max = Integer.valueOf(c[0].split(",")[1]);
       int segmap[][][] = new int [24][3][1];  //chr,side, y-coordnate
        int ytc = 0;
               
        for (int i=0;i<c.length; i++)   //iterated chromosomes
        {
            String cs[] = c[i].split(",");
            Double w = scale(Integer.valueOf(cs[1]),chr_max);
           
        if (i==1){chr1_width=w;}
        y = y + inc;
        int yt = y + (2 * inc) ;
        ytc = y + (2 * inc)-10 ;
        svg = svg + "<circle cx='70' cy='" + ytc + "' r='40' stroke='black' stroke-width='3' fill='blue' />\n";
        svg = svg + "<text x='50' y='" + yt + "' class='chr' fill='white' transform='rotate(0,0,0)'>" +  cs[0] + "</text>\n";
 
  
            segmap[Integer.parseInt(cs[0])][0][0]= y;  //both
            svg = svg + chr(i,"lightgray",y,w);

            y = y + inc;
           segmap[Integer.parseInt(cs[0])][1][0]= y; //paternal
            svg = svg + chr(i,"lightblue",y, w);

            y = y + inc;
            segmap[Integer.parseInt(cs[0])][2][0]= y; //maternal
            svg = svg + chr(i,"lightpink",y, w);

            y = y + (inc);
        }
 

        //paint segments
        for (int i=1; i<cp.length; i++)  //skip header
        {
            svg = svg +  seg(cp[i],chr1_width, segmap);
        }

        y = y + 150;
            svg = svg + "<text alignment-baseline='baseline' x='0' y='" + y + "' class='chr' width='100' fill='black'>Explanations can go here</text>\n";
       y = y + 150;
           svg = svg + "<text alignment-baseline='baseline' x='0' y='" + y + "' class='chr' width='100' fill='black'>©2022 Graphs for Geneaogists</text>\n";
        
        //Legend
        int legend_y = segmap[19][0][0] -65 ;
        svg = svg + "<rect x='960' fill='white' y='" + legend_y + "' width='500' height='240' style=" + q + "stroke:black;stroke-width:3" + q + "/>\n";
        int incy=25;
        int yt = legend_y + incy + 20;

        svg = svg + "<text alignment-baseline='baseline' x='1000' y='" + yt + "' class='chr' width='100'  fill='black'>Legend</text>\n";
  
        legend_y = segmap[19][0][0];
        incy=25;
        yt = legend_y + incy;
   
   
    
        svg = svg + "<rect alignment-baseline='baseline' chr='none' fill='lightgray' x='1000' y='" + legend_y  + "' width='100' height='30' />\n";
        svg = svg + "<text alignment-baseline='baseline' x='1130' y='" + yt + "' class='chr' width='100'  fill='black'>both</text>\n";
  
        legend_y = legend_y + inc;
        yt = legend_y + incy;
        svg = svg + "<rect alignment-baseline='baseline' chr='none' fill='lightblue' x='1000' y='" + legend_y  + "' width='100' height='30' />\n";
        svg = svg + "<text alignment-baseline='baseline' x='1130' y='" + yt + "' class='chr' width='100' fill='black'>paternal</text>\n";
  
        legend_y = legend_y + inc;
        yt = legend_y + incy;
        svg = svg + "<rect alignment-baseline='baseline' chr='none' fill='lightpink' x='1000' y='" + legend_y  + "' width='100' height='30' />\n";
        svg = svg + "<text alignment-baseline='baseline' x='1130' y='" + yt + "' class='chr' width='100' fill='black'>maternal</text>\n";
  
        legend_y = legend_y + inc;
        yt = legend_y + incy;
        svg = svg + "<rect alignment-baseline='baseline' chr='none' fill='purple' x='1000' y='" + legend_y  + "' width='100' height='30' />\n";
        svg = svg + "<text alignment-baseline='baseline' x='1130' y='" + yt + "' class='chr' width='100' fill='black'>segments</text>\n";
  
       
        svg = svg + "\n</svg>";   
        //</body></html>";
        
        String fn =gen.neo4jlib.neo4j_info.Import_Dir + "chr_painter_" + rn +"_" + gen.genlib.current_date_time.getDateTime() + ".svg";
        File fnc = new File(fn);
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc, Charset.forName("UTF8"));
            fw.write(svg);
            fw.flush();
            fw.close();
        }
        catch(Exception e){}
//        java.awt.Desktop.getDesktop().browse(fnc);
        return svg;
    }


public static String chr(int nbr, String clr,int y, Double width)
        {
String s ="<rect chr='" + nbr + "' fill='" + clr + "' x='150' y='" + y + "' width='" + width + "' height='30' />\n";

return s;
}

public static String seg(String sg, Double wc, int[][][] segmap)
        {
            
           String ss[] = sg.split(",");
           int y = 0 ;
           int side_add=0;
           try{
            y = segmap[Integer.parseInt(ss[0])][0][0];

           if (ss[8].equals("paternal"))
           {
               y = segmap[Integer.parseInt(ss[0])][1][0];
               side_add=1;
           }
           if (ss[8].equals("maternal"))
           {
               y = segmap[Integer.parseInt(ss[0])][2][0];
               side_add=2;
           }
           }
           catch(Exception e){
           int hffg=0;
           }
            
            Double x = 150.0 + (Double.parseDouble(ss[1])*wc/249000000) ;
            String s ="<rect x='" + x + "' y='"  + y + "' width='" + (Double.parseDouble(ss[2])-Double.parseDouble(ss[1]))*wc/249000000 + "' height='30' fill='purple' />\n";
            return s;
}

     
public static Double scale(int seg_length,int max)
{
    return  1500.0 * seg_length/max;
}


}
