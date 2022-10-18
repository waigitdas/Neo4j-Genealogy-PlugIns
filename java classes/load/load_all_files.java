/**
 * Copyright 2022
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
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

public class load_all_files {
    @UserFunction
    @Description("Loads GEDCOM, FTDNA and curated files (GEDCOM-DNA links; triangulation groups) using a lookup file for locations of files. This function calls on several others in the PlugIn, enabling a simplier one-step loading process.")

    public String load_everything(
        @Name("anc_rn") 
            Long anc_rn

    )   
        { 
        String s = load_files(anc_rn);
                return s;
        }
        
        public static void main(String args[]) {
            load_files(1L);
    }
        
    public static String load_files(Long anc_rn) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();  //initialize variables
        
        upload_gedcom g = new upload_gedcom();
        g.load_gedcom();
        
        
        load_ftdna_files f = new load_ftdna_files();
        f.load_ftdna_files();
        
        try{
        load_tgs_from_template t = new load_tgs_from_template();
        t.load_tgs_from_csv();
        }
        catch(Exception e){}
        
        //add x_gen_dist property
        load_ftdna_enhancements fe = new load_ftdna_enhancements();
        fe.add_match_segment_properties();
        
        try{
        gen.avatar.targeted_anc_enhance tge = new gen.avatar.targeted_anc_enhance();
        tge.add_enhancements(anc_rn);
            
        gen.avatar.create_avatars cav = new gen.avatar.create_avatars();
        cav.create_avatar_relatives(anc_rn);
        }
        catch(Exception e){}
        
        return "Completed";
    }
}
