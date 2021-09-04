/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.dna.ftdna;
    import gen.neo4jlib.file_lib;
    import gen.neo4jlib.neo4j_info;
    import gen.neo4jlib.neo4j_qry;
            
    import java.io.File;
    import java.io.FilenameFilter;
    import java.io.IOException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.Arrays;
    import java.util.logging.Level;
    import java.util.logging.Logger;
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
        float tm = System.currentTimeMillis();
//String cq = "match (p1:Person{RN:" + rn1 + "})-[r1:father|mother*0..15]->(mrca:Person)<-[r2:father|mother*0..15]-(p2:Person{RN:" + rn2 + "}) with mrca.fullname + ' [' + mrca.RN + '] (' + left(mrca.BD,4) +'-' + left(mrca.DD,4) +')' as mrca_indv return collect(mrca_indv) as mrca" ;    
        load_ftdna_files(root_directory, curated_file, db);
        float tmelapsed = System.currentTimeMillis() - tm;
        return "Completed in " + tmelapsed + " msec";
            }
     }
   
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        load_ftdna_files("E:\\DAS_Coded_BU_2017\\Genealogy\\WhoAmI\\django\\data\\Teves Project\\","Teves curated matches.xlsx","test");
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
            //System.out.print(x + "\n");
            
            String[] paths;
            File f = new File(root_dir + directories[i]);
            paths = f.list();
            String kit;
            int ct = 0;

            //iterate over DNA data files, distinguishing their type before processing
            for (String pathitem : paths) {
                if (ct == 0){
                    String p[] = pathitem.split("_");
                    kit = p[0];
                    ct = ct + 1;
                    //System.out.println(kit);
                }
                else{ct=ct+1;}
                
                String c = file_lib.readFileByLine(root_dir + directories[i] + "\\" + pathitem);
                //System.out.println(root_dir + directories[i] + "\\" + pathitem);
                //System.out.println(c);
                c = c.replace("|"," - ").replace(",","|");
                file_lib.writeFile(c,neo4j_info.Import_Dir + pathitem);
              
                try{
                //System.out.println("\t" + pathitem );
            
                if (pathitem.contains("Family_Finder")){
                    //System.out.println("FF");
                }
 
                if (pathitem.contains("Chromosome_Browser")){
                    //System.out.println("CB");
                }
 
                               
                if (pathitem.contains("Y_DNA")){
                    //System.out.println("Y");
                }
 
                
                }
                catch (Exception e){}
                }
            
                
            
        }   
        
        System.out.print("\n\nHere!!");
    }
}
