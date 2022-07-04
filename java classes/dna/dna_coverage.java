/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.dna;

//import gen.neo4jlib.neo4j_qry;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;
//import.java.awt.Desktop;
import java.util.Collections;

//https://familylocket.com/find-more-ancestors-with-autosomal-dna-by-increasing-coverage/?mc_cid=a28b796d43&mc_eid=530263b9cd
//https://www.legacytree.com/blog/introduction-autosomal-dna-coverage
public class dna_coverage {
    @UserFunction
    @Description("Computer DNA reconstructed for an ancestor.")
 
    public String dna_coverage_of_ancestor(
        @Name("anc_rn") 
            Long anc_rn
  )
   
         { 
             
        String s = get_coverage(anc_rn);
         return s;
            }
   
    public static void main(String args[]) {
        get_coverage(33454L);
    }
    
     public static String get_coverage(Long anc_rn) 
    {
        //get set of paths to descendants who did a DNA test
        String cq = "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) with path,q match (q)-[rm:Gedcom_DNA]-(m:DNA_Match) with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct[x in nodes(path)|x.RN])))) as rns return rns";
        String paths = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
        String ca[] = paths.split("\n")[0].replace("[","").replace("]","").split(",");
        int total_rn_in_paths=(ca.length );
       Double Tbl[][] = null;
       int nbr_kids = 0;
        int DescList[][] = null ;
        Double coverage[][] = null;  
        
        //instantiate variable to hold descendant data and calculations
       
        int persons[][] = new int[ca.length][6];
        //persons array will hold data from analytics
        //one row per descendant in the paths
        //2nd dimension with descendant's data
        //  0 = RN of descendant
        //  1 = number of descendant's children who did a DNA test
        //  2 = descendant's parent's RN
        //  3 = generations from the ancestor
        //  4 = index of row
        //  5 = tested
   
        coverage = new Double[ca.length][2];
        //coverage array dimensions
        //  0 = personal coverage which is 1 for testers and the fraction contributed by children
        //  1 = the fraction the person contributes to the parent. 0.5 when there is a single child and a fraction when siblings also contribute.

        String[] ordpath = new String[ca.length];
        
        //add computed results to arrays
        for (int i=0;i<ca.length; i++)
        {
            persons[i][0] = Integer.valueOf(ca[i].strip());  //record number
            persons[i][1] = 0;   //iterating below will add person themself
            persons[i][4] = i;  //index to facilitate lookups with ordering is filtered
        }
        
        //get descendants
        cq = "match path=(p:Person{RN:" +  anc_rn + "})<-[:father|mother*0..15]-(q:Person) with path,q match (q)-[rm:Gedcom_DNA]-(m:DNA_Match) with q, [x in nodes(path)|x.RN] as rns return rns";
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");

        //get max lenght
        int maxLen=0;
        for (int i=0; i<c.length; i++){
            String cb[] = c[i].split(",");
            if (cb.length>maxLen) { maxLen = cb.length;}
            }
        
            //get descendants who are DNA testers
          cq= "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) with path,q match (q)-[rm:Gedcom_DNA]-(m:DNA_Match) with q, [x in nodes(path)|x.RN] as rns unwind rns as x call { with x MATCH (p:Person)-[r:child]->(u:Union) where (u.U1=x or u.U2=x) and p.RN in " + paths + " RETURN count(*) as ct } with distinct x,ct match path2=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person{RN:x}) return x,ct as children_descendants_who_tested,length(path2) as gen,gen.graph.get_ordpath([y in nodes(path2) | y.RN]) as op order by  op";
        String[] kids = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
    
        for (int i=0;i<c.length; i++ ){
            String cs[]=c[i].replace("[","").replace("]","").split(",");
            for (int j=0;j<cs.length; j++)
            {
                for (int k=0;k< persons.length; k++) 
                {
                    if (Integer.valueOf(cs[j].strip()).equals(persons[k][0]))
                    {
                        //add child to kids if not there already and increment child count if added
                        try{
                            //add parent RN
                            persons[k][2] = Integer.valueOf(cs[j-1].strip());
                            persons[k][3] = cs.length;  //gen
                            if (persons[k][0] == Integer.valueOf(cs[cs.length-1].strip()))
                            {persons[k][5] = 1;}
                           }
                        catch(Exception e){}
                    }
                }
            }
          }
       
        for (int i=0;i<persons.length;i++)
        {
                for (int j=0; j<kids.length; j++)
            {
                //add kit count
                String[] css = kids[j].split(",");
                if (Integer.parseInt(css[0])==persons[i][0])
                {
                    persons[i][1] = Integer.parseInt(css[1]);
                    persons[i][3] = Integer.parseInt(css[2]);
                   ordpath[i] = css[3].replace("\"","");
                }
            }
        }
        String fn = gen.neo4jlib.neo4j_info.Import_Dir +  "coverage_" + anc_rn + ".html";
        File fnc = new File(fn);
        FileWriter fw = null;
        try{
            fw = new FileWriter(fnc);
            fw.write("<html>\n<body>\n");
        }
        catch(Exception e){}
        
        //compute cumulative coverage; 

        String delimiter = "\t";
        String s = "RN" + delimiter + "parent" + delimiter +  "gen" + delimiter + "coverage" + delimiter + "kids" + delimiter + "ordpath\n";
        for (int gen=maxLen; gen>-1; gen--)  //iterate generations up the family tree
        {
            for (int xrow=0; xrow<persons.length; xrow++ ){  //iterate rows
                if(persons[xrow][3]==gen){  // row has xrow in processing generaion i
                
               /////////////////////////////////////////////////////////////////
               /////////  TESTER ... 
               if (persons[xrow][5]==1){  // tester
                    coverage[persons[xrow][4]][0]  = 1.0;
                }
               
               /////////////////////////////////////////////////////////////////
               /////////  NON TESTER ... 
               else // non-tester; get coverage from children previously computed
               {   
 
                   int indv = persons[xrow][0];
                   int indv_row = persons[xrow][4];

                   int parent = persons[xrow][2];  //parent to find
                   nbr_kids = persons[xrow][1];  //kids who tested
                   DescList = new int[nbr_kids][2];   // 0=RN; 1=index in x
                   Double M = Math.pow(2,-nbr_kids);  //maximum theoretical coverage
                   Double P = Math.pow(2,nbr_kids) -1 ; //number of pieces of Venn diagram
                   int iP = (int) Math.pow(2,nbr_kids) -1 ; 
                   Tbl = new Double[iP][(nbr_kids) +3]; //column for each kid + M, W, R
                    Double sumR = 0.0;
                   int colM = nbr_kids; 
                   int kid_ct = 0;
                   for (int t=0; t<persons.length; t++)  //iterare entire list to find kids
                   {
                         if (persons[t][2]==indv) // parent value; k = kid's row
                           {
                             //get each kid's info
 
                             //if (persons[t][0]>0)
                             //{
                             DescList[kid_ct][0] = persons[t][0];
                           
                             DescList[kid_ct][1] = t; 
                             //}
                     
                          kid_ct = kid_ct + 1;
                       }//end kid   
                        
                        if (kid_ct>nbr_kids-1){break;}
                   } //end search for kids
                   
                   populateUniqueStrTbl(Tbl,nbr_kids);

                   
                   
            //put coverage based on kids into indv record
            //get_cov(Tbl,indv, parent, DescList, coverage);               
            /////////////////////////////////////////////////////////////////
            /////////  CALCULATE COVERAGE FOR PARENT USING KID'S DATA ... 
               
            Double[] kid_cov = new Double[nbr_kids];
            Double maxCov = 0.0;
            Double tmp = 0.0;
            for (int t=Tbl.length-1; t>=0; t--)
            {   //rows
                Tbl[t][colM]=M;
                  }
            
   
            {   //rows
    
           sumR = get_cov_rollup(Tbl, indv_row, DescList, coverage);
            }
                //print html of table
            
  
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            
            
            
            print_Tbl1(indv, gen, nbr_kids,DescList,Tbl, coverage, fw);
               
            print_Tbl2(indv, gen, persons, Tbl,nbr_kids, sumR, coverage, fw);
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
               
            }  //next x in iteration
                }
            }  //filer for generation
        } //next generation
        
          try{
          print_summary(persons,kids, coverage, ordpath, fw);
          fw.write("<br><br>Methods developed by Wesley Johnston<br>&copy; 2022 <a href='http://wai.md/gfg' target='new'>Graphs for Genealogists</a><br><a href='https://www.facebook.com/groups/gfgforum' target='new'>Facebook Forum</a>\n </body>\n</html>\n");
         fw.flush();
          fw.close();}
          catch(Exception e){}
             
          
       // Desktop.getDesktop().open(new File(fn));

        return "HTML Report in thr import directory.";
    }
  
