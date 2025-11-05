package com.smartfarming.servlet;

import java.io.*;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/CropRecommendationServlet")
public class CropRecommendationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String soilType = req.getParameter("soil_type");
        float phLevel = Float.parseFloat(req.getParameter("ph_level"));
        float moisture = Float.parseFloat(req.getParameter("moisture_level"));
        String season = req.getParameter("season");

        List<String> recommendedCrops = new ArrayList<>();

        if (season.equalsIgnoreCase("Monsoon")) {
            if (soilType.equalsIgnoreCase("Black") && phLevel >= 6.0 && moisture >= 30) {
                recommendedCrops.add("Rice");
            }
            if (soilType.equalsIgnoreCase("Red") && phLevel >= 5.5 && moisture >= 35) {
                recommendedCrops.add("Sugarcane");
            }
            if (soilType.equalsIgnoreCase("Alluvial") && phLevel >= 6.0 && moisture >= 30) {
                recommendedCrops.add("Cotton");
            }
            if (moisture >= 40) {
                recommendedCrops.add("Soybean");
            }
            if (phLevel < 6.0) {
                recommendedCrops.add("Groundnut");
            }
            if (recommendedCrops.isEmpty()) {
                recommendedCrops.add("Maize");
            }
        } else if (season.equalsIgnoreCase("Winter")) {
            if (soilType.equalsIgnoreCase("Red") && phLevel < 6.5) {
                recommendedCrops.add("Wheat");
            }
            if (soilType.equalsIgnoreCase("Black") && phLevel >= 6.0) {
                recommendedCrops.add("Barley");
            }
            if (moisture >= 25 && moisture < 35) {
                recommendedCrops.add("Gram");
            }
            if (phLevel >= 7.0) {
                recommendedCrops.add("Mustard");
            }
            if (soilType.equalsIgnoreCase("Alluvial")) {
                recommendedCrops.add("Peas");
            }
            if (recommendedCrops.isEmpty()) {
                recommendedCrops.add("Oats");
            }
        } else if (season.equalsIgnoreCase("Summer")) {
            if (moisture < 25) {
                recommendedCrops.add("Millets");
            }
            if (soilType.equalsIgnoreCase("Red") && phLevel >= 6.0) {
                recommendedCrops.add("Pulses");
            }
            if (soilType.equalsIgnoreCase("Black") && moisture >= 20) {
                recommendedCrops.add("Sunflower");
            }
            if (phLevel >= 7.0) {
                recommendedCrops.add("Groundnut");
            }
            if (moisture >= 25) {
                recommendedCrops.add("Sorghum");
            }
            if (recommendedCrops.isEmpty()) {
                recommendedCrops.add("Maize");
            }
        } else {
            recommendedCrops.add("Maize");  // default crop
        }
        List<String> defaultCrops = List.of("Cotton", "Wheat", "Maize", "Soybean", "Barley", "Pulses");
        int i = 0;
        while (recommendedCrops.size() < 4 && i < defaultCrops.size()) {
            String crop = defaultCrops.get(i);
            if (!recommendedCrops.contains(crop)) {
                recommendedCrops.add(crop);
            }
            i++;
        }

        // Join crop names for DB insert
        String cropName1 = String.join(", ", recommendedCrops);

        try {
            // JDBC setup
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/smart_farming", "root", "root");

            // Insert recommendation
            String query = "INSERT INTO croprecommendations (soil_type, ph_level, moisture, crop_name, season) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, soilType);
            pst.setFloat(2, phLevel);
            pst.setFloat(3, moisture);
            pst.setString(4, cropName1);
            pst.setString(5, season);
            pst.executeUpdate();

            // Response - create creative table for farmers
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();

            out.println("<html><head><title>Crop Recommendations</title>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(120deg, #d4fc79, #96e6a1); margin: 0; padding: 20px; }");
            out.println("h2 { text-align: center; color: #2e7d32; }");
            out.println("table { margin: 20px auto; border-collapse: collapse; width: 80%; background-color: #ffffffcc; box-shadow: 0 0 15px rgba(0,0,0,0.2); }");
            out.println("th, td { padding: 12px 20px; border: 1px solid #4caf50; text-align: center; }");
            out.println("th { background-color: #4caf50; color: white; }");
            out.println("tr:hover { background-color: #a5d6a7; }");
            out.println(".footer { text-align:center; margin-top: 30px; font-size: 0.9em; color: #2e7d32; }");
            out.println("</style>");
            out.println("</head><body>");

            out.println("<h2>Crop Recommendations for Your Farm</h2>");
            out.println("<table>");
            out.println("<tr><th>Soil Type</th><th>pH Level</th><th>Moisture Level (%)</th><th>Season</th><th>Recommended Crops</th></tr>");
            out.println("<tr>");
            out.println("<td>" + soilType + "</td>");
            out.println("<td>" + phLevel + "</td>");
            out.println("<td>" + moisture + "</td>");
            out.println("<td>" + season + "</td>");
            out.println("<td>" + cropName1 + "</td>");
            out.println("</tr>");
            out.println("</table>");

            out.println("<div class='footer'>");
            out.println("Happy Farming! 🌱🚜<br>");
            out.println("For more tips and updates, visit your Smart Farming Dashboard.");
            out.println("</div>");
            out.println("<a href='dashboard.html'>Back to Dashboard</a>");
            out.println("</body></html>");
            

        } catch (Exception e) {
            e.printStackTrace();
            res.getWriter().println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
        }
    }
}
