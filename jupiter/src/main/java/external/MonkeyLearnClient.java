package external;

import java.util.ArrayList;
import java.util.List;

//import org.json.JSONArray;
//import org.json.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.monkeylearn.ExtraParam;
import com.monkeylearn.MonkeyLearn;
import com.monkeylearn.MonkeyLearnException;
import com.monkeylearn.MonkeyLearnResponse;

public class MonkeyLearnClient {
	private static final  String API_KEY = " 5a4458259eae6732000d794ca501a28f2616ca41"; // this is needed for Monkey API 
	// this is like the social security key for you to use the API 
	// need everytime you call the API 

	
	public static void main(String[] args)  {
	        
	        String[] textList = {
	                "Elon Musk has shared a photo of the spacesuit designed by SpaceX. This is the second image shared of the new design and the first to feature the spacesuit’s full-body look.", };
	        List<List<String>> words = extractKeywords(textList);
	        for (List<String> ws : words) {
	            for (String w : ws) {
	                System.out.println(w);
	            }
	            System.out.println();
	        }
	    }

	public static List<List<String>> extractKeywords(String [] text) { // we create our new method to extract keyword
		if (text == null || text.length == 0) {
			return new ArrayList<>();
		}
		MonkeyLearn ml = new MonkeyLearn(API_KEY); // create our MOnekyLearn API 
		ExtraParam[] extraParams = { new ExtraParam ("max_keywords", "3")}; // tell Monkey learn to only return 
		// top three API. 
		MonkeyLearnResponse response;
        try {
            response = ml.extractors.extract("ex_YCya9nrn", text, extraParams);//change to your model id
            // use the ex_CYcya extract keyword model ID so that it can return the top 3 keywords
            
            JSONArray resultArray = response.arrayResult;
            //System.out.print(resultArray);
            return getKeywords(resultArray);
        } catch (MonkeyLearnException e) {// it’s likely to have an exception
            e.printStackTrace();
        }
        return new ArrayList<>();
	}
	/*mlResult Array is list of array in following form  where each is a Json object
	 * "front end develop" count : 1 relevance etc. 
	 * black lives matter count : 1 relevance etc.
	 * hello world count : 2 relevance etc.
	 * we then parse each one into an individual keyword
	 * <"front", "end", "develop">
	 * <"black", "lives", "matter">
	 * <"hello", "world">
	 */
	
	private static List<List<String>> getKeywords(JSONArray mlResultArray) {
        List<List<String>> topKeywords = new ArrayList<>();
        // Iterate the Json result array and convert it to our format.
        for (int i = 0; i <  mlResultArray.size(); ++i) {
            List<String> keywords = new ArrayList<>();
            JSONArray keywordsArray = (JSONArray) mlResultArray.get(i); // get first element result "front end developer" 
            for (int j = 0; j <  keywordsArray.size(); ++j) {
                JSONObject keywordObject = (JSONObject) keywordsArray.get(j);
                // We just need the keyword, excluding other fields.
                String keyword = (String) keywordObject.get("keyword");
                keywords.add(keyword);

            }
            topKeywords.add(keywords);
        }
        return topKeywords;
    }

}
