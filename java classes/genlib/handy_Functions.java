package gen.genlib;

public class handy_Functions {

    public static void main(String args[]) {
       String s = "";
       s= s +  FixAsString(123) + "\n";
        s= s +  FixAsString("XXXX") + "\n";
        s= s +  FixAsString(3.14) + "\n";
        System.out.println(s);
    }

    public static String FixAsString(Object v) {
//     String s = "";
//     if (v instanceof Integer) {
//        return String.valueOf(v);
//    } else if(v instanceof String) {
//        return String.valueOf(v);
//    } else if(v instanceof Float) {
//        return String.valueOf(v);
//    } else if (v instanceof Double) {
//        return String.valueOf(v);
//    }      
    
             
    return String.valueOf(v);
    }
    
    public static String fix_str(String s){
        String ss = s.replace("\"","").replace("[","").replace("]","").replace(":","");
        return ss;
    }
    
    public static String[] remove_array_element(String[] arr, int index){
        String[] newArr = new String[arr.length-1];
       int j = 0;
       for(int i = 0; i<arr.length; i++){
       if(i != index){
       newArr[j] = arr[i];
       j++;
       }
       }
       return newArr;
}   
        
    
}

