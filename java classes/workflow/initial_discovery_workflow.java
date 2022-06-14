/**
 * Copyright 2022 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.workflow;

import gen.neo4jlib.neo4j_qry;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class initial_discovery_workflow {
    @UserFunction
    @Description("Produce several inital reports to get you started")

    public String initial_discovery(
//        @Name("rn1") 
//            Long rn1,
//        @Name("rn2") 
//            Long rn2
  )
   
         { 
             
        String s = run_workflow();
         return s;
            }

    
    
    public static void main(String args[]) {
        // TODO code application logic here
    }
    
     public String run_workflow() 
    {
        gen.neo4jlib.neo4j_info.neo4j_var_reload();
        
        String msg="";
        String nextItem = "\n\n----------------------------------\n\n";
        ///////////////////////////////////////////////////////////////////////////
        //           know your data
        ///////////////////////////////////////////////////////////////////////////
        try{
            gen.quality.Data_Summary ds = new gen.quality.Data_Summary();
            ds.understand_your_data();
            msg = msg + "Know your data completed successfully."  + nextItem;
        }
        catch (Exception e) {
                    msg = msg + "Know your data failed to complete with the following error message\n" + e.getMessage() + nextItem;
        }
        
        ///////////////////////////////////////////////////////////////////////////
        //                   icw matches
        ///////////////////////////////////////////////////////////////////////////
        try{
            gen.discovery.icw icw = new gen.discovery.icw();
            icw.in_common_with_matches();
            msg = msg + "In common with match report completed successfully"  + nextItem;
        }
        catch (Exception e) {
            msg = msg + "In common with match report failed with the following error message\n" + e.getMessage() + nextItem;
        }
        
        ///////////////////////////////////////////////////////////////////////////
        //            match clusters without mrca
        ///////////////////////////////////////////////////////////////////////////
        try{
            gen.discovery.matches_from_clustered_matches_without_mrca match_clusters = new gen.discovery.matches_from_clustered_matches_without_mrca();
            match_clusters.get_matches(3L, 7L, 250L);
            msg = msg + "The match cluster report completed successfully. You can adjust the paramenters to refine your report." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "The match cluster report failed. This may be the result of unsuitable default parameters; try adjusting them. and running again: return gen.discovery.match_clusters_without_mrca(3,7,250)  \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
        
        ///////////////////////////////////////////////////////////////////////////
        //           Louvain communities
        ///////////////////////////////////////////////////////////////////////////
               try{
            gen.algo.communities_icw algo = new gen.algo.communities_icw();
            algo.community_detection_icw(1L,25L,150L,true);
            msg = msg + "The Louvain community detection algorithm report completed successfully. Generally the communities will contain individuals in a branch of your family tree. You can adjust the parameters to optimize the results." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "The Louvain community detection algorithm report failed. This may be the result of unsuitable default parameters; try adjusting them. and running again: return gen.algo.community_detection_icw(1,25,150,true)  \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
        ///////////////////////////////////////////////////////////////////////////
        //           modularity optimization communities
        ///////////////////////////////////////////////////////////////////////////
                 try{
            gen.algo.communities_icw algo2 = new gen.algo.communities_icw();
            algo2.community_detection_icw(2L,25L,150L,true);
            msg = msg + "The Modularity community detection algorithm report completed successfully. Generally the communities will contain individuals in a branch of your family tree. You can adjust the parameters to optimize the results." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "The Modularity community detection algorithm report failed. This may be the result of unsuitable default parameters; try adjusting them. and running again: return gen.algo.community_detection_icw(2,25,150,true)  \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
   
        ///////////////////////////////////////////////////////////////////////////
        //           label propagation communities
        ///////////////////////////////////////////////////////////////////////////
                                 try{
            gen.algo.communities_icw algo2 = new gen.algo.communities_icw();
            algo2.community_detection_icw(3L,25L,150L,true);
            msg = msg + "The Label Propagation community detection algorithm report completed successfully. Generally the communities will contain individuals in a branch of your family tree. You can adjust the parameters to optimize the results." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "The Label Propagation community detection algorithm report failed. This may be the result of unsuitable default parameters; try adjusting them. and running again: return gen.algo.community_detection_icw(3,25,150,true)  \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
   
        ///////////////////////////////////////////////////////////////////////////
        //           double cousins
        ///////////////////////////////////////////////////////////////////////////
                 try{
            gen.rel.double_cousin dc = new gen.rel.double_cousin();
            dc.double_cousin_reports();
            msg = msg + "The double cousin report completed successfully. this is an example of how graph methods help you discovery new insights." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "The double cousin report failed. You may not have any double cousins in your database. \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
       
        ///////////////////////////////////////////////////////////////////////////
        //           surname report
        ///////////////////////////////////////////////////////////////////////////

        try{
            gen.discovery.surname_variants sv = new gen.discovery.surname_variants();
            sv.get_matches(gen.neo4jlib.neo4j_info.project);
            
            msg = msg + "Matches with a surname of " + gen.neo4jlib.neo4j_info.project + " which is the project name." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "Error\n\nMatches with a surname of " + gen.neo4jlib.neo4j_info.project + " which is the project name failed. \nThe server gave the following error message\n" + e.getMessage() + nextItem;
        }
       
        ///////////////////////////////////////////////////////////////////////////
        //           ancestor descendants with DNA results
        ///////////////////////////////////////////////////////////////////////////
        try{
        gen.rel.anc_rn anc = new gen.rel.anc_rn();
        Long rn =anc.get_ancestor_rn();
    gen.dna.ancestor_dna_matches adm = new gen.dna.ancestor_dna_matches();
        adm.ancestor_descendants_with_dna_test(rn);
        msg = msg + "Designated ancestor descendant segments and triangulation groups ." + nextItem;
        }
        catch (Exception e) {
            msg = msg + "Error\nDo you have an ancestor designated? Designated ancestor descendat segments and triangulation groups"  + nextItem;
        }
                       
                 
//        try{
//            gen.discovery.icw icw = new gen.discovery.icw();
//            icw.in_common_with_matches();
//            msg = msg + "In common with match report completed successfully";
//        }
//        catch (Exception e) {
//            msg = msg + "In common with match report failed with the following error message\n" + e.getMessage();
//        }
//        

        ///////////////////////////////////////////////////////////////////////////
        //           summary of what just happened
        ///////////////////////////////////////////////////////////////////////////
        String fn = "Initial discovery report.txt";
        
        gen.neo4jlib.file_lib.writeFile(msg, fn);
        try {
            Desktop.getDesktop().open(new File(fn));
        } catch (Exception ex) {
            //Logger.getLogger(initial_discovery_workflow.class.getName()).log(Level.SEVERE, null, ex);
        }

        return msg;
    }
}
