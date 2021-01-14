package com.socatel.filters;

import com.socatel.components.Methods;
import com.socatel.models.User;
import com.socatel.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

@Component
public class LanguageFilter implements Filter {

    private static UserService staticUserService;
    @Autowired private UserService userService;

    static class CustomRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest wrapped;

        private Map<String, String[]> parameterMap;

        public CustomRequestWrapper(HttpServletRequest wrapped) {
            super(wrapped);
            this.wrapped = wrapped;
            try {
                User user = Methods.getLoggedInUser(staticUserService);
                addParameter("lang", user.getFirstLang().getCode());
            } catch (ClassCastException e) {
                addParameter("lang", wrapped.getLocale().getLanguage());
            }
        }

        public void addParameter(String name, String value) {
            if (parameterMap == null) {
                parameterMap = new HashMap<>();
                parameterMap.putAll(wrapped.getParameterMap());
            }
            String[] values = parameterMap.get(name);
            if (values == null) {
                values = new String[0];
            }
            List<String> list = new ArrayList<>(values.length + 1);
            list.addAll(Arrays.asList(values));
            list.add(value);
            parameterMap.put(name, list.toArray(new String[0]));
        }

        @Override
        public String getParameter(String name) {
            if (parameterMap == null) {
                return wrapped.getParameter(name);
            }

            String[] strings = parameterMap.get(name);
            if (strings != null) {
                return strings[0];
            }
            return null;
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            if (parameterMap == null) {
                return wrapped.getParameterMap();
            }
            return Collections.unmodifiableMap(parameterMap);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            if (parameterMap == null) {
                return wrapped.getParameterNames();
            }

            return Collections.enumeration(parameterMap.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            if (parameterMap == null) {
                return wrapped.getParameterValues(name);
            }
            return parameterMap.get(name);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        staticUserService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(new CustomRequestWrapper((HttpServletRequest)servletRequest), servletResponse);
    }

    @Override
    public void destroy() {

    }
}
