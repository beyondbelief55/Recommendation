package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import db.MySQLConnection;
import entity.Item;
import external.GitHubClient;

/**
 * Servlet implementation class SearchItem
 */
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    // servlet response is automatically created for us by Java servlet
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	    //response.setContentType("application/json"); // set content type to JSON
        //PrintWriter writer = response.getWriter();
        
       // JSONArray array = new JSONArray();
       // array.put(new JSONObject().put("username", "abcd"));
       // array.put(new JSONObject().put("username", "1234"));
        //writer.print(array);
       // RpcHelper.writeJsonArray(response, array);
		
		HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		String userId = request.getParameter("user_id");
		GitHubClient client = new GitHubClient();
		List<Item> items = client.search(lat, lon, null); // search is the method the http request is calling 
        JSONArray array = new JSONArray();
        
        MySQLConnection connection = new MySQLConnection();
        Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        connection.close();
        for (Item item : items) {
            //array.put(item.toJSONObject()); // convert item to Json Array format becasue thats what the front end wants
            JSONObject obj = item.toJSONObject();
            obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
            array.put(obj);
        
        }
        //RpcHelper.writeJsonArray(response, array);

		//JSONArray array = client.Search(lat, lon, null);
		RpcHelper.writeJsonArray(response, array);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
