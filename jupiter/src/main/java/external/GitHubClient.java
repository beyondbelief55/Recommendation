package external;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.monkeylearn.MonkeyLearnException;

import entity.Item;
import entity.Item.ItemBuilder;



public class GitHubClient {
	private static final String URL_TEMPLATE = "https://jobs.github.com/positions.json?description=%s&lat=%s&long=%s";
    private static final String DEFAULT_KEYWORD = "developer";
    // develop the search method to get data from github
    public List<Item> search (double lat, double lon, String keyword) {
    	if(keyword == null) {
    		keyword = DEFAULT_KEYWORD;
    	}
    	try {
			keyword = URLEncoder.encode(keyword, "UTF-8"); // whne you search rick sun to rick+sun in a URL 
			//basically change a space to + rick sun becomes rick + sun 
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	String url = String.format(URL_TEMPLATE, keyword, lat, lon);
    	CloseableHttpClient httpclient = HttpClients.createDefault(); // we are the client getting Github data
    	HttpGet httpget = new HttpGet(url);
    	 // help you handle faults in response 
    	ResponseHandler<List<Item>> responseHandler = new ResponseHandler<List<Item>>() {

    	        @Override
    	        public List<Item> handleResponse(
    	                final HttpResponse response) throws ClientProtocolException, IOException {

    	            if (response.getStatusLine().getStatusCode() != 200) {
    	            	return new ArrayList();
    	            }
    	            HttpEntity entity = response.getEntity(); // get entire body of the Job 
    	            if (entity == null) {
    	            	return new ArrayList();
    	            }
    	            String responseBody = EntityUtils.toString(entity); // must convert to string before converting Json array
    	           // JSONArray array = new JSONArray(responseBody);
    	            //return array;
    	            JSONArray array = new JSONArray(responseBody);
                    return getItemList(array);

    	            
    	        }

    	    };
    	    try {
				return httpclient.execute(httpget, responseHandler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    	return new ArrayList();
    }
    // get each single description for each job returned and let Monkey Learn extract each single description
    
    private List<Item> getItemList (JSONArray array){
    	List<Item> itemList = new ArrayList<>();
    	List<String> descriptionList = new ArrayList<>();
    	for (int i = 0; i < array.length(); i++) {
            // We need to extract keywords from description since GitHub API
            // doesn't return keywords. i.e there is no keyword key
            String description = getStringFieldOrEmpty(array.getJSONObject(i), "description");
            if (description.equals("") || description.equals("\n")) {
                descriptionList.add(getStringFieldOrEmpty(array.getJSONObject(i), "title"));
                // it is empty, we will just insert the title in there 
            } else {
                descriptionList.add(description);
            }    
        }

        // We need to get keywords from multiple text in one request since
        // MonkeyLearnAPI has limitations on request per minute.
        List<List<String>> keywords = MonkeyLearnClient
                .extractKeywords(descriptionList.toArray(new String[descriptionList.size()]));

    	for (int i = 0; i < array.length(); i++) {
    		JSONObject object = array.getJSONObject(i);
    		// object parse for field and insert into corresponding fields in  item
    		// item add to  itlist
    		ItemBuilder builder = new ItemBuilder(); // both are fine 
    		//Item.ItemBuilder builder = new Item.ItemBuilder();
           
    		builder.setItemId(getStringFieldOrEmpty(object, "id"));
            builder.setName(getStringFieldOrEmpty(object, "title"));
            builder.setAddress(getStringFieldOrEmpty(object, "location"));
            builder.setUrl(getStringFieldOrEmpty(object, "url"));
            builder.setImageUrl(getStringFieldOrEmpty(object, "company_logo"));
            builder.setKeywords(new HashSet<String>(keywords.get(i)));
    		Item item = builder.build();
    		itemList.add(item);
    	}
    	
    	return itemList;
    }
    private String getStringFieldOrEmpty(JSONObject obj, String field) { // check if key is null 
        return obj.isNull(field) ? "" : obj.getString(field);
    }


}
