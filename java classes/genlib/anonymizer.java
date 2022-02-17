/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import jxl.common.Logger;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class anonymizer {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String anomymize_data(
    //https://stackoverflow.com/questions/53407858/how-to-randomly-generate-different-first-and-last-name-of-users-to-a-list-with-u
    
    )
   
         { 
             
        String s = anonymize();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String anonymize()
    {
              
        
        try {
            FileWriter fw = null;
            
            gen.neo4jlib.neo4j_info.neo4j_var_reload();
            
            
            gen.neo4jlib.neo4j_info.user_database = "teves2";
            
            
            String cq ="match (p:Person) with collect(distinct p.fullname) as fn1 match (m:DNA_Match) with fn1,collect(distinct m.fullname) as fn2 match ()-[r1:match_segment]-() with fn1,fn2,collect(distinct r1.m) as fn3 match (k:Kit) with fn1,fn2,fn3,collect(distinct k.fullname) as fn4 with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(fn1+fn2+fn3+fn4))) as fnames unwind fnames as names return names";
            String[] s = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
            
            //String[] s = gen.neo4jlib.file_lib.ReadFileByLineWithEncoding("E:/DAS_Coded_BU_2017/Genealogy/WhoAmI/Analytics/names.csv").split("\n");
            int nbr = s.length-1;
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = 10;
            Random random = new Random();
            String rn ="";
            File fn = new File("C:/Users/david/AppData/Local/Neo4j/Relate/Data/dbmss/dbms-84ac309c-05d0-4b7b-9902-726a3821cf7d/import/anonymizer.csv");
            fw = new FileWriter(fn);
            fw.write("name|anonymied\n");
            
            for(int x=1;x<=nbr;x++) {
                StringBuilder buffer = new StringBuilder(targetStringLength);
                for (int i = 0; i < targetStringLength; i++) {
                    int randomLimitedInt = leftLimit + random.nextInt(rightLimit - leftLimit + 1);
                    buffer.append((char) randomLimitedInt);
                    
                }
                String generatedString = buffer.toString();
                rn = rn + buffer + "\n";
                fw.write(s[x] + "|" + buffer + "\n");
            }       String[] r = rn.split("\n");
                fw.flush();
                fw.close();
          
                //end of creating csv
             
                //gen.neo4jlib.rename_node.rename("Person", "Person2");
                
            gen.neo4jlib.rename_property_preserve_original.rename("Person", "fullname");
                
            String lc = "LOAD CSV WITH HEADERS FROM 'file:///anonymizer.csv' as line FIELDTERMINATOR '|' return line ";
            cq = "match (p:Person) where p.orig_fullname=line.name set p.fullname=line.anonymized";
            neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 100000);
         
                
                
            return "completed";
    
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(anonymizer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "completed";
        } 
    }
//}
