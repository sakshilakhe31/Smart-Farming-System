package com.smartfarming.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CostEstimationServlet")
public class CostEstimationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final String jdbcURL = "jdbc:mysql://localhost:3306/smart_farming";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "root";

    private static final String INSERT_SQL = 
        "INSERT INTO costestimation (farmer_id, crop_id, estimated_cost, estimated_profit, area) VALUES (?, ?, ?, ?, ?);";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int farmerId = Integer.parseInt(request.getParameter("farmerId"));
        int cropId = Integer.parseInt(request.getParameter("cropId"));
        double estimatedCost = Double.parseDouble(request.getParameter("estimatedCost"));
        double estimatedProfit = Double.parseDouble(request.getParameter("estimatedProfit"));
        double area = Double.parseDouble(request.getParameter("area")); // New field

        double profitPerArea = estimatedProfit / area;

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            out.println("<h3>Error: MySQL Driver not found!</h3>");
            return;
        }

        try (Connection connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement ps = connection.prepareStatement(INSERT_SQL)) {

            ps.setInt(1, farmerId);
            ps.setInt(2, cropId);
            ps.setDouble(3, estimatedCost);
            ps.setDouble(4, estimatedProfit);
            ps.setDouble(5, area);

            int rows = ps.executeUpdate();

            out.println("<!DOCTYPE html><html><head><title>Estimation Result</title>");
            out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', sans-serif; background: #e0f7fa; padding: 20px; }");
            out.println(".result-box { background: #fff; padding: 25px; border-radius: 10px; max-width: 600px; margin: 40px auto; box-shadow: 0 0 15px rgba(0,0,0,0.1); }");
            out.println("h2 { color: #00796b; text-align: center; }");
            out.println("p { font-size: 16px; color: #004d40; margin: 10px 0; }");
            out.println("canvas { max-width: 100%; margin-top: 20px; }");
            out.println("a { display: block; text-align: center; margin-top: 20px; color: #00796b; text-decoration: none; font-weight: bold; }");
            out.println("a:hover { text-decoration: underline; }");
            out.println("</style></head><body>");
            out.println("<div class='result-box'>");

            if (rows > 0) {
                out.printf("<h2>Estimation Submitted Successfully!</h2>");
                out.printf("<p><strong>Farmer ID:</strong> %d</p>", farmerId);
                out.printf("<p><strong>Crop ID:</strong> %d</p>", cropId);
                out.printf("<p><strong>Estimated Cost:</strong> ₹%.2f</p>", estimatedCost);
                out.printf("<p><strong>Estimated Profit:</strong> ₹%.2f</p>", estimatedProfit);
                out.printf("<p><strong>Area:</strong> %.2f acres</p>", area);
                out.printf("<p><strong>Profit per Acre:</strong> ₹%.2f</p>", profitPerArea);

                // Bar Chart using Chart.js
                out.println("<canvas id='costChart'></canvas>");
                out.println("<script>");
                out.println("const ctx = document.getElementById('costChart').getContext('2d');");
                out.println("const costChart = new Chart(ctx, {");
                out.println("  type: 'bar',");
                out.println("  data: {");
                out.println("    labels: ['Cost', 'Profit', 'Profit per Acre'],");
                out.printf("    datasets: [{ label: 'Estimation (₹)', data: [%.2f, %.2f, %.2f], backgroundColor: ['#42a5f5','#66bb6a','#ffa726'] }]",
                    estimatedCost, estimatedProfit, profitPerArea);
                out.println("  },");
                out.println("  options: { responsive: true, plugins: { legend: { display: false } }, scales: { y: { beginAtZero: true } } }");
                out.println("});");
                out.println("</script>");
            } else {
                out.println("<p>Failed to save estimation. Please try again.</p>");
            }

            out.println("<a href='costestimation.html'>Back to Estimation Form</a>");
            out.println("<a href='dashboard.html'>Back to Dashboard</a>");
            out.println("</div></body></html>");

        } catch (SQLException e) {
            e.printStackTrace(out);
            out.println("<p>Error during database operation.</p>");
        }
    }
}
