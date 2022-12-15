/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.endogamy;

import gen.neo4jlib.neo4j_qry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class subpath_sort {
    @UserFunction
    @Description("Sorts subpath to order in rot/original full path.")

    public List<Long>sort_subpath(
        @Name("root_path1") 
            List<Long> root_path1,
        @Name("root_path2") 
            List<Long> root_path2,
        @Name("subpath") 
            List<Long> subpath
  )
   
         { 
             
        List<Long> ll = get_sorted_path(root_path1,root_path2,subpath);
         return ll;
            }

    
    
    public static void main(String args[]) {
//        List<Long> lst1 = new ArrayList<Long>(Arrays.asList(23L,25L,1329L,1332L,2893L,2896L,3052L,3055L,3067L,2654L));
//        List<Long> lst2 = new ArrayList<Long>(Arrays.asList(2893L,3055L,25L,1329L,1332L,23L,2896L,2654L));
//        List<Long> lst3 = new ArrayList<Long>(Arrays.asList(23L,25L,1329L,1332L,2893L,2654L));
        
        List<Long> lst1 = new ArrayList<Long>(Arrays.asList(4L, 12L, 19L, 36L, 1050L, 1049L, 1798L, 1803L, 1869L, 1873L, 1877L, 2653L));
        List<Long> lst2 = new ArrayList<Long>(Arrays.asList(4L, 13L, 23L, 25L, 1329L, 1332L, 2893L, 19L, 36L, 2896L, 3052L, 3055L, 3067L, 2653L));
        List<Long> lst3 = new ArrayList<Long>(Arrays.asList(19L,36L));  //2653L));
        
        
        List<Long> ll = get_sorted_path(lst1,lst2,lst3);
        System.out.println(ll);
    }
    
     public static List<Long> get_sorted_path(List<Long> root_path1, List<Long> root_path2, List<Long> subpath) 
    {
        //APOC's intersection function does NOT keep the sort order from the sources.
        //This function uses both of the source paths to sort the intersection in the same order.
        //rejects intersect if either skip over items in paths, which means the subpath is not a full sequence path
        List<Long> sorted_list = new ArrayList<>();
        int ct =0;
        int i=0;
        int last_i=0;
        //check root_path1 and subpath for aligned sequences order without skips
        for (i=0; i<root_path1.size(); i++) 
        { // iterate through root, which is sort order to be replicated in subpath
        for (int j=0; j<subpath.size(); j++)
        { // is rn in subpath; if so, add to sorted list
            if (root_path1.get(i).equals(subpath.get(j)))
            { // items are matched
                if (ct==0)
                        { //start found sequence
                            ct = i + 1;
                            sorted_list.add((long)root_path1.get(i));
                            last_i =i;
                            break;
                        }
                else //check if found sequence is same as root sequence
                {

                     if (Integer.compare(ct,i)<0 ) { //ct<1
                         //skipped over elements in root sequence; therefore, not a true subpath
                         List<Long> r = new ArrayList<>();
                         return r;  
                        //these will be ignored and not used to create intersect node. 
                        //In development, this eliminate ~20% of erroreous intercept node 
                        //and 11% of path_intercept relationships that would otherwise be created
                     }
                     else{
                         sorted_list.add((long)root_path1.get(i));
                         ct = ct +1;
                         last_i = i;
                         break;
                     }

                }
             }
        }
                    
       
       }

        //reininitialize and check 2nd root path
        sorted_list = new ArrayList<>();
        ct =0;
        i=0;
        //check root_path1 and subpath for aligned sequences order without skips
        for (i=0; i<root_path2.size(); i++) 
        { // iterate through root, which is sort order to be replicated in subpath
        for (int j=0; j<subpath.size(); j++)
        { // is rn in subpath; if so, add to sorted list
            if (root_path2.get(i).equals(subpath.get(j)))
            { // items are matched
                if (ct==0)
                        { //start found sequence
                            ct = i + 1;
                            sorted_list.add((long)root_path2.get(i));
                            break;
                        }
                else //check if found sequence is same as root sequence
                {

                     if (Integer.compare(ct,i)<0) { //ct<1
                         //skipped over elements in root sequence; therefore, not a true subpath
                         List<Long> r = new ArrayList<>();
                         return r;  
                        //these will be ignored and not used to create intersect node. 
                        //In development, this eliminate ~20% of erroreous intercept node 
                        //and 11% of path_intercept relationships that would otherwise be created
                     }
                     else{
                         sorted_list.add((long)root_path2.get(i));
                         ct = ct +1;
                         break;
                     }

                }
             }
        }
                    
       
       }


        
//        //reininitialize and check 2nd root path
//        sorted_list = new ArrayList<>();
//        ct =0;
//        //check root_path2 and subpath for aligned sequences order without skips
//        for (int i=0; i<root_path2.size(); i++) 
//        { // iterate through root, which is sort order to be replicated in subpath
//        for (int j=0; j<subpath.size(); j++)
//        { // is rn in subpath; if so, add to sorted list
//            if (root_path2.get(i)==subpath.get(j))
//            { // items are matched
//                if (ct == 0)
//                        { //start found sequence
//                            ct = i;
//                            sorted_list.add((long)root_path2.get(i));
//                        }
//                else //check if found sequence is same as root sequence
//                {
//                     if (ct<i) {
//                         //skipped over elements in root sequence; therefore, not a true subpath
//                         List<Long> r = new ArrayList<>();
//                        return r;  
//                        //these will be ignored and not used to create intersect node. 
//                        //In development, this eliminate ~20% of erroreous intercept node 
//                        //and 11% of path_intercept relationships that would otherwise be created
//                     }
//                     else{sorted_list.add((long)root_path2.get(i));}
// 
//                }
//            }
//                ct = ct + 1;            }
//        }

        return sorted_list;
    }
}
