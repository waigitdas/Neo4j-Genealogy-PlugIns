/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.genlib;

import java.util.Random;

/**
 *
 * @author david
 */
public class randon_strings {

    
  
    public static void random_names(int nbr){
         int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        for(int x=1;x<=nbr;x++) {
            StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + random.nextInt(rightLimit - leftLimit + 1);
            buffer.append((char) randomLimitedInt);
    }
    String generatedString = buffer.toString();

  
    System.out.println(generatedString);
    }
}
      public static void main(String[] args) {
        random_names(1000);
    }
    
}
