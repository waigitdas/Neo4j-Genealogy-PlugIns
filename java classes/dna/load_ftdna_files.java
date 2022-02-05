/**
 * Copyright 2021 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
    import gen.neo4jlib.neo4j_qry;
     import gen.conn.connTest;
import gen.rel.add_rel_property;
           
    import java.io.File;
    import java.io.FileWriter;
    import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
    import java.util.regex.Pattern;
    import org.neo4j.procedure.Name;
    import org.neo4j.procedure.UserFunction;
    import org.neo4j.procedure.Description;

public class load_ftdna_files {
        @UserFunction
        @Description("Loads FTDNA DNA result CSV files from a named directory and specifically structured subdirectories. File names must NOT be altered after downloaded. CSV files used to load Neo4 are in the Neo4j Import Directory.")

public  String load_ftdna_csv_files(
//        @Name("root_directory") 
//            String root_directory,
//       @Name("curated_file") 
//            String curated_file
  )
    {
        
        { 
        double tm = System.currentTimeMillis();
        String s = load_ftdna_files();
        double tmelapsed = System.currentTimeMillis() - tm;
        return "Completed in " + tmelapsed + " msec";
            }
     }
   
    
    public void main(String args[]) {
        load_ftdna_files();
    }
    
    public  String load_ftdna_files() {
        neo4j_info.neo4j_var_reload(); //initialize user information
        gen.conn.connTest.cstatus();

        String root_dir= gen.neo4jlib.neo4j_info.root_directory;
        String curated_file = gen.neo4jlib.neo4j_info.Curated_rn_gedcom_file;
        
        //set up neo4j indices
        neo4j_qry.CreateIndex("Kit","Kit");
        neo4j_qry.CreateIndex("Kit","RN");
        neo4j_qry.CreateIndex("DNA_Match","fullname");
        neo4j_qry.CreateIndex("DNA_Match","RN");
        neo4j_qry.CreateIndex("DNA_Match","kit");
        neo4j_qry.CreateIndex("DNA_YMatch","fullname");
        neo4j_qry.CreateIndex("DNA_YMatch","YHG");
        neo4j_qry.CreateIndex("Segment","Indx");
       //neo4j_qry.CreateIndex("Segment","cm");
        //neo4j_qry.CreateIndex("Segment","snp");
        //neo4j_qry.CreateIndex("str","name");
        neo4j_qry.CreateIndex("tg","tgid");
        neo4j_qry.CreateIndex("tg","strt_pos");
        neo4j_qry.CreateIndex("tg","end_pos");
        neo4j_qry.CreateIndex("email","fullname");
        neo4j_qry.CreateRelationshipIndex("match_segment","cm");
        neo4j_qry.CreateRelationshipIndex("match_segment","snp_ct");
        neo4j_qry.CreateRelationshipIndex("match_segment","m");
        neo4j_qry.CreateRelationshipIndex("match_segment","p");
        neo4j_qry.CreateRelationshipIndex("match_by_segment","cm");
        neo4j_qry.CreateRelationshipIndex("match_segment", "p_rn");
        neo4j_qry.CreateRelationshipIndex("match_segment", "m_rn");
 
        try{ //will fail if index already exists
            neo4j_qry.CreateCompositeIndex("Segment", "chr,strt_pos,end_pos");
            neo4j_qry.CreateCompositeIndex("Segment", "strt_pos,end_pos");
            neo4j_qry.qry_write("CREATE INDEX rel_match_segement_composit2 FOR ()-[r:match_segment]-() ON (r.p,r.p_anc_rn,r.cm,r.snp_ct)");
        
        }
        catch (Exception e){};        //neo4j_qry.CreateIndex();
        
        //load curation file
       file_lib.get_file_transform_put_in_import_dir(root_dir + curated_file, "RN_for_Matches.csv");
       neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' merge (l:Lookup{fullname:toString(line.Match_Name),RN:toInteger(case when line.Curated_RN is null then 0 else line.Curated_RN end),Upload:toString(case when line.File_Upload is null then 'N' else line.File_Upload end),kit:toString(case when line.Kit is null then '' else line.Kit end),HG:toString(case when line.HG is null then '' else line.HG end)})");
        
        //create instances class to load reference data
        gen.ref.fam_rel fr = new gen.ref.fam_rel();
        fr.load_family_relationships();
        
        File file = new File(root_dir);
        String[] directories = file.list(new FilenameFilter() {
        @Override
        public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        
       
        String FileDNA_Match = "";
        String FileSegs = "";
        String YMatch = "";
        String chrPainter ="";
        
        String kit_fullname="";
        String cq ="";
        String[] kit_list = gen.neo4jlib.neo4j_qry.qry_to_csv("MATCH p=(k:Lookup{Upload:'Y'}) return k.kit as kit").replace("\"","").split("\n");
   
//        
        // iterating over subdirectories holding DNA data files
        for (int i = 0; i < directories.length; i++) {
            String x = directories[i];
            //System.out.print(x + "***\n");
            
            String[] paths;
            File f = new File(root_dir + directories[i]);
            
            paths = f.list();
            Arrays.sort(paths); //so that chromosome browser data goes before population data; making cb_version predictable
            
            String kit="";
            Long kit_rn =  Long.valueOf(0L);
            int ct = 0;
            Boolean hasDNAMatch = false;
            Boolean hasSegs = false;
            Boolean kit_found=false;
            Boolean hasEthnicity = false;
            
            String cb_version = "";  //chromosome browser file version
            
            //iterate over DNA data files, distinguishing their type before processing
            for (String pathitem : paths) {
                if (ct == 0){
                    String p[] = pathitem.split("_");
                    kit = p[0].strip();
                    for (int k=0;k<kit_list.length; k++){
                     //gen.neo4jlib.file_lib.writeFile("|" + kit + "|" + kit_list[k] + "|", gen.neo4jlib.neo4j_info.Import_Dir + "kit_list_" + i + "_" + k + ".csv");
                        if (kit.equals(kit_list[k])) {
                            //gen.neo4jlib.file_lib.writeFile(kit_list[k], gen.neo4jlib.neo4j_info.Import_Dir + "z_kit_list_" + k + ".csv");
                            kit_found = true;
                            break;
                        }
                    }
                    ct = ct + 1;
                    if (kit_found == true){
                        
                    try{
                    kit_fullname = neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return case when l.fullname is null then '' else l.fullname end as fullname");
                    kit_fullname=kit_fullname.replace("[", "").replace("]", "").replace("\"", "");
                                       }
                    catch (Exception e) {kit_fullname="";};
  
  
                  try{
                      kit_rn =Long.parseLong(neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return case when l.RN is null then 0 else l.RN end as kit_rn"));
                                     }
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
                 }
                else{ct=ct+1;}
        if (kit_found == true){
                try{
       
                //******************************************************************************
                //************  Family Finder: at-DNA  *****************************************
                //******************************************************************************
                if (pathitem.contains("Family_Finder")){
                         
                    hasDNAMatch=true;
                    FileDNA_Match = pathitem;
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem,  pathitem);
                    //Load kit at-DNA matches
                    cq= "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match  + "' AS line FIELDTERMINATOR '|' merge (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) set f.first_name=toString(case when line.First_Name is null then '' else line.First_Name end), f.middle_name=toString(case when line.Middle_Name is null then '' else line.Middle_Name end), f.surname=toString(case when line.Last_Name is null then '' else line.Last_Name end)";
                    neo4j_qry.qry_write(cq);
 
                    //Kit - match edges
                     cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match + "' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) match (k:Kit{kit:'" + kit + "'})  merge (k)-[r:KitMatch{suggested_relationship:toString(case when line.Relationship_Range is null then '' else line.Relationship_Range end), sharedCM:toFloat(case when line.Shared_DNA is null then 0.0 else line.Shared_DNA end), longest_block:toFloat(case when line.Longest_Block is null then 0.0 else line.Longest_Block end), linked_relationship:toString(case when line.Linked_Relationship is null then '' else line.Linked_Relationship end)}]->(f)";
                    neo4j_qry.qry_write(cq);
                    
//                    //shared matches
//                    cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match + "' AS line FIELDTERMINATOR '|' match(m1:DNA_Match{fullname:'" + kit_fullname + "'}) match(m2:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}] merge(m1)-[r:shared_match{cm:toFloat(line.Shared_DNA),longest_seg:toFloat(line.Longest_Block),rel:toString(line.Relationship_Range)}]-(m2)";
//                    neo4j_qry.qry_write(cq);
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
                    cb_version = "";
                    
                    //determine if old or new file type
                    String[] st = header.split(Pattern.quote("|"));
                    int fileTypeChrCol=0;
                                      
                    if (st[2].strip().equals("Chromosome")) {  //old format
                        fileTypeChrCol=2;
                        cb_version = "old";
                    }
                    else {    //new format
                        fileTypeChrCol=1;
                        cb_version = "new";
                        header =  "Name|" + header;
                    }
                    
                    
                    String[] ccc = c.split("\n");
                    File fn = new File(neo4j_info.Import_Dir + pathitem);
                    FileWriter fw = new FileWriter(fn);
                    fw.write(header + "\n");
                                        
                    //iterate each row
                     for (int ii=1; ii<ccc.length; ii++){
                        String[] xxx = ccc[ii].split(Pattern.quote("|"));
                        
                        if (xxx.length > 5) { //ignore defective row
                        String s = "";
                        
                        //iterate each column
                        for(int j=0; j<xxx.length; j++) {
                            xxx[j]=xxx[j].strip();
                            if (j==fileTypeChrCol) {  //chr
                                //add leading zero if <10 so sorting is okay
                                if (xxx[j].length()==1) {
                                    xxx[j] = "0" + xxx[j].strip();
                                }
                            }
                             s = s + xxx[j] + "|";
                        
                         
                      
                        
                        }  //next col

                        s = s +  "\n";

                        //add column to new format for consistent processing in subsequent steps
                        //new version dropped 1st column with the kit owner but this is required to efficiently create edges so it is added back
                         if (fileTypeChrCol == 2) {
                            fw.write(s);}
                         else{
                             fw.write(kit_fullname + "|" + s);
                         }
                         
                         }
                     }  //next row

                    fw.flush();    
                    fw.close();
}
 
                //Load unique chromosome segments shared by matches and currently iterated kit
                //Segment nodes use only chr, strt and end pos. Variation in other properties will be incorporated into edges because they are variations from match pairs
//                 String cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' AS line FIELDTERMINATOR '|' merge (l:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)  ,chr:toString(case when line.Chromosome is null then '' else line.Chromosome end), strt_pos:toInteger(case when line.Start_Location is null then 0 else line.Start_Location end), end_pos:toInteger(case when line.End_Location is null then 0 else line.End_Location end),cm:apoc.math.round(toFloat(case when line.Centimorgans is null then 0 else line.Centimorgans end),1),snp:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end)})";
                 cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' AS line FIELDTERMINATOR '|' merge (l:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)  ,chr:toString(case when line.Chromosome is null then '' else line.Chromosome end), strt_pos:toInteger(case when line.Start_Location is null then 0 else line.Start_Location end), end_pos:toInteger(case when line.End_Location is null then 0 else line.End_Location end)})";
                   neo4j_qry.qry_write(cq);
                               
                //******************************************************************************
                //***************  Y-DNA   *****************************************************
                //******************************************************************************
    if (pathitem.contains("Y_DNA")){
                    YMatch = pathitem;
                    
                    //add Y kit nodes
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem, pathitem);
                    neo4j_qry.qry_write("merge (k:Kit{kit:'" + kit + "', vendor:'ftdna', fullname:'" + kit_fullname + "' , RN:" + kit_rn + ", kit_desc:'" + directories[i] + "'})");
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + YMatch + "' AS line FIELDTERMINATOR '|' merge (f:DNA_YMatch{fullname:toString(case when line.Full_Name is null then '' else trim(line.Full_Name) end), first_name:toString(case when line.First_Name is null then '' else line.First_Name end), middle_name:toString(case when line.Middle_Name is null then '' else line.Middle_Name end), surname:toString(case when line.Last_Name is null then '' else line.Last_Name end),YHG:toString(case when line.Y_DNA_Haplogroup is null then '' else line.Y_DNA_Haplogroup end)})");
               
                    //add Y links to kits
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + YMatch + "' AS line FIELDTERMINATOR '|' match (f:DNA_YMatch{fullname:toString(trim(line.Full_Name))}) match (k:Kit{kit:'" + kit + "'})  merge (k)-[r:DNA_YKitMatch{gd:toInteger(line.Genetic_Distance),YHG:toString(case when line.Y_DNA_Haplogroup is null then '' else line.Y_DNA_Haplogroup end),terminal_SNP:toString(case when line.Terminal_SNP is null then '' else line.Terminal_SNP end),match_date:toString(case when line.Match_Date is null then '' else line.Match_Date end),str_diff:toString(case when line.Big_Y_STR_Differences is null then '' else line.Big_Y_STR_Differences end),str_compare:toString(case when line.Big_Y_STRs_Compared is null then '' else line.Big_Y_STRs_Compared end)}]->(f)");

               } // end Y DNA
 
    
                  //******************************************************************************
                //***************  chr painter  **************************************************
                //********************************************************************************
    if (pathitem.contains("detailed_segments_data")){
                    chrPainter = pathitem;
                    hasEthnicity=true;
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem, pathitem);
                   String c = file_lib.readFileByLine(root_dir + directories[i] + "\\" + pathitem);
                    c = c.replace("|"," ").replace(",","|").replace("\"", "`");
                    String[] cc = c.split("\n");
                    String header = cc[0].replace(" ","_");
                    c = c.replace(cc[0], header);
                    //cb_version = "";
                    
                    //determine if old or new file type
                    String[] st = header.split(Pattern.quote("|"));
                    int fileTypeChrCol=3;
                 
                    
                    String[] ccc = c.split("\n");
                    File fn = new File(neo4j_info.Import_Dir + pathitem);
                    FileWriter fw = new FileWriter(fn);
                    fw.write(header + "\n");
                                        
                    //iterate each row
                     for (int ii=1; ii<ccc.length; ii++){
                        String[] xxx = ccc[ii].split(Pattern.quote("|"));
                        
                        if (xxx.length > 5) { //ignore defective row
                        String s = "";
                        
                        //iterate each column
                        for(int j=0; j<xxx.length; j++) {
                            xxx[j]=xxx[j].strip();
                            if (j==fileTypeChrCol) {  //chr
                                //add leading zero if <10 so sorting is okay
                                if (xxx[j].length()==1) {
                                    xxx[j] = "0" + xxx[j].strip();
                                }
                            }
                             s = s + xxx[j] + "|";
                        
                         
                      
                        
                        }  //next col

                        s = s +  "\n";

                        fw.write(s);
                        
                         
                         }
                     }  //next row

                    fw.flush();    
                    fw.close();

                    
                    
                    //add continent, if new
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' merge (c:Continent{name:toString(line.Continent)})");

                   
                    //add super population if new
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' merge (sp:pop_group{name:toString(line.Super_Population)})");

                    
                    //add segment if new
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' merge (l:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)  ,chr:toString(case when line.Chromosome is null then '' else line.Chromosome end), strt_pos:toInteger(case when line.Start_Location is null then 0 else line.Start_Location end), end_pos:toInteger(case when line.End_Location is null then 0 else line.End_Location end)})");

                    //create seg property to tag segment as in ethnicity role
                     neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' match (s:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)}) set s.ethnicity=1");
                  
                    
                    //add edge between continent and super population
                    //create seg property to tag segment as in ethnicity role
                     neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|'  match (c:Continent{name:toString(line.Continent)}) match (sp:pop_group{name:toString(line.Super_Population)}) merge (c)-[r:continent_pop]->(sp)");
                    //add edge between super population and match
                     neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|'  match (m:DNA_Match{fullname:toString(line.Name)}) match (sp:pop_group{name:toString(line.Super_Population)}) merge (m)-[r:match_pop]->(sp)");
                    
                    
                    //add edge between super population and the segment (put the kit number and cm here as it may be different with different kits)
                    neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|'  match (s:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)}) match (sp:pop_group{name:toString(line.Super_Population)}) merge (s)-[r:segment_pop{kit:'" + kit + "'}]->(sp)");


            
 
    } // end chr painter
 
    
               }  // end try
                catch (Exception e){}
                }   ///end if kit found
            
      
//******************************************************************************
//***********  Graph Enhancements  *********************************************
//******************************************************************************
            if (kit_found == true){
     
if (hasSegs==true){
        //line 391 in VB.NET
        //match_segment edges with phasing paramenters m (match) and p (propositus) (for matches)
        String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' as line FIELDTERMINATOR '|' return line ";
        cq = " match (s:Segment{Indx:toString(case when line.Chromosome is null then '' else line.Chromosome end) + ':' + toString(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toString(case when line.End_Location is null then 0 else line.End_Location end) }) match (m:DNA_Match{fullname:toString(line.Match_Name)}) merge (m)-[r:match_segment{m:'" +  kit_fullname + "',p:toString(line.Match_Name),cm:toFloat(line.Centimorgans),snp_ct:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end),cb_version:'" + cb_version + "'}]-(s)";
        neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
}

if (hasEthnicity==true){
       //add edge between DNA_match node and segment // name is csv is different than match name!
       neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' match (s:Segment{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)}) match (m:DNA_Match{fullname:'" + kit_fullname + "'}) merge (m)-[r:match_pop_segment{cm:toFloat(line.Centimorgans), haplotype:toInteger(line.Haplotype)}]-(s) ");
                    
      //add property to match_segment edge tagging it as ethnicity relevant
        //neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///" + chrPainter + "' AS line FIELDTERMINATOR '|' match (s:Segment{{Indx:ltrim(toString(case when line.Chromosome is null then '' else line.Chromosome end)) + ':' + toInteger(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toInteger(case when line.End_Location is null then 0 else line.End_Location end)}) match (m:DNA_Match{fullname:toString(line.Name)}) merge (m)-[r:match_segment]-(s)");
 
}

        //Line 397 in VB.NET
        //Kit-Match edges
//if (hasDNAMatch==true) {
//            try{
////           cq = "LOAD CSV WITH HEADERS FROM 'file:///" + FileDNA_Match + "' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(case when line.First_Name is null then '' else line.First_Name end + case when line.Middle_Name is null then '' else ' ' + line.Middle_Name end + case when line.Last_Name is null then '' else ' ' + line.Last_Name end)}) match (k:Kit{kit:'" + kit + "'})  merge (k)-[r:KitMatch{suggested_relationship:toString(case when line.Suggested_Relationship is null then '' else line.Suggested_Relationship end), sharedCM:toFloat(case when line.Shared_cM is null then 0.0 else line.Shared_cM end), longest_block:toFloat(case when line.Longest_Block is null then 0.0 else line.Longest_Block end), linked_relationship:toString(case when line.Linked_Relationship is null then '' else line.Linked_Relationship end)}]-(f)";
////            neo4j_qry.qry_write(cq);
//            }
//            catch (Exception e)
//            {}
//        }

        //Line 406 in VB.NET
        //match-segments 2 unphased (for kit owners that are not found in matches' results)
        // most kit owners also appear in matches' list of matches; but not all. This query address them.
        if (hasSegs==true) {
            String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + FileSegs + "' as line FIELDTERMINATOR '|' return line ";
           cq = "match (s:Segment{Indx:toString(case when line.Chromosome is null then '' else line.Chromosome end) + ':' + toString(case when line.Start_Location is null then 0 else line.Start_Location end) + ':' + toString(case when line.End_Location is null then 0 else line.End_Location end) }) match (m:DNA_Match{fullname:'" + kit_fullname + "'}) merge (m)-[r:match_segment{p:'" + kit_fullname + "',m:toString(line.Match_Name),cm:toFloat(line.Centimorgans),snp_ct:toInteger(case when line.Matching_SNPs is null then 0 else line.Matching_SNPs end),cb_version:'" + cb_version + "'}]-(s)";
            neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
            
        }
        } //kit_found 
            }
} // next kit   

//same as Kit_Match
//        //Line 419 in VB.NET
////        //shared match csv
//        String cq = "MATCH (k:DNA_Match)-[r:match_segment]->(s:Segment) where k.fullname=r.p with r.p as Match1,r.m as Match2,count(*) as seg_ct, toInteger(sum((s.end_pos-s.strt_pos)/1000000.0)) as mbp, toFloat(sum(r.cm)) as cm,toFloat(min(r.cm)) as shortest_segment,toFloat(max(r.cm)) as longest_segment RETURN Match1,Match2,seg_ct,mbp,cm,shortest_segment,longest_segment order by cm desc";
//         neo4j_qry.qry_to_pipe_delimited(cq, "shared_matches.csv" );
//          
//        //Line 422 in VB.Net
//        //shared match edges
//          String lc = "LOAD CSV WITH HEADERS FROM 'file:///shared_matches.csv' as line FIELDTERMINATOR '|' return line ";
//          cq = "match (m1:DNA_Match{fullname:toString(line.Match1)}) match (m2:DNA_Match{fullname:toString(line.Match2)}) merge (m1)-[r:shared_match{seg_ct:toInteger(line.seg_ct),cm:toInteger(line.cm), mbp:toFloat(line.mbp),longest_seg:toFloat(line.longest_segment),shortest_seg:toFloat(line.shortest_segment)}]-(m2)"; 
//          neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);
//         
      //Links using curated data
         neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' match (f:DNA_Match{fullname:toString(line.Match_Name)}) set f.curated=1");
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
         
         //match_by_segment
         String mbsf =  "match_by_segment.csv";

         String Q = "\"";
        String qry = "MATCH (k:DNA_Match)-[r:match_segment]-(s:Segment) where s.chr<>'0X' with case when r.p<r.m then r.p else r.m end as Match1, case when r.p>r.m then r.p else r.m end as Match2,collect(distinct (s.end_pos-s.strt_pos)/1000000.0) as m,collect(distinct r.cm) as c,count(*) as segment_ct with Match1,Match2,m,c,segment_ct,apoc.coll.min(m) as shortest_mbp,apoc.coll.max(m) as longest_mbp,apoc.coll.min(c) as shortest_cm,apoc.coll.max(c) as longest_cm with Match1,Match2,apoc.coll.sum(m) as mbp,apoc.coll.sum(c) as cm,segment_ct,shortest_mbp,longest_mbp,shortest_cm,longest_cm RETURN Match1,Match2,segment_ct,mbp,cm,shortest_mbp,longest_mbp,shortest_cm,longest_cm order by cm desc";
         cq =  "call apoc.export.csv.query(" + Q + qry + Q + ",'" + mbsf + "', {delim:'|', quotes: false, format: 'plain'}) ";
         neo4j_qry.qry_write(cq);

                  String lc = "LOAD CSV WITH HEADERS FROM 'file:///" + mbsf + "' as line FIELDTERMINATOR '|' return line ";
        cq = " match (m1:DNA_Match{fullname:toString(line.Match1)}) match (m2:DNA_Match{fullname:toString(line.Match2)})  merge (m1)-[rz:match_by_segment{cm:toFloat(line.cm),mbp:toFloat(line.mbp),seg_ct:toInteger(line.segment_ct),shortest_cm:toFloat(line.shortest_cm),longest_cm:toFloat(line.longest_cm),shortest_mbp:toFloat(line.shortest_mbp),longest_mbp:toFloat(line.longest_mbp)}]-(m2) ";
           neo4j_qry.APOCPeriodicIterateCSV(lc, cq, 10000);

         String mbsf_x =  "match_by_segment_x.csv";
  
        qry = "MATCH (k:DNA_Match)-[r:match_segment]-(s:Segment) where s.chr='0X' with case when r.p<r.m then r.p else r.m end as Match1, case when r.p>r.m then r.p else r.m end as Match2,collect(distinct (s.end_pos-s.strt_pos)/1000000.0) as m,collect(distinct r.cm) as c,count(*) as segment_ct with Match1,Match2,m,c,segment_ct,apoc.coll.min(m) as shortest_mbp,apoc.coll.max(m) as longest_mbp,apoc.coll.min(c) as shortest_cm,apoc.coll.max(c) as longest_cm with Match1,Match2,apoc.coll.sum(m) as mbp,apoc.coll.sum(c) as cm,segment_ct,shortest_mbp,longest_mbp,shortest_cm,longest_cm RETURN Match1,Match2,segment_ct,mbp,cm,shortest_mbp,longest_mbp,shortest_cm,longest_cm order by cm desc";
         cq =  "call apoc.export.csv.query(" + Q + qry + Q + ",'" + mbsf_x + "', {delim:'|', quotes: false, format: 'plain'}) ";
         neo4j_qry.qry_write(cq);
         
        cq = "LOAD CSV WITH HEADERS FROM 'file:///" + mbsf_x + "' as line FIELDTERMINATOR '|'  match (m1:DNA_Match{fullname:toString(line.Match1)})-[r:match_by_segment]-(m2:DNA_Match{fullname:toString(line.Match2)}) set r.x_cm=toFloat(line.cm), r.x_seg_ct=toInteger(line.segment_ct), r.x_shortest_cm=toFloat(line.shortest_cm), r.x_longest_cm=toFloat(line.longest_cm)  ";
        neo4j_qry.qry_write((cq));
             
        //segment megabase pair property
        neo4j_qry.qry_write("CALL apoc.periodic.iterate('match (s:Segment) return s','set s.mbp=(s.end_pos-s.strt_pos)/1000000', {batchSize:1000, parallel:true})");
         
      
        return "completed";

    }
    
   
    
   
}
