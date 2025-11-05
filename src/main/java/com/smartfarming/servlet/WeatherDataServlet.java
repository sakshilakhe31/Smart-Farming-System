package com.smartfarming.servlet;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/WeatherServlet")
public class WeatherDataServlet extends HttpServlet {
    private static final String API_KEY = "d07d2aebee7e89f7d323407146712d2b";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double lat = Double.parseDouble(request.getParameter("lat"));
        double lon = Double.parseDouble(request.getParameter("lon"));

        String urlString = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY + "&units=metric";

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder responseData = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            responseData.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(responseData.toString());

        String city = json.getString("name");
        String weatherMain = json.getJSONArray("weather").getJSONObject(0).getString("main");
        String description = json.getJSONArray("weather").getJSONObject(0).getString("description");
        String icon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
        double temp = json.getJSONObject("main").getDouble("temp");

        JSONObject result = new JSONObject();
        result.put("city", city);
        result.put("main", weatherMain);
        result.put("description", description);
        result.put("icon", icon);
        result.put("temperature", temp);

        response.setContentType("application/json");
        response.getWriter().print(result.toString());
    }
}
