package com.electricity;

import com.electricity.servlet.ElectricityBillServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Embedded Tomcat 10 launcher for quick standalone testing and demonstration.
 * Serves static web assets and registers ElectricityBillServlet.
 * Runs on http://localhost:8080/ (or fallback 8085).
 *
 * To run via Maven:
 *   mvn compile exec:java
 */
public class EmbeddedTomcatServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String portProp = System.getProperty("server.port");
        if (portProp != null && !portProp.isEmpty()) {
            try {
                port = Integer.parseInt(portProp);
            } catch (NumberFormatException ignored) {}
        } else {
            // Check if 8080 is busy; if so, fall back to 8085
            try (java.net.ServerSocket s = new java.net.ServerSocket(8080)) {
                port = 8080;
            } catch (Exception e) {
                port = 8085;
            }
        }

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir("target/tomcat");

        // Force creation of default HTTP connector
        tomcat.getConnector();

        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            throw new IllegalStateException("Cannot find webapp directory at: " + webappDir.getAbsolutePath());
        }

        Context ctx = tomcat.addContext("", webappDir.getAbsolutePath());

        // DefaultServlet serves static files: index.html, css/style.css, js/script.js
        org.apache.catalina.Wrapper defaultServlet = Tomcat.addServlet(ctx, "default", "org.apache.catalina.servlets.DefaultServlet");
        defaultServlet.addInitParameter("debug", "0");
        defaultServlet.addInitParameter("listings", "false");
        defaultServlet.setLoadOnStartup(1);
        ctx.addServletMappingDecoded("/", "default");

        // Register ElectricityBillServlet
        ElectricityBillServlet servlet = new ElectricityBillServlet();
        Tomcat.addServlet(ctx, "ElectricityBillServlet", servlet);
        ctx.addServletMappingDecoded("/ElectricityBillServlet", "ElectricityBillServlet");
        ctx.addServletMappingDecoded("/calculate-bill", "ElectricityBillServlet");

        // Welcome file
        ctx.addWelcomeFile("index.html");

        System.out.println("===============================================================");
        System.out.println("  ⚡ ElectroBill - Electricity Bill Calculator Live!          ");
        System.out.println("  🌐 Open in Browser: http://localhost:" + port + "/          ");
        System.out.println("  📋 Progressive Tariff Slabs: 0-50, 51-150, 151-250, >250     ");
        System.out.println("===============================================================");

        tomcat.start();
        tomcat.getServer().await();
    }
}
