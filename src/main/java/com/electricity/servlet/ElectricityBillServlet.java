package com.electricity.servlet;

import com.electricity.model.BillResult;
import com.electricity.service.ElectricityBillCalculator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * ElectricityBillServlet handles HTTP POST requests for calculating electricity bills.
 * Accepts:
 *  - consumerName: Consumer's full name
 *  - consumerNumber: Unique account / meter ID
 *  - units: Total units consumed in the billing period
 *
 * Performs server-side validation and progressive tariff slab calculation.
 * Returns JSON representation of BillResult with HTTP 200 (success) or HTTP 400 (validation error).
 */
@WebServlet(name = "ElectricityBillServlet", urlPatterns = {"/ElectricityBillServlet", "/calculate-bill"})
public class ElectricityBillServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ElectricityBillCalculator calculator;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        this.calculator = new ElectricityBillCalculator();
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests back to the main calculator interface
        response.sendRedirect(request.getContextPath() + "/index.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Ensure UTF-8 character encoding
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // Extract parameters from form submission or AJAX payload
        String consumerName = request.getParameter("consumerName");
        String consumerNumber = request.getParameter("consumerNumber");
        String units = request.getParameter("units");

        // Perform server-side validation and progressive calculation
        BillResult result = calculator.calculate(consumerName, consumerNumber, units);

        // Check whether the client prefers JSON or if it's standard HTML form submit
        String acceptHeader = request.getHeader("Accept");
        boolean wantsJson = (acceptHeader == null || acceptHeader.contains("application/json") 
                             || "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"))
                             || "fetch".equalsIgnoreCase(request.getHeader("Sec-Fetch-Mode")));

        if (wantsJson) {
            response.setContentType("application/json;charset=UTF-8");
            if (!result.isSuccess()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
            }
            PrintWriter out = response.getWriter();
            out.print(gson.toJson(result));
            out.flush();
        } else {
            // Server-side HTML fallback for non-JS browsers
            renderHtmlResponse(response, result);
        }
    }

    /**
     * Fallback HTML rendering in case JavaScript is disabled on client browser.
     */
    private void renderHtmlResponse(HttpServletResponse response, BillResult result) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Electricity Bill Receipt</title>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container' style='max-width: 650px; margin: 40px auto; padding: 20px;'>");
        
        if (!result.isSuccess()) {
            out.println("<div class='error-box' style='display:block;'>");
            out.println("<h3>Calculation Error</h3>");
            out.println("<p>" + result.getErrorMessage() + "</p>");
            out.println("<a href='index.html' class='btn btn-secondary' style='margin-top:15px; display:inline-block;'>Back to Calculator</a>");
            out.println("</div>");
        } else {
            out.println("<div class='card result-card' style='display:block;'>");
            out.println("<h2 style='color: #059669;'>⚡ Electricity Bill Receipt</h2>");
            out.println("<p><strong>Consumer Name:</strong> " + result.getConsumerName() + "</p>");
            out.println("<p><strong>Consumer Number:</strong> " + result.getConsumerNumber() + "</p>");
            out.println("<p><strong>Units Consumed:</strong> " + result.getFormattedTotalUnits() + " kWh</p>");
            out.println("<p><strong>Total Amount:</strong> " + result.getFormattedTotalAmount() + "</p>");
            out.println("<hr>");
            out.println("<h4>Progressive Slab Breakdown</h4>");
            out.println("<table class='slab-table'>");
            out.println("<thead><tr><th>Slab</th><th>Units</th><th>Rate</th><th>Amount</th></tr></thead>");
            out.println("<tbody>");
            for (var slab : result.getSlabs()) {
                out.println("<tr>");
                out.println("<td>" + slab.getSlabTitle() + "</td>");
                out.println("<td>" + slab.getFormattedUnits() + "</td>");
                out.println("<td>" + slab.getFormattedRate() + "</td>");
                out.println("<td>" + slab.getFormattedAmount() + "</td>");
                out.println("</tr>");
            }
            out.println("</tbody></table>");
            out.println("<div style='margin-top:20px; display:flex; gap:10px;'>");
            out.println("<button onclick='window.print()' class='btn btn-primary'>🖨️ Print Bill</button>");
            out.println("<a href='index.html' class='btn btn-secondary'>Calculate Another</a>");
            out.println("</div>");
            out.println("</div>");
        }

        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
    }
}
