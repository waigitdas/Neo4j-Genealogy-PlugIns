package gen.genlib;

public class LeadInt {
    
    public static String leadInt  (String s) {
        return s.split("(?=\\D)")[0];
    }
//            String[] n = s.split(""); //array of strings
//            StringBuffer f = new StringBuffer(); // buffer to store numbers
//        for (String n1 : n) {
//            if (n1.matches("[0-9]+")) {
//                // validating numbers
//                f.append(n1); //appending
//            } else {
//                //parsing to int and returning value
//                return Integer.parseInt(f.toString());   
//            }
//        }
//            return 0;
//    }
}
