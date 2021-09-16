/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna.ftdna;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
    import gen.neo4jlib.neo4j_qry;
            
    import java.io.File;
import java.io.FileWriter;
    import java.io.FilenameFilter;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.Arrays;
    import java.util.logging.Level;
    import java.util.logging.Logger;
import java.util.regex.Pattern;
    import java.util.stream.Stream;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;

/**
 *
 * @author david
 */
public class load_ftdna_files {
        @UserFunction
        @Description("Loads FTDNA DNA result CSV files from a named directory and specifically structured subdirectories. File names must NOT be altered after downloaded.")

public String load_ftdna_csv_files(
        @Name("root_directory") 
            String root_directory,
       @Name("curated_file") 
            String curated_file
  )
    {
        
        { 
        double tm = System.currentTimeMillis();
        load_ftdna_files(root_directory, curated_file);
        double tmelapsed = System.currentTimeMillis() - tm;
        return "Completed in " + tmelapsed + " msec";
            }
     }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        load_ftdna_files("E:\\DAS_Coded_BU_2017\\Genealogy\\WhoAmI\\django\\data\\Teves Project\\","Teves curated matches.csv");
    }
    
    public static void load_ftdna_files(String root_dir,String curated_file) {
        neo4j_info.neo4j_var(); //initialize user information
        
        //set up neo4j indices
        neo4j_qry.CreateIndex("Kit","Kit");
        neo4j_qry.CreateIndex("Kit","RN");
        neo4j_qry.CreateIndex("DNA_Match","fullname");
        neo4j_qry.CreateIndex("DNA_Match","RN");
        neo4j_qry.CreateIndex("DNA_Match","kit");
        neo4j_qry.CreateIndex("YMatch","fullname");
        neo4j_qry.CreateIndex("Segment","Indx");
        neo4j_qry.CreateIndex("Segment","cm");
        neo4j_qry.CreateIndex("Segment","snp");
        neo4j_qry.CreateIndex("str","name");
        neo4j_qry.CreateIndex("tg","tgid");
        neo4j_qry.CreateIndex("tg","strt_pos");
        neo4j_qry.CreateIndex("tg","end_pos");
        neo4j_qry.CreateIndex("email","fullname");
        //neo4j_qry.CreateIndex();
        
        //load curation file
        file_lib.get_file_transform_put_in_import_dir(root_dir + curated_file, "RN_for_Matches.csv");
        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' merge (l:Lookup{fullname:toString(line.Match_Name),RN:toInteger(case when line.Curated_RN is null then 0 else line.Curated_RN end),kit:toString(case when line.Kit is null then '' else line.Kit end)})");
        gen.ref.family_relationships.load_family_relationships();
        
        File file = new File(root_dir);
        String[] directories = file.list(new FilenameFilter() {
        public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        
       
        String FileDNA_Match = "";
        String FileSegs = "";
        String YMatch = "";
        
        String kit_fullname="";
        
        // iterating over subdirectories holding DNA data files
        for (int i = 0; i < directories.length; i++) {
            String x = directories[i];
            //System.out.print(x + "***\n");
            
            String[] paths;
            File f = new File(root_dir + directories[i]);
            paths = f.list();
            String kit="";
            Long kit_rn =  Long.valueOf(0L);
            int ct = 0;
            Boolean hasDNAMatch = false;
            Boolean hasSegs = false;
            
            //iterate over DNA data files, distinguishing their type before processing
            for (String pathitem : paths) {
                if (ct == 0){
                    String p[] = pathitem.split("_");
                    kit = p[0].strip();
                    
                    ct = ct + 1;
                    
                     try{kit_fullname = neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return l.fullname as fullname").replace("[", "").replace("]", "").replace("\"", "");}
                    catch (Exception e) {kit_fullname="";};
   
                    try{kit_rn =Long.parseLong(neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return case when l.RN is null then 0 else l.RN end as kit_rn"));}
                    catch (Exception e) {kit_rn=Long.valueOf(0L);};
                  
                  
                    //placeholder match needed to create edges before full match set up
                    try
                    {
                    neo4j_qry.qry_write("merge (m:DNA_Match{fullname:'" + kit_fullname + "'}) set m.kit='" + kit + "', m.RN=" + kit_rn);
                    
                }
                catch (Exception e) {};
     
                                   try
                    {
                    neo4j_qry.qry_write("merge (k:Kit{kit:'" + kit + "', vendor:'ftdna', fullname:'" + kit_fullname + "' , RN:" + kit_rn + ", kit_desc:'" + directories[i] + "'})");
                }
                catch (Exception e) {};
     
                
                 }
                else{ct=ct+1;}
          
                try{
       
                //******************************************************************************
                //************  Family Finder: at-DNA  *****************************************
                //******************************************************************************
          
                if (pathitem.contains("Family_Finder")){
                    hasDNAMatch=true;
                    FileDNA_Match = pathitem;
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem,  pathitem);
                    
                    //Load kit at-DNA matches
                    String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match  + "' AS line FIELDTERMINATOR '|' merge (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) set f.first_name=toString(case when line.First_Name is null then '' else line.First_Name end), f.middle_name=toString(case when line.Middle_Name is null then '' else line.Middle_Name end), f.surname=toString(case when line.Last_Name is null then '' else line.Last_Name end)";
                    neo4j_qry.qry_write(cq);
 
                    //Kit - match edges
                     cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match + "' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) match (k:Kit{kit:'" + kit + "'})  merge (k)-[r:KitMatch{suggested_relationship:toString(case when line.Suggested_Relationship is null then '' else line.Suggested_Relationship end), sharedCM:toFloat(case when line.Shared_cM is null then 0.0 else line.Shared_cM end), longest_block:toFloat(case when line.Longest_Block is null then 0.0 else line.Longest_Block end), linked_relationship:toString(case when line.Linked_Relationship is null then '' else line.Linked_Relationship end)}]->(f)";
                    neo4j_qry.qry_write(cq);
                    
                }

                //******************************************************************************
                //***********  Chromosome Browser  *********************************************
                //******************************************************************************
                if (pathitem.contains("Chromosome_Browser")){
                    hasSegs = true;
                    FileSegs = pathitem;
                    String c = file_lib.readFileByLine(root_dir + directories[i] + "\\" + pathitem);
                    c = c.replace("|"," ").replace(",","|").replace("\"", "`");
                    String[] cc = c.split("\n");
                    String header = cc[0].replace(" ","_");
                    c = c.replace(cc[0], header);
                    
                    String[] ccc = c.split("\n");
                    File fn = new File(neo4j_info.Import_Dir + pathitem);
                    FileWriter fw = new FileWriter(fn);
                    fw.write(header + "\n");
                    
                     for (int ii=1; ii<ccc.length; ii++){
                        String[] xxx = ccc[ii].split(Pattern.quote("|"));
                        
                        String s = "";
                        for(int j=0; j<xxx.length; j++) {
                             if (j==1) {  //chr
                                if (xxx[j].strip().length()==1) {
                                    xxx[j] = "0" + xxx[j].strip();
                                }
                                else {xxx[j]=xxx[j].strip();}
                             }
                             s = s + xxx[j] + "|";
                         }
                         s = s +  "\n";
                         fw.write(s);
                     }

                    fw.flush();    
                    fw.close();
}
 
                //Load unique chromosome segments shared by matches and currently iterated kit
                 String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' AS line FIELDTERMINATOR '|' merge (l:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)  ,chr:toString(case when line.Chromosome is null then '' else line.Chromosome end), strt_pos:toInteger(case when line.Start_Location is null then 0 else line.Start_Location end), end_pos:toInteger(case when line.End_Location is null then 0 else line.End_Location end),cm:apoc.math.round(toFloat(case when line.Centimorgans is null then 0 else line.Centimorgans end),1),snp:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end)})";
                 neo4j_qry.qry_write(cq);
                               
                //******************************************************************************
                //***************  Y-DNA   *****************************************************
                //******************************************************************************
               if (pathitem.contains("Y_DNA")){
                    YMatch = pathitem;
                    
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem, pathitem);
                    neo4j_qry.qry_write("merge (k:Kit{kit:'" + kit + "',fullname:'" + kit_fullname + "',RN:" + kit_rn + ", Kit_desc:'" + pathitem + "'})");
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + YMatch + "' AS line FIELDTERMINATOR '|' merge (f:DNA_YMatch{fullname:toString(case when line.Full_Name is null then '' else trim(line.Full_Name) end), first_name:toString(case when line.First_Name is null then '' else line.First_Name end), middle_name:toString(case when line.Middle_Name is null then '' else line.Middle_Name end), surname:toString(case when line.Last_Name is null then '' else line.Last_Name end),YHG:toString(case when line.Y_DNA_Haplogroup is null then '' else line.Y_DNA_Haplogroup end)})");
               }
 
               }
                catch (Exception e){}
                }
            
        
//******************************************************************************
//***********  Graph Enhancements  *********************************************
//******************************************************************************
        
if (hasSegs==true){
        //line 391 in VB.NET
        //match_segment edges with phasing paramenters m (match) and p (propositus)
        String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' as line FIELDTERMINATOR '|' return line ";
        String cq = " match (s:Segment{Indx:toString(case when line.Chromosome is null then '' else line.Chromosome end) + ':' + toString(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toString(case when line.End_Location is null then 0 else line.End_Location end) }) match (m:DNA_Match{fullname:toString(line.Match_Name)}) merge (m)-[r:match_segment{hops:1,p:'" +  kit_fullname + "',m:toString(line.Match_Name),cm:toFloat(line.Centimorgans),snp_ct:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end)}]->(s)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
}

//Line 397 in VB.NET
        //Kit-Match edges
        if (hasDNAMatch==true) {
           String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match + "' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) match (k:Kit{kit:'" + kit + "'})  merge (k)-[r:KitMatch{suggested_relationship:toString(case when line.Suggested_Relationship is null then '' else line.Suggested_Relationship end), sharedCM:toFloat(case when line.Shared_cM is null then 0.0 else line.Shared_cM end), longest_block:toFloat(case when line.Longest_Block is null then 0.0 else line.Longest_Block end), linked_relationship:toString(case when line.Linked_Relationship is null then '' else line.Linked_Relationship end)}]->(f)";
            neo4j_qry.qry_write(cq);
    
        }

        //Line 406 in VB.NET
        //match-segments 2 unphased
        if (hasSegs==true) {
            String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' as line FIELDTERMINATOR '|' return line ";
            String  cq = "match (s:Segment{Indx:toString(case when line.Chromosome is null then '' else line.Chromosome end) + ':' + toString(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toString(case when line.End_Location is null then 0 else line.End_Location end) }) match (m:DNA_Match{fullname:'" + kit_fullname + "'}) merge (m)-[r:match_segment{hops:1,p:'" + kit_fullname + "',m:toString(line.Match_Name),cm:toFloat(line.Centimorgans),snp_ct:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end)}]->(s)";
                neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
            
        }
        
} // next kit   


        //Line 419 in VB.NET
        //shared match csv
        String cq = "MATCH (k:DNA_Match)-[r:match_segment]->(s:Segment) where k.fullname=r.p with r.p as Match1,r.m as Match2,count(*) as seg_ct, toInteger(sum((s.end_pos-s.strt_pos)/1000000.0)) as mbp, toFloat(sum(s.cm)) as cm,toFloat(min(s.cm)) as shortest_segment,toFloat(max(s.cm)) as longest_segment RETURN Match1,Match2,seg_ct,mbp,cm,shortest_segment,longest_segment order by cm desc";
         neo4j_qry.qry_to_pipe_delimited(cq, "shared_matches.csv" );
          
        //Line 422 in VB.Net
        //shared match edges
          String lc = "LOAD CSV WITH HEADERS FROM 'file:///shared_matches.csv' as line FIELDTERMINATOR '|' return line ";
          cq = "match (m1:DNA_Match{fullname:toString(line.Match1)}) match (m2:DNA_Match{fullname:toString(line.Match2)}) merge (m1)-[r:shared_match{seg_ct:toInteger(line.seg_ct),cm:toInteger(line.cm), mbp:toFloat(line.mbp),longest_seg:toFloat(line.longest_segment),shortest_seg:toFloat(line.shortest_segment)}]-(m2)"; 
          neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
         
      //Links using curated data
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(line.Match_Name)}) set f.RN=toInteger(line.Curated_RN)");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(line.Match_Name)}) set f.kit=toString(line.Kit)");
         //neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(line.Match_Name)}) set f.email=toString(line.email)\"");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (k:Kit{kit:toString(line.Kit)}) set k.RN=toInteger(line.Curated_RN)");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (k:Kit{kit:toString(line.Kit)}) set k.fullname=toString(line.Match_Name)");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(line.Match_Name)}) match (p:Person{RN:toInteger(line.Curated_RN)})  merge (p)-[r:Gedcom_DNA]->(f)");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|'  match (p:Person{RN:toInteger(line.Curated_RN)}) set p.kit=toString(line.Kit)");
         
         try
         {
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:YMatch) where trim(f.fullname)=toString(line.Match_Name)  set f.RN=toInteger(line.Curated_RN)");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:YMatch{fullname:toString(line.Match_Name)}) set f.kit=toString(line.Kit)");
         }
         catch (Exception e){}
         
         neo4j_qry.qry_write("match (k:Kit)  where k.RN>0 optional match (p:Person) where p.RN=k.RN set k.fullname=p.fullname,k.surname=p.surname");
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:YMatch{fullname:toString(line.Curated_RN)}) match (p:Person{RN:toInteger(line.Curated_RN)})  merge (p)-[:Gedcom_DNA]->(f)");
         neo4j_qry.qry_write("LOAD CSV With HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (k:Kit{kit:toString(line.Kit)}) match (p:Person{RN:toInteger(line.Curated_RN)})  merge (p)-[:Gedcom_Kit]->(k)");
         neo4j_qry.qry_write("LOAD CSV With HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (m:DNA_Match{fullname:line.Match_Name}) set m.notes=toString(trim(line.Notes))");

        }
}