     public static void print_summary(int[][] persons,String[] kids, Double[][] coverage,String[] ordpath, FileWriter fw)
     {
         String summary = "" ; // new String[persons.length][2];
         gen.gedcom.get_family_tree_data gp = new gen.gedcom.get_family_tree_data();
        try{
            fw.write("<h3>List of all testers.</h3>Testers whose parents also tested are shown, but the coverage to parents is null because it is irrelevant in this analysis.<br><br>");
            fw.write("<table>,<tr><th>gen</th><th>coverage</th><th>coverage to parent</th><th>person</th><th></th><th></th></tr>");
        } 
        catch(Exception e){}
         
        for (int k=0; k<kids.length; k++)
         {
            
            String[] ks = kids[k].split(",");
             
             for (int p=0;p<persons.length; p++)
             {
                 if(Integer.parseInt(ks[0]) == persons[p][0])
                 {
                     try{
                     fw.write("<tr>");
                     fw.write("<td>" + persons[p][3] + "</td><td>" + coverage[persons[p][4]][0] + "</td><td>" + coverage[persons[p][4]][1] + "</td><td>" + gen.genlib.handy_Functions.lpad("",persons[p][3],"...") + gp.person_from_rn(Long.valueOf(persons[p][0]),true).replace("⦋", "[").replace("⦌","]") + "</td>" +  "\n");
                     fw.write("</tr>");
                     
                     }
                     catch(Exception e){}
                     
                 }
             }
 
         }
            try{fw.write("</table>");} catch(Exception e){}
        
         
             System.out.println(summary);
         
         
     }
         
//            String pp = gp.person_from_rn(Long.valueOf(persons[i][0]),true).replace("⦋", "[").replace("⦌","]");
//            
//            summary[i][0] = pp;
//            summary[i][1] = ordpath[i]; 
//            }
//         
////         //https://stackoverflow.com/questions/71523721/sort-2d-string-array-with-respect-to-a-column-in-java
////        List<String[]> collect1 = Arrays.stream(summary).sorted(Comparator.comparing(a -> a[1])).collect(Collectors.toList());
////        String[][] sortedArray = new String[summary.length][2];
////        for (int i = 0; i < collect1.size(); i++) {
////        sortedArray[i] = collect1.get(i);
////        }        
////        //System.out.println(Arrays.deepToString(sortedArray));
////    
//           for (int i=0; i<persons.length; i++)
//         {
//             try{
//             fw.write(summary[i][0] + "<br>");
//             }
//             catch(Exception e){}
//             
////System.out.println(summary[i][0] + "\t" + summary[i][1]);
//        }
//     }
//     
     
