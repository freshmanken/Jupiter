package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "UcDRxhDNQ9gJz0bwBIsNLjHuQCIyDqgA";
	
	public List<Item> search (double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);
		
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
		
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			System.out.println("\nSending GET Request to URL: " + URL + "?" + query);
			System.out.println("Response Code : " + responseCode);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			JSONObject obj = new JSONObject(response.toString());
			if (obj.isNull("_embedded")) {
				return new ArrayList<>();
			}
			
			JSONObject embedded = obj.getJSONObject("_embedded");
			JSONArray events = embedded.getJSONArray("events");
			return getItemList(events);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}
	
	/**
	 * Helper function for item informations
	 * @param event
	 * @return
	 * @throws Exception
	 */
	private String getAddress(JSONObject event) throws Exception {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for (int i = 0; i < venues.length(); i ++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sb = new StringBuilder();
					if (venue != null && !venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							sb.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sb.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sb.append(address.getString("line3"));
						}
						sb.append(",");
					}
					if (venue != null && !venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							sb.append(city.getString("name"));
						}
					}
					
					if (!sb.toString().equals("")) {
						return sb.toString();
					}
				}
			}
			
		}
		return "";
	}
	
	private String getImageUrl(JSONObject event) throws Exception {
		if (!event.isNull("images")) {
			JSONArray images = event.getJSONArray("images");
			for (int i = 0; i < images.length(); i ++) {
				JSONObject image = images.getJSONObject(i);
				if (!image.isNull("url")) {
					String url = image.getString("url");
					return url;
				}
			}
		}
		return "";
	}
	
	private Set<String> getCategories(JSONObject event) throws Exception {
		Set<String> categories = new HashSet<String>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); i ++) {
				JSONObject item = classifications.getJSONObject(i);
				if (!item.isNull("segment")) {
					JSONObject segment = item.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	
	/**
	 * convert events (with a lot of redundant information) to itemList (with only information we needed)
	 * @param events
	 * @return
	 * @throws Exception
	 */
	private List<Item> getItemList(JSONArray events) throws Exception {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); i ++) {
			JSONObject event = events.getJSONObject(i);
			
			ItemBuilder builder = new ItemBuilder();
			
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			
			if (!event.isNull("rating")) {
				builder.setRating(event.getLong("rating"));
			}
			
			if (!event.isNull("distance")) {
				builder.setDistance(event.getLong("distance"));
			}
			
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));
			builder.setAddress(getAddress(event));
			
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	/**
	 * combine queryAPI() and main() to test this whole API
	 * @param lat
	 * @param lon
	 */
	private void queryAPI (double lat, double lon) {
		List<Item> itemList = search (lat, lon, null);
		try {
			for (Item item : itemList) {
				JSONObject obj = item.toJSONObject();
				System.out.println(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		tmAPI.queryAPI(29.682684, -95.295410);
	}
	
	
}
