/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.quality;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class match_identity {
    @UserFunction
    @Description("Identifies matches with uncertain identity due to duplicate names with different characteristics..")

    public String confounded_matches(
 
  )
   
         { 
             
        String s = get_matches();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String get_matches() 
    {
        String cq ="MATCH p=(k:Kit)-[[r:KitMatch]]->(m:DNA_Match) with k,m.fullname as m,collect(r.sharedCM) as cm,count(*) as ct with k,m,cm ,ct order by k.kit where ct>1 with m, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(cm)))) as cm, collect(k.kit) as kits, count(k) as kit_ct, sum(ct) as ct return m,size(cm) as ct,cm as cm_variants,kit_ct,kits,ct as total_ct order by ct desc";
        gen.excelLib.queries_to_excel.qry_to_excel(cq, "confounded_matches", "confounded", 1, "", "1:####;3:#####", "", true,"UDF:\nreturn gen.quality.confounded_matches()\n\ncypher query:\n" + cq + "\n\nReported here are DNA matches whose identity is confounded. They are identified because a name has more than one row in the Family Finder file on the kits listed for the match.\nThe duplicate rows were identified by variations in the cm; their ct and values are reported\nThe number of kits with the match repeated is also reported.\nThe total count is the sum of all the variants in all the kits. \nThere are several reasons identified: name is common, name is initials, different prefixes or suffixes (e.g., Rev., Dr., Jr, Sr, etc.). There are likely more reasons; please report them if identified! \n\nYou should be cautious with these matches. \n\nNOTE: a new property 'confounded='Y' was added to the identified DNA_Match nodes.", false);
        
        //add confounded property to DNA_Match nodes identified
        cq = "MATCH p=(k:Kit)-[r:KitMatch]->(m:DNA_Match) with k,m.fullname as m,collect(r.sharedCM) as cm,count(*) as ct with k,m,cm ,ct order by k.kit where ct>1 with m, apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect(cm)))) as cm, collect(k.kit) as kits, count(k) as kit_ct, sum(ct) as ct with collect(m) as mc match (mtag:DNA_Match) where mtag.fullname in mc set mtag.confounded='Y'";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
        return "completed";
    }
}