      public static void sortbyColumn2DInt(int arr[][], int col)
    {
        //https://www.geeksforgeeks.org/sorting-2d-array-according-values-given-column-java/
        // Using built-in sort function Arrays.sort
        Arrays.sort(arr, new Comparator<int[]>() {
                        
          @Override              
          // Compare values according to columns
          public int compare(final int[] entry1, 
                             final int[] entry2) {
  
            // To sort in descending order revert 
            // the '>' Operator
            if (entry1[col] > entry2[col])
                return 1;
            else
                return -1;
          }
        });  // End of function call sort().
    }
     
    public static void print_Tbl1(int indv, int gen, int nbr_kids,int[][] DescList,Double[][] Tbl,Double[][] coverage, FileWriter fw)
    {
                   try{
              gen.gedcom.get_family_tree_data gp = new gen.gedcom.get_family_tree_data();
 
             if (nbr_kids>1)
             {
                String pp = gp.person_from_rn(Long.valueOf(indv),true).replace("⦋", "[").replace("⦌","]");
                fw.write("<h3>" + pp + "&nbsp;&nbsp;&nbsp;&nbsp;generation:&nbsp;&nbsp;" + gen + "</h3>\n");
                fw.write("<b>Table 1</b>\n");
                fw.write("<table>\n");
                fw.write("<tr>");
                //header
                for (int n=0;n<nbr_kids; n++)
                {
                    int qq = n + 1;
                     fw.write("<th>Child " + qq + " </th>");
                }
                       fw.write("</tr>\n");
                
                //data
                fw.write("<tr>\n");

                for (int n=0;n<nbr_kids; n++)
                {
                    String pk = gp.person_from_rn(Long.valueOf(DescList[n][0]),true).replace("⦋", "[").replace("⦌","]");
                    fw.write("<td>" + pk + "</td>\n");
                }    
                   fw.write("</tr>\n");  

                for (int n=0;n<nbr_kids; n++)
                {
                   fw.write("<td style=\"text-align:center\">" + coverage[DescList[n][1]][0]  + "</td>\n");
                }    
                   fw.write("</tr>\n");  

                fw.write("</table><hr style='height:1px'>\n");
             }
 }

           catch (Exception e){System.out.println("Trouble: " + e.getMessage());}
           

 
    }
    
