package com.smartfarming.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/WaterScheduleServlet")
public class WaterScheduleServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Retrieve form values
        String crop = request.getParameter("crop");
        int frequency = Integer.parseInt(request.getParameter("frequency"));
        int duration = Integer.parseInt(request.getParameter("duration"));
        String weather = request.getParameter("weather");

        String recommendation = "";
        int adjustedFreq = frequency;
        int adjustedDuration = duration;

        // Adjust based on weather
        if ("Dry".equalsIgnoreCase(weather)) {
            recommendation = "Increase watering";
            adjustedFreq += 1;
            adjustedDuration += 10;
        } else if ("Rainy".equalsIgnoreCase(weather)) {
            recommendation = "Reduce watering";
            adjustedFreq = Math.max(1, adjustedFreq - 1);
            adjustedDuration = Math.max(5, adjustedDuration - 10);
        } else if ("Humid".equalsIgnoreCase(weather)) {
            recommendation = "Avoid watering";
            adjustedFreq = 0;
            adjustedDuration = 0;
        } else {
            recommendation = "Maintain watering";
        }

        // Output HTML and chart
        out.println("<!DOCTYPE html><html><head><title>Watering Recommendation</title>");
        out.println("<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>");
        out.println("<style>body { font-family: 'Segoe UI'; text-align:center; background:#e0f7fa; padding:20px; }");
        out.println(".card { background:#fff; padding:20px; margin:auto; width:400px; border-radius:10px; box-shadow: 0 0 8px rgba(0,0,0,0.1); }</style>");
        out.println("</head><body>");

        out.println("<h2>Recommended Watering Schedule</h2>");
        out.println("<div class='card'>");
        out.println("<p><b>Crop:</b> " + crop + "</p>");
        out.println("<p><b>Original Frequency:</b> " + frequency + " times/week</p>");
        out.println("<p><b>Original Duration:</b> " + duration + " minutes</p>");
        out.println("<p><b>Weather:</b> " + weather + "</p>");
        out.println("<p><b>Recommendation:</b> " + recommendation + "</p>");
        out.println("<hr>");
        out.println("<p><b>Adjusted Frequency:</b> " + (adjustedFreq > 0 ? adjustedFreq + " times/week" : "No watering") + "</p>");
        out.println("<p><b>Adjusted Duration:</b> " + adjustedDuration + " minutes</p>");
        out.println("</div>");

        out.println("<canvas id='chart' width='400' height='200' style='margin-top:30px;'></canvas>");
        out.println("<script>");
        out.println("const ctx = document.getElementById('chart').getContext('2d');");
        out.println("const myChart = new Chart(ctx, {");
        out.println("  type: 'bar',");
        out.println("  data: {");
        out.println("    labels: ['Original Freq', 'Adjusted Freq', 'Original Dur', 'Adjusted Dur'],");
        out.println("    datasets: [{");
        out.println("      label: 'Watering Schedule (times/minutes)',");
        out.println("      data: [" + frequency + ", " + adjustedFreq + ", " + duration + ", " + adjustedDuration + "],");
        out.println("      backgroundColor: ['#80cbc4', '#004d40', '#b2dfdb', '#00796b']");
        out.println("    }]");
        out.println("  },");
        out.println("  options: { scales: { y: { beginAtZero: true } } }");
        out.println("});");
        out.println("</script>");

        out.println("<a href='dashboard.html'>Back to Dashboard</a>");
        out.println("</body></html>");
        
    }
}
