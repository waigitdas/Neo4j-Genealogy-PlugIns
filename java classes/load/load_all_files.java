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
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;

public class load_all_files {
    @UserFunction
    @Description("Not working. Loads GEDCOM, FTDNA files, triangulation groups using lookup file for locations of files.")

    public String load_everything(
        @Name("Gedcom_Fam_Tag") 
            String Gedcom_Fam_Tag

    )   
        { 
        String s = load_files(Gedcom_Fam_Tag);
                return s;
        }
        
    public  String load_files(String famTag) {
        gen.neo4jlib.neo4j_info.neo4j_var();  //initialize variables
        gen.conn.connTest.cstatus();
        gen.gedcom.upload_gedcom.load_gedcom(famTag);
        gen.dna.load_ftdna_files.load_ftdna_files();
        gen.tgs.load_tgs_from_template.load_tgs_from_csv();
       
        return "Completed";
    }
    
    
    public void main(String args[]) {
        // TODO code application logic here
    }
}