     public static void print_Tbl2(int indv,int gen,int [][] persons, Double[][] Tbl,int ncol, Double sumR,Double[][] coverage, FileWriter fw)
     {
         int colWidth = 150;
          try{
             if (ncol==1)
             {
                gen.gedcom.get_family_tree_data gp = new gen.gedcom.get_family_tree_data();
                String pp = gp.person_from_rn(Long.valueOf(indv),true).replace("⦋", "[").replace("⦌","]");
                fw.write("<h3>" + pp + "&nbsp;&nbsp;&nbsp;generation:&nbsp;&nbsp;" + gen + "</h3>\n");
                
                for (int p=0; p<persons.length; p++)
                {
                    if (persons[p][2]==indv)
                    {
                        String per2 = gp.person_from_rn(Long.valueOf(persons[p][0]),true).replace("⦋", "[").replace("⦌","]");
                        fw.write("Child: " + per2 + "&nbsp;&nbsp;&nbsp;&nbsp;\n");
                        fw.write("coverage: " + coverage[persons[p][4]][0] + "<br>");
                        fw.write("coverage of " + pp + ": " + coverage[persons[p][4]][1]);
                    }
                }

                 fw.write("<br><hr style='height:5px;; color:#000; background-color:#000;'>\n\n\n");
             }
             
             else{
                fw.write("<b>Table 2</b><table>\n<tr><td>Kids " + ncol + " Options: " + Tbl.length + "</td></tr>\n");
                fw.write("<tr><th>\tPieces</th>");
          for (int i=0; i<ncol; i++)
                {
                int q = i +1;
                fw.write("<th>Child" + q +"</th>");
                }
          fw.write("<th style=\"width:" + colWidth + "px; text-align:right\"x>M</th><th style=\"width:" + colWidth + "px; text-align:right\">W</th><th style=\"width:" + colWidth + "px; text-align:right\">R</th></tr>");
                
            for (int i=0; i<Tbl.length; i++)
                {
            int q = i+1;
            fw.write("<tr> <td>" + q + ":\t<br></td>");
            for (int j=0; j<ncol;j++){
                    fw.write("<td>" + Tbl[i][j].intValue() + "</td>");
            }
            fw.write("<td style=\"width:" + colWidth + "px; text-align:right\">" + Tbl[i][ncol] + "</td>");
            fw.write("<td style=\"width:" + colWidth + "px; text-align:right\">" + Tbl[i][ncol+1] + "</td>");
            fw.write("<td style=\"width:" + colWidth + "px; text-align:right\">" + Tbl[i][ncol+2] + "</td>");
            fw.write("</tr>\n");
                }
            fw.write("<tr><td colspan='" + ncol+1 + "'></td><td>sum R: " + sumR  + "</td></tr>\n");
        fw.write("</table><br><hr style='height:5px;; color:#000; background-color:#000;'>\n\n\n");
                fw.flush();
            }
              
         }
         catch(Exception e){}
  
        
     }
     
