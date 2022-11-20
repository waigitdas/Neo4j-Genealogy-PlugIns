/**
 * Copyright 2021-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;
    import gen.load.web_file_to_import_folder;
    import gen.neo4jlib.neo4j_qry;
    import org.neo4j.procedure.Description;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;


public class fam_rel {
  @UserFunction
  @Description("Loads reference data for relationships based on the number of hops in traversing a graph from two individuals to a common ancestor. Also loads data from the Shared Centimorgan Project with mean and range of expected shared cm for the relationships.")

    public String family_relationships(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
    {
        load_family_relationships();
        return "completed";
    }
    
    public void main(String args[]) {
        load_family_relationships();
    }
    
    public void load_family_relationships() {
        gen.neo4jlib.neo4j_info.neo4j_var();
        web_file_to_import_folder.url_file_to_import_dir("https://blobswai.blob.core.windows.net/gen-udf/Family_relationship_table.csv", "Family_relationship_table.csv");

        gen.neo4jlib.neo4j_qry.CreateIndex("fam_rel", "Indx");
        String cq = "LOAD CSV WITH HEADERS FROM 'file:///Family_relationship_table.csv' AS line FIELDTERMINATOR ',' merge (f:fam_rel{Indx:toString(line.Indx), path1:toInteger(line.path1), path2:toInteger(line.path2), nmrca:toInteger(line.nmrca), LowSharedCM:toInteger(line.LowSharedCM), MeanSharedCM:toInteger(line.MeanSharedCM), HighSharedCM:toInteger(line.HighSharedCM), relationship:toString(case when line.relationship is null then '' else line.relationship end)})";
        neo4j_qry.qry_write(cq);
    }
}
