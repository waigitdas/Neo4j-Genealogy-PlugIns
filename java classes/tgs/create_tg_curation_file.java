/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.tgs;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class create_tg_curation_file {
    @UserFunction
    @Description("Prepares curation file from current data, including HapMap computed cm. You specified whether to update all cm or just add this if missing.")

    public String create_updated_tg_curation_file(
    @Name("Update_All_cm") 
        Boolean Update_All_cm
//    @Name("rn2") 
//        Long rn2
  )
   
         { 
             
        String s = createfile(Update_All_cm);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String createfile(Boolean Update_All_cm) 
    {
        //add tg cm property if it is not already populated
        if (Update_All_cm==true){
           gen.neo4jlib.neo4j_qry.qry_write("match (t:tg) with t,gen.dna.hapmap_cm(t.chr,t.strt_pos,t.end_pos) as cm set t.cm = cm");
        }
        else{
            gen.neo4jlib.neo4j_qry.qry_write("match (t:tg) where t.cm is null with t,gen.dna.hapmap_cm(t.chr,t.strt_pos,t.end_pos) as cm set t.cm = cm");
        }

        //rename tg_name
        if(gen.neo4jlib.neo4j_info.tg_file!=""){
           gen.neo4jlib.neo4j_qry.qry_write("match (t:tg) set t.tg_name = t.chr + '-' + apoc.text.lpad(toString(t.strt_pos/1000000),3,'0') + '-' + apoc.text.lpad(toString(t.end_pos/1000000),3,'0')");
        
        gen.neo4jlib.neo4j_qry.qry_to_csv("match (t:tg) return t.tgid as tgid,t.tg_name as tg_name,t.chr as chr,t.strt_pos as strt_pos,t.end_pos as end_pos,t.project as project,t.cm as cm order by chr,strt_pos,end_pos", gen.neo4jlib.neo4j_info.tg_file);
        }
        
        return "The updated file is in the import directory. Move it to the directory specified for your project. We didnot overwritte the current file.";
    }
}
