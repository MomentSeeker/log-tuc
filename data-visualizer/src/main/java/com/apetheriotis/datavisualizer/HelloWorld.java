package com.apetheriotis.datavisualizer;// Import required java libraries

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.apetheriotis.datavisualizer.domain.StatusCode;
import com.google.gson.Gson;

// Extend HttpServlet class
public class HelloWorld extends HttpServlet {

    int i = 0;
    private String message;

    @Override
    public void destroy() {
        // do nothing.
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {

        StatusCode sc = new StatusCode();
        sc.setTime(1398886486000L);

        Map<String, Long> lala = new HashMap<>();
        lala.put("time", 0L);
        lala.put("code_200", 5000L);
        lala.put("code_500", 100L);

        Map<String, Long> lala2 = new HashMap<>();
        lala2.put("time", 10L);
        lala2.put("code_200", 51000L);
        lala2.put("code_500", 1200L);

        List<Map<String, Long>> kia = new ArrayList<Map<String, Long>>();

        if (i < 10) {
            kia.add(lala);
            kia.add(lala2);
        }

        i++;

        // Set response content type
        response.setContentType("application/json");

        // Actual logic goes here.
        PrintWriter out = response.getWriter();
        out.println(new Gson().toJson(kia));
    }

    @Override
    public void init() throws ServletException {
        // Do required initialization
        message = "Hello World";
    }
}