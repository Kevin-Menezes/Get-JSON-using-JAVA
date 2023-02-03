
package com.rapi.cloudfinchgetjson;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MainClass {
    
    public static void main(String[] args){
        
        try{
            
            URL url = new URL("https://cloudfinch-public.s3.ap-south-1.amazonaws.com/countries.json"); // Fetches the api
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Typecasts the API to Http
            conn.setRequestMethod("GET");
            conn.connect();
            
            int responsecode = conn.getResponseCode(); // Check the response code
            
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } 
            else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

               //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                   inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();
                
                JSONParser parse = new JSONParser();
                
                JSONArray data = (JSONArray) parse.parse(inline); // Converts string to json
                
                Map<String, List<JSONObject>> ContinentWithCountries = new HashMap<>(); // Mapping each continent with respective countries

                // Iterating through the JSONArray
                for(int i=0; i<data.size();i++){
                    
                    JSONObject country = (JSONObject) data.get(i);
                    String continent = (String) country.get("region");       
                    
                    // Excluding the empty continent
                    if(!continent.isEmpty()){
                        
                        if (!ContinentWithCountries.containsKey(continent)) {
                        ContinentWithCountries.put(continent, new ArrayList<>()); // Getting dynamic continents 
                      }
                      ContinentWithCountries.get(continent).add(country); // Adding country details to the respective continent
                    }
 
//                    System.out.println("Name : "+country.get("name"));
//                    System.out.println("Region : "+country.get("region"));
//                    System.out.println("Area : "+country.get("area"));
//                    System.out.println("==========================");
//                    System.out.println(" ");
                }
          
                // Printing out the distinct continents with their country details
//                for (Map.Entry<String,List<JSONObject>> entry : ContinentWithCountries.entrySet()) 
//                    System.out.println("Key = " + entry.getKey() +", Value = " + entry.getValue());
//                System.out.println();
                
                // Iterating through each continent in the HashMap.....Sorting the top 5 in decending order
                for (Map.Entry<String, List<JSONObject>> entry : ContinentWithCountries.entrySet()) {      
                    List<JSONObject> TopFiveCountries = entry.getValue().stream()
                      .filter(c -> c.get("area")!=null && !c.get("area").getClass().getSimpleName().equals("Double")) // Filtering out null and Double values
                      .sorted((o1,o2) -> {
                          Long area1 = (Long) o1.get("area");
                          Long area2 = (Long) o2.get("area");
                          return area2.compareTo(area1);
                      })
                      .limit(5)
                      .collect(Collectors.toList());
                
                    System.out.println("Continent - " + entry.getKey());
                    for (int i = 0; i < TopFiveCountries.size(); i++) {
                        System.out.println((i + 1) + ". " + TopFiveCountries.get(i).get("name") + " , Area = " + TopFiveCountries.get(i).get("area"));
                      }
                    System.out.println();
                }
            }  
        }
        catch(Exception e){
            System.out.println("Error in MainClass - "+e);
        }
    }
    
}
