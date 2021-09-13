/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;
import gen.neo4jlib.neo4j_qry;
import gen.neo4jlib.file_lib;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class load_tgs_from_template {
    @UserFunction
    @Description("Loads curated triangulation group csv file to Neo4j and creates edges to segments, matches and persons.")

  public String load_curated_tg_file(
        @Name("CSVFilePath") 
            String CSVFilePath
  )
    {
        
        { 
        
        load_tgs_from_csv(CSVFilePath);
        
            }
return "Completed";
    }

  
    public static void main(String args[]) {
        load_tgs_from_csv("E://DAS_Coded_BU_2017/Genealogy/WhoAmI/django/data/Teves Project/Teves_tgs_20210909_0900.csv");
    }
    
    public static String load_tgs_from_csv(String csvFile) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        String SaveFileName = gen.neo4jlib.file_lib.getFileNameFromPath(csvFile);
        gen.neo4jlib.file_lib.parse_chr_containing_csv_save_to_import_folder(csvFile, 2);
   
        String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + SaveFileName + "' AS line FIELDTERMINATOR '|' merge (t:tg{tgid:toInteger(line.tg_id),chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),end_pos:toInteger(line.end_pos),cm:toFloat(line.cm),project:toString(line.project),mrca_rn:toInteger(line.mrca_rn)})";
        neo4j_qry.qry_write(cq);
        
        //delete any existing tg edges to avoid duplications
        neo4j_qry.qry_write("match (t:tg)-[r]-() delete r");
        
        //add tg_seg edge
        cq = "match (t:tg) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic + " merge (t)-[r:tg_seg]-(s)";
        neo4j_qry.qry_write(cq);

        //add match_tg edge
        cq = "match (t:tg)-[:tg_seg]->(s:Segment)<-[:match_segment]-(m:DNA_Match) merge (m)-[r:match_tg{tgid:t.tgid}]-(t)";
        neo4j_qry.qry_write(cq);

        //add person_tg edge
        cq = "MATCH p=(m1:DNA_Match)-[r:match_tg]->(t:tg) where m1.RN is not null with m1.RN as rn,t match (p:Person{RN:rn}) match (t2:tg{tgid:t.tgid}) merge (p)-[rt:person_tg]->(t2)";
        neo4j_qry.qry_write(cq);

        
        return "Completed tg upload and created edges of tgs to segments, matches and persons.";
    }
}
