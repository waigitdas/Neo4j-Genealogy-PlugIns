/*
 *https://www.developer.com/design/understanding-java-tree-apis/
 */
package gen.graph;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author david
 */
public class testTreeSet {
     public static void main(String[] args) {
         Integer[] nums={2,4,1,6,3,7,9,5};
      SortedSet<Integer> tree=new TreeSet<>(Arrays.asList(nums));

      // Print first and last element
      System.out.println(tree.first());
      System.out.println(tree.last());

      printAll(tree);
      // False. Set does not allow duplicates,
      // so this will not be added.
      System.out.println(tree.add(1));

      // But, this will be added because 11 is not a duplicate
      System.out.println(tree.add(11));
      printAll(tree);

      printAll(tree.headSet(7));

   }

   public static void printAll(SortedSet<Integer> tree){
      for(int s: tree){
         System.out.println(s);
      }
      System.out.println();
   }
}
