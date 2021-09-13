/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.neo4jlib.neo4j_qry;


public class family_relationships {

    
    public static void main(String args[]) {
        load_family_relationships();
    }
    
    public static void load_family_relationships() {
        gen.neo4jlib.file_lib.get_file_transform_put_in_import_dir("c:/Genealogy/Neo4j/Family_relationship_table.csv", "Family_relationship_table.csv");
        gen.neo4jlib.neo4j_qry.CreateIndex("fam_rel", "Indx");
        String cq = "LOAD CSV WITH HEADERS FROM 'file:///Family_relationship_table.csv' AS line FIELDTERMINATOR '|' merge (f:fam_rel{Indx:toString(line.Indx), path1:toInteger(line.path1), path2:toInteger(line.path2), nmrca:toInteger(line.nmrca), LowSharedCM:toInteger(line.LowSharedCM), MeanSharedCM:toInteger(line.MeanSharedCM), HighSharedCM:toInteger(line.HighSharedCM), relationship:toString(case when line.relationship is null then '' else line.relationship end)})";
        neo4j_qry.qry_write(cq);
    }
}
