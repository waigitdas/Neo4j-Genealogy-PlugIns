/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.avatar;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class merge_segs {
    @UserFunction
    @Description("merge overlapping segments and compute cm.")

    public Double segment_cm(
        @Name("rn") 
            Long rn,
       @Name("side") 
            String side,
        @Name("segments") 
            String segments
  )
         { 
        Double cm = get_cM(rn, side,segments);
         return cm;
            }
    
    public static void main(String args[]) {
        String segs = "10:001328249:007617759,10:001524409:007617759,10:001530884:007759595,11:003656521:044871779,11:013057028:040094796,11:020162806:044446140,11:020855440:044874638,11:024891999:044046351,11:078939685:106458501,11:079006492:109665250,11:080828124:111908137,12:008964708:016222477,18:058363142:070979801,18:058824736:071475457";
        Double cm = get_cM(27L,"paternal",segs);
        //System.out.println(cm);
    }
    
     public static Double get_cM(Long rn, String side, String seg) 
    {   
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        List<String> segs = Arrays.asList(seg);
        ArrayList<ArrayList<Long>> data = new ArrayList<ArrayList<Long>>();
        String c[] = segs.get(0).split(Pattern.quote(","));
        String ct[];
        Long cs[][] = new Long[c.length][4];
        Boolean b = false;
        
        //iterate segments to set up arrays for further processing
        //parse once for efficiency
        for (int i=0; i<c.length; i++)
        {
            int gh=8;
            ct = c[i].split(Pattern.quote(":"));
            cs[i][0] = Long.parseLong(ct[0].replace("0X","23"));
            cs[i][1] = Long.parseLong(ct[1]);
            cs[i][2] = Long.parseLong(ct[2]);
            if (i==0) { //initialize list
                {
                data.add(new ArrayList<Long>());
                data.get(0).add(cs[0][0]);
                data.get(0).add(cs[0][1]);
                data.get(0).add(cs[0][2]);
            }
            }
        }
        
        //process overlaps and populate array data
        for (int i=0; i<c.length; i++) {
               overlap(cs,data,i);
        }
        
        //iterate avatar Segments to compute cm and create nodes.
        //Double cm_total = 0.0;
        //gen.dna.get_hapmap_cm hm = new gen.dna.get_hapmap_cm();
        int ff=0;
       for (int i=0; i<data.size(); i++)
        {
            //get cm from HapMap
            String chr = "";
            if (data.get(i).get(0)<10) 
            {
                chr = "0" +  String.valueOf(data.get(i).get(0));
            }
            else {chr = String.valueOf(data.get(i).get(0)); }
            if (data.get(i).get(0)==23){chr="0X";}
            
          String strt_pos = StringUtils.leftPad(String.valueOf(data.get(i).get(1)),9,"0");
          String end_pos = StringUtils.leftPad(String.valueOf(data.get(i).get(2)),9,"0");
            
            //create avSegment nodes and relationships
            gen.neo4jlib.neo4j_qry.qry_write("merge (s:avSegment{chr:'" + chr + "', strt_pos:" + data.get(i).get(1) + ", end_pos:" + data.get(i).get(2) + ", Indx:'" + chr + ":" + strt_pos + ":" + end_pos + "'})");

          gen.neo4jlib.neo4j_qry.qry_write("match (a:Avatar{RN:" + rn + "}) match(s:avSegment{Indx:'" + chr + ":" + strt_pos + ":" + end_pos + "'}) merge (a)-[r:avatar_avsegment{side:'" + side + "'}]-(s)");
            }
            
       //compute overlap cm
       for (int i=0; i<data.size(); i++)
       {
           for (int rw=0; rw<cs.length; rw++)
           {
               if (cs[rw][3]==i) 
               {  //iterate by group
                if (data.get(i).get(1)<= (cs[rw][2]) && data.get(i).get(2)>=(cs[rw][1]))  //is MSeg in CSeg?
                    {  //if overlapping. create relationship
                            String chr="";   
                            if (data.get(i).get(0)<10) 
                             {
                                 chr = "0" +  String.valueOf(data.get(i).get(0));
                             }

                             else {chr = String.valueOf(data.get(i).get(0)); }
                            if (data.get(i).get(0)==23){chr="0X";}
                           String strt_pos = StringUtils.leftPad(String.valueOf(data.get(i).get(1)),9,"0");
                             String end_pos = StringUtils.leftPad(String.valueOf(data.get(i).get(2)),9,"0");
                           String  cq = "match(s:Segment{chr:'" + chr.strip() + "', strt_pos:" + cs[rw][1] + ", end_pos:" + cs[rw][2] + "}) match(a:avSegment{Indx:'" + chr + ":" + strt_pos + ":" + end_pos + "'}) merge (a)-[r:avseg_seg]-(s)" ;
                             gen.neo4jlib.neo4j_qry.qry_write(cq );
                   
               
                    }  //next seg in group
                    //break;
           } //group change
       }  //new row
       }
       
        return 0.0; //cm_total;
    }
     
     //can we speed this up!
     public static ArrayList<ArrayList<Long>> overlap(Long cs[][],ArrayList<ArrayList<Long>> array, int rw) 
     {
         Boolean b = false;
         int j=0;
         int i=0;
             for (i=0; i<array.size(); i++)
             {
                 if (array.get(i).get(0).equals(cs[rw][0]) && array.get(i).get(1)<= (cs[rw][2]) && array.get(i).get(2)>=(cs[rw][1]) )
                 { //overlap found
                     array.get(i).set(0,cs[rw][0]);
                     
                     if(array.get(i).get(1) > cs[rw][1]) 
                     {
                        array.get(i).set(1, cs[rw][1]);
                     }
                     if(array.get(i).get(2) < cs[rw][2]) 
                     {
                         array.get(i).set(2, cs[rw][2]);
                     }
                     b = true;;
                     cs[rw][3] = Long.valueOf(i);
                     break;
                 }
                 
             }     
             
                 if (b.equals(false)) //add new range
                 {
                     array.add(new ArrayList<Long>());
                     array.get(array.size()-1).add(0,cs[rw][0]);
                     array.get(array.size()-1).add(1,cs[rw][1]);
                     array.get(array.size()-1).add(2,cs[rw][2]);
                     cs[rw][3] = Long.valueOf(array.size()-1);
 
                }
      return array;
     }
}
