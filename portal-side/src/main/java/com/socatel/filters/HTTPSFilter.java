package com.socatel.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// TODO https://nvanhuong.wordpress.com/2013/11/25/redirect-http-to-https-using-servlet-filter/

// @Component // TODO uncomment before deploying to GCloud
public class HTTPSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws java.io.IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String protocol = req.getScheme();
        String domain = req.getServerName();
        String port = Integer.toString(req.getServerPort());

        if (protocol.toLowerCase().equals("http")) {

            // Set response content type
            response.setContentType("text/html");

            // New location to be redirected
            String site = "https" + "://" + domain /*+ ":" + port*/ + uri;

            /*
            res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); // or SC_MOVED_PERMANENTLY
            res.setHeader("Location", site);
             */

            res.sendRedirect(site);
        }
        // Pass request back down the filter chain
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

    @Override
    public void destroy() {

    }

}
