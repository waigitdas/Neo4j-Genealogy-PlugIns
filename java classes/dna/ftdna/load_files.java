/**
 * Copyright 2020 
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
public class load_files {
        @UserFunction
        @Description("Loads FTDNA DNA result CSV files from a named directory and specifically structured subdirectories. File names must NOT be altered after downloaded.")

public String load_ftdna_csv_files(
        @Name("root_directory") 
            String root_directory,
       @Name("curated_file") 
            String curated_file,
        @Name("db") 
            String db
  )
    {
        
        { 
        double tm = System.currentTimeMillis();
//String cq = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "}) with mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv return collect(mrca_indv) as mrca" ;    
        load_ftdna_files(root_directory, curated_file, db);
        double tmelapsed = System.currentTimeMillis() - tm;
        return "Completed in " + tmelapsed + " msec";
            }
     }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        load_ftdna_files("E:\\DAS_Coded_BU_2017\\Genealogy\\WhoAmI\\django\\data\\Teves Project\\","Teves curated matches.csv","test");
    }
    
    public static void load_ftdna_files(String root_dir,String curated_file, String db) {
        neo4j_info.neo4j_var(); //initialize user information
        
        //set up neo4j indices
//        neo4j_qry.CreateIndex("Kit","Kit", db);
//        neo4j_qry.CreateIndex("Kit","RN", db);
//        neo4j_qry.CreateIndex("DNA_Match","fullname", db);
//        neo4j_qry.CreateIndex("DNA_Match","RN", db);
//        neo4j_qry.CreateIndex("DNA_Match","kit", db);
//        neo4j_qry.CreateIndex("YMatch","fullname", db);
//        neo4j_qry.CreateIndex("Segment","Indx", db);
//        neo4j_qry.CreateIndex("Segment","cm", db);
//        neo4j_qry.CreateIndex("Segment","snp", db);
//        neo4j_qry.CreateIndex("str","name", db);
//        neo4j_qry.CreateIndex("tg","tgid", db);
//        neo4j_qry.CreateIndex("tg","strt_pos", db);
//        neo4j_qry.CreateIndex("tg","end_pos", db);
//        neo4j_qry.CreateIndex("email","fullname", db);
        //neo4j_qry.CreateIndex(, db);
        
        //load curation file
        file_lib.get_file_transform_put_in_import_dir(root_dir + curated_file, "RN_for_Matches.csv");
        neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///RN_for_Matches.csv' AS line FIELDTERMINATOR '|' merge (l:Lookup{fullname:toString(line.Match_Name),RN:toInteger(case when line.Curated_RN is null then 0 else line.Curated_RN end),kit:toString(case when line.Kit is null then '' else line.Kit end)})", db);
        
        File file = new File(root_dir);
        String[] directories = file.list(new FilenameFilter() {
        public boolean accept(File current, String name) {
            return new File(current, name).isDirectory();
          }
        });
        
       //System.out.println(Arrays.toString(directories));
        
        // iterating over subdirectories holding DNA data files
        for (int i = 0; i < directories.length; i++) {
            String x = directories[i];
            //System.out.print(x + "***\n");
            
            String[] paths;
            File f = new File(root_dir + directories[i]);
            paths = f.list();
            String kit;
            Long kit_rn =  Long.valueOf(0L);
            String kit_fullname="";
            int ct = 0;

                //        System.out.print(paths + "\n");

            //iterate over DNA data files, distinguishing their type before processing
            for (String pathitem : paths) {
                if (ct == 0){
                    String p[] = pathitem.split("_");
                    kit = p[0].strip();
                    
                    ct = ct + 1;
                    
                     try{kit_fullname = neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return l.fullname as fullname", db);}
                    catch (Exception e) {};
   
                    try{kit_rn =Long.parseLong(neo4j_qry.qry_str("match (l:Lookup{kit:'" + kit + "'}) return case when l.RN is null then 0 else l.RN end as kit_rn", db));}
                    catch (Exception e) {};
                  
                  
                    //placeholder match needed to create edges before full match set up
                    try
                    {
                        neo4j_qry.qry_write("merge (m:DNA_Match{fullname:'" + kit_fullname + "'}) set m.kit='" + kit + "', m.RN=" + kit_rn, db);
                    
                    neo4j_qry.qry_write("merge (k:Kit{kit:'" + kit + "', vendor:'ftdna, fullname:'" + kit_fullname + "' , RN:" + kit_rn + ", kit_desc:'" + pathitem + "})'", db);
                }
                catch (Exception e) {};
                    
//                    neo4j_qry.qry_str("", db);
//                    neo4j_qry.qry_str("", db);
//                    neo4j_qry.qry_str("", db);
//                    neo4j_qry.qry_str("", db);
                    
                 }
                else{ct=ct+1;}
          
                try{
                //System.out.println("\t" + pathitem );
            
                if (pathitem.contains("Family_Finder")){
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem,  pathitem);
    
                }
 
                if (pathitem.contains("Chromosome_Browser")){
                    String c = file_lib.readFileByLine(root_dir + directories[i] + "\\" + pathitem);
                    c = c.replace("|"," ").replace(",","|");
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
 
                               
                if (pathitem.contains("Y_DNA")){
                    file_lib.get_file_transform_put_in_import_dir(root_dir + directories[i] + "\\" + pathitem, pathitem);
    
                }
 
                
                }
                catch (Exception e){}
                }
            
                
            
        }   
        
        System.out.print("\n\nHere!!");
    }
}
