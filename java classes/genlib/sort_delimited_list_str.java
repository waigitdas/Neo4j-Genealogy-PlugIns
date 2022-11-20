/**
 * Copyright 2022-2023 
 * David A Stumpf, MD, PhD
 * Who Am I -- Graphs for Genealogists
 * Woodstock, IL 60098 USA
 */
package gen.genlib;

import gen.neo4jlib.neo4j_qry;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;


public class sort_delimited_list_str {
    @UserFunction
    @Description("Template used in creating new functions.")

    public String sort_str(
        @Name("str") 
            String str,
        @Name("delimiter")
            String delimiter
  )
   
         { 
             
        String s = sort_str_list(str,delimiter);
         return s;
            }

    
    
    public static void main(String args[]) {
        //String s = "Rene LETARTE ⦋2533⦌ (1626-1699); Robert DROUIN ⦋2335⦌ (1607-1685); Vincent BRUN ⦋2673⦌ (1611-1686); Jean THERIAULT ⦋2653⦌ (1601-1671); Jean TETARD ⦋2187⦌ (-); Mathieu LENEUF DUHERISSON ⦋1828⦌ (-1619); Pierre DUCHESNE LAPIERRE ⦋1838⦌ (1633-1697); Hilaire LIMOUSIN BEAUFORT ⦋1809⦌ (1633-2022); Jean ROY ⦋1499⦌ (1633-1676); Pierre CHATILLON dit GODIN ⦋1129⦌ (1630-1653); Pierre LEDUC ⦋1449⦌ (1670-1740); Jean BAUNE LAFRANCHISE ⦋1316⦌ (1633-1687); Gabriel DECELLES dit DUCLOS SAILLE ⦋1098⦌ (1626-1671); Etienne Br� ⦋1084⦌ (1694-1774); Helene DESPORTES ⦋2617⦌ (1620-1675); Louise GOULET ⦋2534⦌ (1628-2022); Anne CLOUTIER ⦋2336⦌ (1626-1649);Renee BREAU ⦋2674⦌ (1616-1678); Perrine RAU ⦋2654⦌ (1611-2022); Anne GODEFROY ⦋2188⦌ (-); Jeanne MARCHAND DELACELLONIERE DELAROQUE ⦋1829⦌ (2022-); Marie Catherine RIVET ⦋1839⦌ (1644-1723); Marie Antoinette LEFEBVRE ⦋1810⦌ (1653-2022); Francoise BOUET ⦋1500⦌ (1640-1722); Barbe POISSON ⦋1099⦌ (1633-1711); Marie Catherine FORTIN LAGRANDEUR ⦋1450⦌ (1684-1747); Jeanne ROUSSELIERE ⦋1130⦌ (1636-1686); Marie Agnes Jeanne ROY ⦋1496⦌ (1664-1717); Marie Madeleine BOURGERY BOURGIS ⦋1317⦌ (1652-1741); Marie Barbe DAZE ⦋1085⦌ (1703-1770)";
        //sort_str_list(s,";");
    }
    
     public static String sort_str_list(String s, String delimiter) 
    {
        String ss[]=s.split(delimiter);
        
        for(int i = 0; i<ss.length-1; i++)   
{  
            for (int j = i+1; j<ss.length; j++)   
            {  
            //compares each elements of the array to all the remaining elements  
            if(ss[i].compareTo(ss[j])>0)   
            {  
            //swapping array elements  
            String temp = ss[i].strip();  
            ss[i] = ss[j].strip();  
            ss[j] = temp;  
            }  
}  
}  
        
        String sout="";
        for (int i=0;i<ss.length;i++)
        {
            sout = sout + ss[i];
            if (i<ss.length){sout=sout + delimiter + " ";}
        }
        
        return sout;
    }
}