     public static String zero_str(int n)
     {
         String s="";
         for (int i=0; i<n; i++)
         {
             s = s + "0";
         }
         return s;
     }
     
     
      public static void populateUniqueStrTbl(Double[][] Tbl,int nbr_kids)
     {
    Boolean b = false;
    int ic = 0;
    String se ="";
    String seu ="";
    String pos="";
    int rows_filled = 0;
    String[] seu_added= new String[Tbl.length];
         
    for (int u=0; u<1000; u++) ///generate indefinite nymber of string and identify unique ones to fill the table
    {
         seu="";
         //rows_filled=0;
         for (int v=0; v<nbr_kids;v++)  //create string of required size
         {
            //random generator of os and 1s
            if(Math.random() > 0.5) {pos = "1";}
            else {pos = "0";}
            seu = seu + pos;
         }  //end creating random string

         b= false;
            for (int a=0;a<rows_filled;a++)
            {
                if (seu_added[a].equals(seu)){
                b = true;
                }
            }

            if (b.equals(false) && !seu.replace("0", "").equals(""))
            {
            seu_added[rows_filled]=seu;
            rows_filled = fill_row(Tbl,nbr_kids,rows_filled,seu);
            }
               
            seu="";
         if (rows_filled>=Tbl.length) {return;}   //stop when all rows are filled
         

         
     } // iterate up to 1000 to find uniques
     }  //end of function

      
     public static int fill_row(Double[][] Tbl,int nbr_kids, int row_to_fill,String fill_with)
     {
         //parses the submitted string of 0's and 1's and distributes them to the Tbl columns
         try {
         String ss ="";
        for (int kkk=0;kkk<nbr_kids;kkk++)
            { 
                String s = fill_with.substring(kkk, kkk+1);
                Tbl[row_to_fill][kkk] = Double.valueOf(s);
                ss = ss + s + "\t";
            }
        return row_to_fill + 1;
         }
         catch(Exception e){return row_to_fill;}
         
         }

    
    ////////////////////////////////////////////////////////
public static Double get_cov_rollup(Double[][] Tbl, int indv_row, int[][] DescList, Double[][] coverage)
    {
        int childIndx =0;
        //if (DescList.length==1){return 1.0;}
        //Double c = 0.0;
        Double colCov[]= new Double[DescList.length];
        for (int i=0; i<DescList.length; i++)
        {
           //individual child's coverageplaced into array with same ordering as the Tbl and DescList 
           childIndx = DescList[i][1];
            
           colCov[i] =coverage[childIndx][0];
        }
        
        //////////////
        //finl calculation
       Double W_Max = 0.0;
       Double R = 0.0;
       Double sumR = 0.0;
       
        for (int trw=0; trw<Tbl.length; trw++)
        {
            W_Max=0.0;  //initialize for each row
            
              for (int i=0; i<DescList.length; i++)
            {
                  if (Tbl[trw][i]==1.0)  //use child Tbl columns that are 1; ignore 0;s
                {
                    Double M = Tbl[trw][DescList.length];
                    Double W_item = Tbl[trw][i] * colCov[i];
                    if(W_Max < W_item)
                    {
                        W_Max=W_item;
                    }
                }
 
            
                Tbl[trw][DescList.length + 1] = W_Max;  // W
                 R = W_Max * Tbl[trw][DescList.length];  //M * W
                 int ci = DescList[i][1];
                 coverage[ci][1]=R;  //conribution to parents coverage in [1]
                Tbl[trw][DescList.length + 2] = R;
                
               
            }
       }
        
        sumR = 0.0;
        for (int i=0; i<Tbl.length;i++){ 
            sumR = sumR + Tbl[i][DescList.length + 2]; 
            coverage[indv_row][0]=sumR;  //parent's own coverage
        }
        return sumR;
    }   
    
}
