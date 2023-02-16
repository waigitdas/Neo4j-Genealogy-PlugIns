/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.mt_research;

import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class tessellator {
   public static int tile_len = 100;
   public static int tile_partitions = 17000/tile_len;
   public static String[][] tile_seq = null;
   public static int tile_ct=tile_partitions;
   public static int tile_seq_ct[] = null; 
   public static int ckit[][] = null;
   public static String kit[] = null;
    @UserFunction
    @Description("Template used in creating new functions.")

    public String tessellate_kits(
        @Name("kit_dir") 
            String kit_dir
      )
   
         { 
             
        create_tiles(kit_dir);
         return "";
            }

    
    
    public static void main(String args[]) {
        create_tiles("E:/DAS_Coded_BU_2017/Genealogy/DNA/mt_haplotree_project/mt_DNA_sequences/entrez/");
    }
    
     public static String create_tiles(String dir) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();

        gen.neo4jlib.neo4j_qry.qry_write("match(k:seq_kit)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match(t:tile) delete t");
//        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_pos)-[r]-() delete r");
        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_kit) delete k");
//        gen.neo4jlib.neo4j_qry.qry_write("match (k:seq_pos) delete k");
        
        
        //set up variables to hold data from processing kits and their tiles
        //some of these are public static and defined for the entire class (see above)
        //  tile_len = number of nucleotides per tile
        //  tile_partitions = count of the number of tiles possible in each kit
        //  tile_seq = 2-dimensionl array holding the tile_partition number and its set of unique tile sequences
        //  tile_seq_ct = array holding the number of sequences identified for each tile
        
        //tessellation creates tiles
        //each partition is evaluated, kit by kit, to either
        //  identify a new tile sequence, in which case it is added to the tile_seq array and the tile_seq_ct incremented
        //  or an existing tile sequence is identified an its tilde_id is assigned to the kit's partition
        tile_seq = new String[tile_partitions][30000];
        tile_seq_ct= new int[tile_partitions];
        
        //iterate the kits to initialize the tile counts
        for (int i=0;i<tile_seq_ct.length;i++)
         {
             tile_seq_ct[i]=0;
         }
        
        //create Neo4j indices before data populates the database
        try{
            gen.neo4jlib.neo4j_qry.CreateIndex("tile", "n");
            gen.neo4jlib.neo4j_qry.CreateIndex("tile", "seq");
            gen.neo4jlib.neo4j_qry.CreateIndex("tile", "tile_id");
            gen.neo4jlib.neo4j_qry.CreateCompositeIndex("tile","tile_id, n" );
            gen.neo4jlib.neo4j_qry.CreateIndex("seq_kit", "name");
        }
        catch(Exception e){}
        
        //get a list of files from the directory which holds them
        List<File> fasta = new ArrayList<File>();
//        String fn = "tiles.csv";
//        File ftiles = new File(gen.neo4jlib.neo4j_info.Import_Dir + fn);
//         FileWriter fw = null;
         int slen = 0;


       int i = 0;
       //iterate kits to create an array of file names
       for (File fasta_file : (List<File>) FileUtils.listFiles(new File(dir), new String[]{"fasta", "FASTA"}, true)) 
          {
            fasta.add(fasta_file);
        }
       
       //initialize arrays to hold kit and kit tile data
       kit = new String[fasta.size()];
       int ckit[][] = new int[fasta.size()][tile_partitions];
       
       //iterate through each kit
       for (i=0; i<fasta.size(); i++)  
       {
//            try
//        {
//            fw = new FileWriter(ftiles);
//            fw.write("kit|ct|tile\n");
//        }    
//        catch(Exception e){System.out.println("Error # 101");}
           
           // read kit file and parse it into an array of the multiple lines in the fasta file
           String s[] = gen.neo4jlib.file_lib.readFileByLine(fasta.get(i).getPath()).split("\n");
           String seq = "";
           String tile[] = new String[tile_partitions];
           kit[i] = s[0].split(" ")[0].replace(">","").split(Pattern.quote("."))[0];
           int n =0;
           
           // concatenate the rows into a sinle string with the full sequence
           for (int j=1; j<s.length; j++)
           {
               seq = seq + s[j];
               }
           
           //partition the sequence using the substring function into the tiles
           //the partition will have a variable length because of difference in insertions and deletions
           //each partition creates a tile
           //each tile is submitted to the processing function: process_tile
           for (int j=0; j<tile_partitions; j++)
           {
               try
               {
                  if (seq.length() < (j+1)*tile_len) {  //shorter last partition
                   tile[j] = seq.substring(j * tile_len, seq.length());
                    n = process_tile(kit[i], j, tile[j]);   
                  }
                  else
                  {  //full length partition
                   tile[j] = seq.substring(j * tile_len, (j+1) * tile_len);
                   n = process_tile(kit[i], j, tile[j]);   
                  }
                ckit[i][j] = n;   
                   
               }
               catch(Exception e){ //triggered when last lines are void
                   //System.out.println("Error # 102\t" + i + "\t" + e.getMessage());
               }           
           }
           
//           try
//           {
//               fw.flush();
//           }
//           catch(Exception e){}
           
       }

