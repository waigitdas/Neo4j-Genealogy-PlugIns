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
    @Description("Loads curated triangulation group csv file to Neo4j and creates edges to segments, matches and persons. Deleted prior tg nodes and edges before doing so. File path in user's curated private file")

  public String load_curated_tg_file(
        //@Name("CSVFilePath") 
        //    String CSVFilePath
  )
    {
        
        { 
        
        load_tgs_from_csv();
        
            }
return "Completed";
    }

  
    public static void main(String args[]) {
        gen.conn.connTest.cstatus();
        load_tgs_from_csv();
    }
    
    public static String load_tgs_from_csv() {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.conn.connTest.cstatus();
        String csvFile =gen.neo4jlib.neo4j_info.root_directory + gen.neo4jlib.neo4j_info.tg_file;
        String SaveFileName = gen.neo4jlib.file_lib.getFileNameFromPath(csvFile);
        gen.neo4jlib.file_lib.parse_chr_containing_csv_save_to_import_folder(csvFile, 3);
   
        neo4j_qry.qry_write("match ()-[r]-(t:tg) delete r");
        neo4j_qry.qry_write("match (t:tg) delete t");
        
        //delete any existing tg edges to avoid duplications
        neo4j_qry.CreateIndex("tg", "tgid");
        neo4j_qry.CreateIndex("tg", "chr");
        neo4j_qry.CreateIndex("tg", "start_pos");
        neo4j_qry.CreateIndex("tg", "end_pos");
        neo4j_qry.CreateIndex("tg", "cm");

        String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + gen.neo4jlib.neo4j_info.tg_file + "' AS line FIELDTERMINATOR '|' merge (t:tg{tgid:toInteger(line.tg_id),chr:toString(line.chr),strt_pos:toInteger(line.strt_pos),end_pos:toInteger(line.end_pos),cm:toFloat(case when line.cm is null then 0.0 else line.cm end),project:toString(line.project),mrca_rn:toInteger(line.mrca_rn)})";
        neo4j_qry.qry_write(cq);
        
        
        //add tg_seg edge
        cq = "match (t:tg) with t match (s:Segment) where " + gen.neo4jlib.neo4j_info.tg_logic + " merge (t)-[r:tg_seg]-(s)";
        neo4j_qry.qry_write(cq);

        //add match_tg edge
        cq = "match (t:tg)-[:tg_seg]-(s:Segment)-[:match_segment]-(m:DNA_Match) merge (m)-[r:match_tg{tgid:t.tgid}]-(t)";
        neo4j_qry.qry_write(cq);

        //add person_tg edge
        cq = "MATCH p=(m1:DNA_Match)-[r:match_tg]-(t:tg) where m1.RN is not null with m1.RN as rn,t match (p:Person{RN:rn}) match (t2:tg{tgid:t.tgid}) merge (p)-[rt:person_tg]->(t2)";
        neo4j_qry.qry_write(cq);

        
        return "Completed tg upload and created edges of tgs to segments, matches and persons.";
    }
}
