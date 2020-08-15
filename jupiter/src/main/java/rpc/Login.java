package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import db.MySQLConnection;

/**
 * Servlet implementation class Login
 */
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    // validate session when you login 
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		HttpSession session = request.getSession(false); // check if a seesion exists
        JSONObject obj = new JSONObject();
        if (session != null) {
            MySQLConnection connection = new MySQLConnection();
            String userId = session.getAttribute("user_id").toString();
            obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
            connection.close();
        } else {
            obj.put("status", "Invalid Session");
            response.setStatus(403);
        }
        RpcHelper.writeJsonObject(response, obj);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 JSONObject input = new JSONObject(IOUtils.toString(request.getReader()));
	        String userId = input.getString("user_id");
	        String password = input.getString("password");

	        MySQLConnection connection = new MySQLConnection();
	        JSONObject obj = new JSONObject();
	        if (connection.verifyLogin(userId, password)) {
	            HttpSession session = request.getSession();
	            session.setAttribute("user_id", userId); // say once you are logged in, you can 
	            // look at other things are require to be authenticated becasue the object is binded to the session already. 
	            session.setMaxInactiveInterval(600); // time out time 
	            obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
	        } else {
	            obj.put("status", "User Doesn't Exist");
	            response.setStatus(401);
	        }
	        connection.close();
	        RpcHelper.writeJsonObject(response, obj);

	}

}
