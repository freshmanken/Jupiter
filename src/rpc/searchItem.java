package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class searchItem
 */
@WebServlet("/search")
public class searchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public searchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
		/** case 1 - get parameter and add to response body */
//		response.getWriter().append("Hello ").append(request.getParameter("username"));
		
		/** case 2 - add html to response body */
		/** what if NOT set of ContentType ? */
		/** nowadays browser is intelligent enough to handle the contentType automatically */
//		response.setContentType("text/html");
//		PrintWriter out = response.getWriter();
//		out.println("<html><body>");
//		out.println("<h1>This is a HTML page</h1>");
//		out.println("</body></html>");
//		
//		out.close();
		
		/** remember to download the json.jar library and add to WEB-INF/lib, otherwise json will not be recognized by IDE
		/** case 3 - add JSON object to response body */
		/** obj.put() always need try catch Exception, normally use JSONException e here */
		/** JSONObject obj.put() or obj.append()? */
		/** does obj.append need try catch as well ? */
		
//		response.setContentType("application/JSON"); we can omit this but show here for clarification
//		PrintWriter out = response.getWriter();
//		
//		String username = "";
//		if (request.getParameter("username") != null) {
//			username = request.getParameter("username");
//		}
//		
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("username", username);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		out.print(obj);
//		out.close();
		
		/** case 4 - add a list of JSON object to response body */
		
//		JSONArray array = new JSONArray();
//		PrintWriter out = response.getWriter();
//		
//		try {
//		array.put(new JSONObject().put("username", "abc"));
//		array.put(new JSONObject().put("username", "123"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		
//		out.print(array);
//		out.close();
		
		/**
		 * use TicketMasterAPI
		 */
		
		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		//term can be empty
		String keyword = request.getParameter("term");
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		List<Item> itemList = tmAPI.search(lat, lon, keyword);
		
		JSONArray jsonArray = new JSONArray();
		
		for (Item item : itemList) {
			jsonArray.put(item.toJSONObject());
		}
		
		/**
		 * my version
		 */
//		try {
//			response.getWriter().print(jsonArray);
//			response.setContentType("application/json");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		RpcHelper.writeJsonArray(response, jsonArray);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
