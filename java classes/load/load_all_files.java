/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.load;
    import gen.conn.connTest;
    import gen.neo4jlib.neo4j_info;
    import gen.gedcom.upload_gedcom;
    import gen.dna.load_ftdna_files;
    import gen.tgs.load_tgs_from_template;
    import gen.load.load_ftdna_enhancements;

    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

public class load_all_files {
    @UserFunction
    @Description("Loads GEDCOM, FTDNA and curated files (GEDCOM-DNA links; triangulation groups) using a lookup file for locations of files. This function calls on several others in the PlugIn, enabling a simplier one-step loading process.")

    public String load_everything(
//        @Name("Gedcom_Fam_Tag") 
//            String Gedcom_Fam_Tag

    )   
        { 
        String s = load_files();
                return s;
        }
        
    public  String load_files() {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();  //initialize variables
        gen.conn.connTest.cstatus();
        
        upload_gedcom g = new upload_gedcom();
        g.load_gedcom();
        
        load_ftdna_files f = new load_ftdna_files();
        f.load_ftdna_files();
        
        load_tgs_from_template t = new load_tgs_from_template();
        t.load_tgs_from_csv();
       
       load_ftdna_enhancements e = new load_ftdna_enhancements();
       e.add_match_segment_properties();
        
        return "Completed";
    }
    
    
    public void main(String args[]) {
        // TODO code application logic here
    }
}
