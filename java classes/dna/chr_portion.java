/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class chr_portion {
    @UserFunction
    @Description("computes portion of chr for a segment")

    public Double chr_portion_of_segment(
        @Name("chr") 
            String chr,
        @Name("rel_id") 
            Long rel_id
  )
   
         { 
        gen.neo4jlib.neo4j_info.neo4j_var();
        Double p = get_portion(chr,rel_id);
         return p;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public Double get_portion(String c, Long r) 
    {
        String cq = "MATCH (s:Segment{chr:'" + c + "'})-[rs:match_segment]-() where id(rs)=" + r + " with s, rs match (cc:chr_cm{chr:s.chr}) RETURN round(rs.cm/cc.cm,2) as x ";
        try{
        Double d = Double.parseDouble(gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n")[0]);
        return d;
        }
        catch (Exception e) { 
            gen.neo4jlib.file_lib.writeFile("**\n" + cq  + "\n**\n\n" + e.getMessage(), "c://temp/errorlog.csv");
 
            return 1.0;
        }
    }
}
