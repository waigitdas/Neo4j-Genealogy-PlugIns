/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gen.load;
        
import static gen.load.web_file_to_import_folder.url_file_to_import_dir;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;

//import android.util.Log;

public class web_json_to_import_dir {

static InputStream is = null;
static JSONObject jObj = null;
static String json = "";

// constructor
//public JSONParser() {
//
//}

    public static void main(String args[]) {
        gen.neo4jlib.neo4j_info.neo4j_var();
        getJSONFromUrl("https://www.familytreedna.com/public/y-dna-haplotree/get","Y_haplotree.json");
    
    }


public static JSONObject getJSONFromUrl(String url, String FileNm) {

    // pull down FTDNA Y-hplotree reference data
    try {
        // defaultHttpClient
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClient httpClient = httpClientBuilder.build();

//        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);

        HttpResponse httpResponse = httpClient.execute(httpPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        is = httpEntity.getContent();

    } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
    } catch (ClientProtocolException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }

    //write json file to the import directory
    try {
        FileWriter fw= new FileWriter(gen.neo4jlib.neo4j_info.Import_Dir + FileNm);

        BufferedReader reader = new BufferedReader(new InputStreamReader(
                is), 8);
        //StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            //sb.append(line + "\n");
            fw.write(line);
            //System.out.println(line);
        }
        is.close();
        //json = sb.toString();
        fw.flush();
        fw.close()
                ;
    } catch (Exception e) {
        //Log.e("Buffer Error", "Error converting result " + e.toString());
    }

    // try parse the string to a JSON object
//    try {
//        jObj = new JSONObject(json);
//    } catch (JSONException e) {
//        //Log.e("JSON Parser", "Error parsing data " + e.toString());
//        System.out.println("error on parse data in jsonparser.java");
//    }

    // return JSON String
    return jObj;

}
}
   
