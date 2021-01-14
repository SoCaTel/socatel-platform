package com.socatel.handlers;

import com.socatel.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public CustomAuthenticationSuccessHandler(String defaultSuccessUrl) {
        setDefaultTargetUrl(defaultSuccessUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession();
        if (session != null) {
            User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            session.setAttribute("username", authUser.getUsername());
            session.setAttribute("authorities", authentication.getAuthorities());
            String redirectUrl = (String) session.getAttribute("url_prior_login");
            DefaultSavedRequest savedRequest = ((DefaultSavedRequest) session.getAttribute("SPRING_SECURITY_SAVED_REQUEST"));
            //set our response to OK status
            response.setStatus(HttpServletResponse.SC_OK);
            if (savedRequest != null) { // Try to access to secured url
                String redirectUrl2 = savedRequest.getRequestURI();
                // Clean attribute from session
                session.removeAttribute("SPRING_SECURITY_SAVED_REQUEST");
                // Redirect
                getRedirectStrategy().sendRedirect(request, response, redirectUrl2);
            } else if (redirectUrl != null) { // Comes from a public url inside the platform
                // Clean attribute from session
                session.removeAttribute("url_prior_login");
                // Redirect
                if (redirectUrl.contains("/topic"))
                    getRedirectStrategy().sendRedirect(request, response, redirectUrl);
                else getRedirectStrategy().sendRedirect(request, response, getDefaultTargetUrl());
            } else { // Unknown previous url
                getRedirectStrategy().sendRedirect(request, response, getDefaultTargetUrl());
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
        // fixes a bug: makes the redirect work, otherwise it can get stuck
        return;
    }
}
