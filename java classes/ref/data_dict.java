/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.ref;

import gen.neo4jlib.neo4j_qry;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class data_dict {
    @UserFunction
    @Description("Developers only. Requires reference files to build the dictionary")

    public String create_data_dictionary(
        @Name("filePath")
          String filePath
  )
   
         { 
             
        String s = create_dict(filePath);
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String create_dict(String filePath) 
    {
        String cq="";
        
       gen.neo4jlib.neo4j_qry.qry_write("match (d:DataDictionary) delete d");
       gen.neo4jlib.neo4j_qry.CreateIndex("DataDictionary", "element");
       gen.neo4jlib.neo4j_qry.CreateIndex("DataDictionary", "name");
       gen.neo4jlib.neo4j_qry.CreateIndex("DataDictionary", "property");
       
        String nfile = "prior_data_dict.csv";
        gen.neo4jlib.file_lib.get_file_transform_put_in_import_dir(filePath, nfile);
        
       //gen.neo4jlib.file_lib.get_file_transform_put_in_import_dir(filePath,  rfile);
        
        //call db.schema.nodeTypeProperties()
        
//re-create nodes from current database schema
//this deletes prior required and desc properties
        cq = "MATCH (p) WITH distinct p, keys(p) as pKeys UNWIND pKeys as Key with distinct labels(p) as name, Key as property, apoc.map.get(apoc.meta.cypher.types(p), Key, [true]) as datatype create (d:DataDictionary{element:'node',name:name[0],property:property,datatype:datatype})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);


        cq = "MATCH ()-[r]-() WITH distinct r, keys(r) as pKeys UNWIND pKeys as Key with distinct type(r) as name, Key as property, apoc.map.get(apoc.meta.cypher.types(r), Key, [true]) as datatype create (d:DataDictionary{element:'relationship',name:name,property:property,datatype:datatype})";
        gen.neo4jlib.neo4j_qry.qry_write(cq);
        
//bring in curated info            
          
        cq ="LOAD CSV WITH HEADERS FROM 'file:///prior_data_dict.csv' as line FIELDTERMINATOR '|' match (d:DataDictionary{element:toString(line.element), name:toString(line.name), property:toString(line.property)}) set d.required=toString(line.required), d.desc=toString(line.desc)";
        gen.neo4jlib.neo4j_qry.qry_write(cq);

//        cq = "LOAD CSV WITH HEADERS FROM 'file:///relationship_data_dict.csv' as line FIELDTERMINATOR '|' match (d:DataDictionary{element:'relationship', name:toString(line.relationship), property:toString(line.property)})  set d.required=toString(line.required),d.desc=toString(line.desc)";
//          gen.neo4jlib.neo4j_qry.qry_write(cq);

          cq= "match (d:DataDictionary) return d.element as element,d.name as name,d.property as property, d.datatype as datatype, case when d.required is null then '' else d.required end as required, case when  d.desc is null then '' else d.desc end as desc,count(*) as ct order by element,toUpper(name), property";
          
        String fout = "data_dict_" + gen.genlib.current_date_time.getDateTime() + ".csv";  
        gen.neo4jlib.neo4j_qry.qry_to_csv(cq, fout);
        try {
            Desktop.getDesktop().open(new File(gen.neo4jlib.neo4j_info.Import_Dir + fout));
        } catch (Exception ex) {
            //Logger.getLogger(data_dict.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "dictionary created";
    }
}
