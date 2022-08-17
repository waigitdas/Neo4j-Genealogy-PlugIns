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
    @Description("DNA Coverage. method: 1 = DNA results loaded; 2 = testers whether loaded or not")
 
    public String dna_coverage_of_ancestor(
        @Name("anc_rn") 
            Long anc_rn,
        @Name("methos") 
            Long method
  )
   
         { 
             
        String s = get_coverage(anc_rn, method);
         return s;
            }
   
    public static void main(String args[]) {
        get_coverage(41L, 1L);
    }
    
     public  static String get_coverage(Long anc_rn, Long method) 
    {
        gen.neo4jlib.neo4j_info.neo4j_var();
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        String cq="";
//get set of paths to descendants who did a DNA test
        if (method==1L){
        cq = "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y'] with path,q  with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct[x in nodes(path)|x.RN])))) as rns return rns";}
        if (method==2L){
        cq = "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y','A'] with path,q  with apoc.coll.dropDuplicateNeighbors(apoc.coll.sort(apoc.coll.flatten(collect (distinct[x in nodes(path)|x.RN])))) as rns return rns";}

        String paths = gen.neo4jlib.neo4j_qry.qry_to_csv(cq);
        String ca[] = paths.split("\n")[0].replace("[","").replace("]","").split(",");
        //int total_rn_in_paths=(ca.length );
       Double Tbl[][] = null;
       int nbr_kids = 0;
        int DescList[][] = null ;
        Double coverage[][] = null;  
        int tester_ct=0;
               //get descendants
        if (method==1L){
        cq = "match path=(p:Person{RN:" +  anc_rn + "})<-[:father|mother*0..15]-(q:Person{at_DNA_tester:'Y'})  with q, [x in nodes(path)|x.RN] as rns return rns";
        }
        
        if (method==2L){
        cq = "match path=(p:Person{RN:" +  anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['A', 'Y'] with q, [x in nodes(path)|x.RN] as rns return rns";}
                
                
        String c[] = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
        
        //get max lenght
        int maxLen=0;
        for (int i=0; i<c.length; i++){
            String cb[] = c[i].split(",");
            if (cb.length>maxLen) { maxLen = cb.length;}
            }
        
            //get descendants who are DNA testers
            if (method==1L){
          cq= "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y'] with q, [x in nodes(path)|x.RN] as rns unwind rns as x call { with x MATCH (p:Person)-[r:child]->(u:Union) where (u.U1=x or u.U2=x) and p.RN in  " + paths + "  RETURN count(*) as ct } with distinct x,ct match path2=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person{RN:x}) with x,ct as children_descendants_who_tested, nodes(path2) as p2, length(path2) as gen,gen.graph.get_ordpath([y in nodes(path2) | y.RN]) as op, q.at_DNA_tester as test_type return x,children_descendants_who_tested,p2[gen-1].RN as parent, gen, op order by op";}
            
            if (method==2L){
          cq= "match path=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person) where q.at_DNA_tester in ['Y', 'A'] with q, [x in nodes(path)|x.RN] as rns unwind rns as x call { with x MATCH (p:Person)-[r:child]->(u:Union) where (u.U1=x or u.U2=x) and p.RN in  " + paths + "  RETURN count(*) as ct } with distinct x,ct match path2=(p:Person{RN:" + anc_rn + "})<-[:father|mother*0..15]-(q:Person{RN:x}) with x,ct as children_descendants_who_tested, nodes(path2) as p2, length(path2) as gen,gen.graph.get_ordpath([y in nodes(path2) | y.RN]) as op, q.at_DNA_tester as test_type return x,children_descendants_who_tested,p2[gen-1].RN as parent, gen, op order by op";}
          
        String[] kids = gen.neo4jlib.neo4j_qry.qry_to_csv(cq).split("\n");
    
        //instantiate variable to hold descendant data and calculations
        int persons[][] = new int[kids.length][7];
        //persons array will hold data from analytics
        //one row per descendant in the paths
        //2nd dimension with descendant's data
        //  0 = RN of descendant
        //  1 = number of descendant's children who did a DNA test
        //  2 = descendant's parent's RN
        //  3 = generations from the ancestor
        //  4 = index of row
        //  5 = tested
        //  6 = number of paths; for pedigree collapse and endogamy
   
        coverage = new Double[kids.length][2];
        //coverage array dimensions
        //  0 = personal coverage which is 1 for testers and the fraction contributed by children
        //  1 = the fraction the person contributes to the parent. 0.5 when there is a single child and a fraction when siblings also contribute.

        String[] ordpath = new String[kids.length];
        
        //add computed results to arrays
        for (int i=0;i<kids.length; i++)
        {   try{
            String[] sKids = kids[i].split(",");
            persons[i][0] = Integer.valueOf(sKids[0].strip());  //record number
            persons[i][2] =  Integer.valueOf(sKids[2].strip());  ;   //parent
            //persons[i][2] = 0;   //iterating below will add person themself
            persons[i][4] = i;  //index to facilitate lookups with ordering is filtered
        }
        catch(Exception e){
            //error; do nothing in current version
        }
        }
        
 
        for (int i=0;i<c.length; i++ ){
            String cs[]=c[i].replace("[","").replace("]","").split(",");
            for (int j=0;j<cs.length; j++)
            {
                for (int k=0;k< persons.length; k++) 
                {
                    if (Integer.valueOf(cs[j].strip()).equals(persons[k][0]) && Integer.valueOf(persons[k][3])!=null)
                    {
                        //add child to kids if not there already and increment child count if added
                        try{
                            persons[k][3] = cs.length;  //gen
                            if (persons[k][0] == Integer.valueOf(cs[cs.length-1].strip()))
                            {
                                persons[k][5] = 1;
                            }
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
                //add kid count
                String[] css = kids[j].split(",");
                if (Integer.parseInt(css[0])==persons[i][0])
                {
                    persons[i][1] = Integer.parseInt(css[1]);
                    persons[i][3] = Integer.parseInt(css[3]);
                   ordpath[i] = css[4].replace("\"","");
                }
            }
        }
        String fn = gen.neo4jlib.neo4j_info.Import_Dir + gen.neo4jlib.neo4j_info.project +  "_coverage_" + anc_rn + "_" + method + "_" + gen.genlib.current_date_time.getDateTime() + ".html";
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
                    tester_ct = tester_ct + 1;
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
    
           sumR = get_cov_rollup(persons, Tbl, indv_row, DescList, coverage);
            }
  
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
          print_summary(persons,kids, coverage, ordpath, method,tester_ct, fw);
          fw.write("<br><br>Methods developed by Wesley Johnston<br>&copy; 2022 <a href='http://wai.md/gfg' target='new'>Graphs for Genealogists</a><br><a href='https://www.facebook.com/groups/gfgforum' target='new'>Facebook Forum</a>\n </body>\n</html>\n");
         fw.flush();
          fw.close();}
          catch(Exception e){}
             
          
       // Desktop.getDesktop().open(new File(fn));

        return "HTML Report in the import directory.";
    }
    
  
     public static void print_summary(int[][] persons,String[] kids, Double[][] coverage,String[] ordpath, Long method,int tester_ct, FileWriter fw)
     {
         String summary = "" ; // new String[persons.length][2];
         gen.gedcom.get_family_tree_data gp = new gen.gedcom.get_family_tree_data();
        try{
            fw.write("<h3>List of " + persons.length + " DNA testers in the paths to their ancestors of whom " + tester_ct + " have DNA results loaded to the database.</h3>");
            if (method==1L){
            fw.write("Method 1 used. This uses only testers whose DNA is loaded into the database<br>");}
            if (method==2L){
            fw.write("Method 2 used. This uses testers whose DNA is loaded into the database and those tagged as testers but not loaded into the database.<br>");}
            
            //fw.write("Testers whose parents also tested are shown, but the coverage to parents is null because it is irrelevant in this analysis.<br><br>");
            //fw.write("<table>,<tr><th>gen</th><th>coverage</th><th>coverage to parent</th><th>person</th><th></th><th></th></tr>");
            fw.write("<table>,<tr><th>gen</th><th>coverage</th><th>person</th><th></th><th></th></tr>");
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
                     fw.write("<td>" + persons[p][3] + "</td><td>" + coverage[persons[p][4]][0] + "</td><td>" + gen.genlib.handy_Functions.lpad("",persons[p][3],"...") + gp.person_from_rn(Long.valueOf(persons[p][0]),true).replace("⦋", "[").replace("⦌","]") + "</td>" +  "\n");
                     
                     //if method 1, update Avatar coverage
                     if (method==1)
                     {
                       String  cq = "match (a:Avatar{RN:" + Long.valueOf(persons[p][0]) + "}) set a.stat_coverage=" + coverage[persons[p][4]][0];
                         gen.neo4jlib.neo4j_qry.qry_write(cq);
                     }
//                     }
                     fw.write("</tr>");
                     
                     }
                     catch(Exception e){}
                     
                 }
             }
 
         }
            try{fw.write("</table>");} catch(Exception e){}
        
         
             System.out.println(summary);
         
         
     }
         

     
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
                        break;
                    }
                }

                 fw.write("<br><hr style='height:5px;; color:#000; background-color:#000;'>\n\n\n");
             }
             
             else{
                 int fl = 0;
                fw.write("<b>Table 2</b><table>\n<tr><td>Kids " + ncol + " Options: " + Tbl.length + "</td></tr>\n");
                fw.write("<tr><th>\tPieces</th>");
          for (int i=0; i<ncol; i++)
                {
                int q = i +1;
                fw.write("<th>Child" + q +"</th>");
                }
          fw.write("<th style=\"width:" + colWidth + "px; text-align:right\"x>M</th><th style=\"width:" + colWidth + "px; text-align:right\">W(p)</th><th style=\"width:" + colWidth + "px; text-align:right\">R(p)</th></tr>");
            if (Tbl.length>201){fl=200;}   
            else {fl = Tbl.length;}
            for (int i=0; i<fl; i++)
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
        fw.write("</table>");
        if (Tbl.length>201){fw.write("truncated output<br>");}   
        fw.write("<br><hr style='height:5px;; color:#000; background-color:#000;'>\n\n\n");
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
public static Double get_cov_rollup(int[][] persons, Double[][] Tbl, int indv_row, int[][] DescList, Double[][] coverage)
    {
        int childIndx =0;

        //if (Integer.valueOf(persons[indv_row][6])!=null)
        if (persons[indv_row][0]==18381)
        {
            int edg=0;
        }
        
        Double colCov[]= new Double[DescList.length];
        for (int i=0; i<DescList.length; i++)
        {
           //individual child's coverageplaced into array with same ordering as the Tbl and DescList 
           childIndx = DescList[i][1];
           if(coverage[childIndx][0]!=null)
           {
           colCov[i] =coverage[childIndx][0];
           }
           else
           {  //error ar indv_row
               gen.genlib.errors.error_rept(125, "Error 125: coverage not set properly for person with RN=" + persons[indv_row][0]);
           }
        }
        
        //////////////
        //finl calculation
        Double M = 0.0;
       Double W_Max = 0.0;
       Double W_item = 0.0;
       Double R = 0.0;
       Double sumR = 0.0;
       
        for (int trw=0; trw<Tbl.length; trw++)
        {
            W_Max=0.0;  //initialize for each row
            
              for (int i=0; i<DescList.length; i++)
            {
                {   try{
                  if (Tbl[trw][i]==1.0)  //use child Tbl columns that are 1; ignore 0;s
                    M = Tbl[trw][DescList.length];
                    W_item = Tbl[trw][i] * colCov[i];
                    if(W_Max < W_item)
                    {
                        W_Max=W_item;
                    }
                }
                catch(Exception e)
                {
                    //error; do nothing in current version
                }
                }
 
            
                Tbl[trw][DescList.length + 1] = W_Max;  // W
                 R = W_Max * Tbl[trw][DescList.length];  //M * W
                 int ci = DescList[i][1];
                 if (coverage[ci][1]==null){coverage[ci][1]=0.0;}
                 if (R!=null) {
                 coverage[ci][1]= coverage[ci][1] + R;  //conribution to parents coverage in [1]
                 }
                 else
                 {
                     //error; do nothing in current version
                 }
                Tbl[trw][DescList.length + 2] = R;
                
               
            }
       }
        
        sumR = 0.0;
        for (int i=0; i<Tbl.length;i++){ 
            sumR = sumR + Tbl[i][DescList.length + 2]; 
            if (sumR!=null){
            coverage[indv_row][0]=sumR;  //parent's own coverage
            }
            else{
                //error; do nothing in current version
            }
        }
        return sumR;
    }   
    
}