//       try
//       {
//           fw.flush();
//           fw.close();
//       }
//       catch(Exception e){}
       //finished partitioning kits; the results are in the arrays which are next processed for imort into Neo4j
       
       
       //////////////////////////////////////////////////////////////
       //print csv files for uploading to Neo4
      //kits
       String fnkits = "kits.csv";
        FileWriter fwk = null;
                
       try
        {
            fwk = new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + fnkits);
            fwk.write("kit\n");
                }    
        catch(Exception e){
            System.out.println("error #105\t" + e.getMessage());
        }
       
       for (int k=0; k<kit.length; k++)
       {
           try
           {
               fwk.write(kit[k] + "\n");
           }
           catch(Exception e){}
       }
       
       try
       {
           fwk.flush();
           fwk.close();
       }
       catch(Exception e){}

               
       //tiles
       String fnpartitions = "tiles.csv";
        FileWriter fwp = null;
                
       try
        {
            fwp = new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + fnpartitions);
            fwp.write("partition|tile_id|tile\n");
                }    
        catch(Exception e){}
       
       for (i=0; i<tile_partitions; i++)
       {
           for (int j=0; j < tile_seq_ct[i]; j ++)
           {
               try
               {
                fwp.write(i + 1 + "|" + j + "|" + tile_seq[i][j] + "\n");
                fwp.flush();
               }
               catch(Exception e){}
           } //next item
       } // next partition
       try
       {
           fwp.flush();
           fwp.close();
       }
       catch(Exception e){}

       /////////////////////////////////////////
       // kt_tile relationship
       String fnrel = "kit_tiles.csv";
        FileWriter fwrel = null;
                
       try
        {
            fwrel = new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + fnrel);
            fwrel.write("kit,partition,tile_id\n");
                }    
        catch(Exception e){}
       for (int k=0; k<kit.length; k++)
            {
            for (i=0; i<tile_partitions; i++)
            {
                    try
                    {
                    if(i<tile_seq_ct[i])
                        {
                     int ct = i +1;
                     fwrel.write(kit[k] + "," + ct + "," + ckit[k][i] + "\n");
                        }
                        else{break;}
                    }
                    catch(Exception e){}
            } //next partition

            try
            {
                fwrel.flush();
            }
            catch(Exception e){System.out.println("error # 103\t" + k + "\t" + e.getMessage());}
            
            }  //next kit 
       try
       {
           fwrel.flush();
           fwrel.close();
       }
       catch(Exception e){}

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
// import to Neo4j using LOAD CSV
//create tiles; each is unique so the faster create can be used rather than merge.
gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///tiles.csv' as line FIELDTERMINATOR '|' create (t:tile{tile_id:toInteger(line.tile_id), n:toInteger(line.partition),seq:toString(line.tile)})");

//merge kits; merge to avoid duplicate kits or tiles
gen.neo4jlib.neo4j_qry.qry_write("LOAD CSV WITH HEADERS FROM 'file:///kits.csv' as line FIELDTERMINATOR '|' merge(k:seq_kit{name:toString(line.kit)})");

//create kit_tile relationships
//this take ~5 min; this is the heavy lift which, even with indices, takes a while.
gen.neo4jlib.neo4j_qry.APOCPeriodicIterateCSV("LOAD CSV WITH HEADERS FROM 'file:///kit_tiles.csv' as line FIELDTERMINATOR ','  return line ", "match(k:seq_kit{name:toString(line.kit)}) match(t:tile{n:toInteger(line.partition), tile_id:toInteger(line.tile_id)}) merge (k)-[r:kit_tile]->(t)",20000);

       return "";
    }
     
     /////////////////////////////////////////////////////////////////////////
     /////////////////////////////////////////////////////////////////////////
     //process individual kit sequences into tiles
     //multiple tiles per kit
     //uses an array of each tile (presently 1 to 170) with the patterns observed at each, 
     //   distinguished by their tial_id, the number of the sequence instance and the tile sequence itself
     //the tile_id for each kit partition is written to a file; more efficient than holding in memory
     public static int process_tile(String kit, int n, String tile)
     {
         Boolean fnd = false;
         int fnd_item=0;
         int i=0;
         for (i=0; i<tile_partitions-1; i++)
         {

             try
             {  //catches if null
                if(tile_seq[n][i].compareTo(tile)==0)
                {
                    fnd = true;
                    fnd_item=i;
                    break;
                }
            }
         catch(Exception e) 
         {
//             System.out.println(kit + "\terror #107  " + e.getMessage());
             break;
         }
             
         
         }
         
         String fndTile="";
//         int fndCt = 0;
         
         if (fnd.compareTo(false)==0)
         {
            //break at i when fnd=false is where in the array you add the seq
            tile_seq[n][tile_seq_ct[n]]=tile; 
            fnd_item = tile_seq_ct[n];
            fndTile=tile;
//            fndCt=n;
         }
         else  //seq already in array
         {
             fndTile= tile_seq[n][fnd_item];
//             fndCt = fnd_item;
            tile_seq[n][fnd_item]=fndTile; 
         }
//         try
//         {  
//        //write kit partition data to file
//        //kit name; the partition number and the tile_id of the fund or created tile sequence
//             //saves to tiles.csv file
//        fw.write(kit + "|" + n + "|" + tile_seq[n][tile_seq_ct[n]] + "|" + fndTile + "\n");
//        fw.flush();
//        }
//        catch(Exception e)
//        {
//        System.out.println(kit + "\terror #109 " + e.getMessage());
//        }
          if (fnd.compareTo(false)==0)
         {
            //break at i when fnd=false is where in the array you add the seq
            tile_seq_ct[n] = tile_seq_ct[n] + 1;
 
         }
           
         return fnd_item;
}


}

