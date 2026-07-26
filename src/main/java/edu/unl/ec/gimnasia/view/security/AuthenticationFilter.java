package edu.unl.ec.gimnasia.view.security;

import edu.unl.ec.gimnasia.domain.security.Role;
import jakarta.inject.Inject;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {
        "/judge/*",
        "/admin/*",
        "/security/*",
        "/competition/*",
        "/gymnast/*",
        "/dashboard.xhtml"
})
public class AuthenticationFilter implements Filter {

    @Inject
    private UserSessionBean userSessionBean;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);

        if (userSessionBean == null || !userSessionBean.isAuthenticated()) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.xhtml");
            return;
        }

        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
        boolean adminOnly = path.startsWith("/admin/") || path.startsWith("/competition/")
                || path.startsWith("/gymnast/") || path.startsWith("/security/");
        boolean judgeOnly = path.startsWith("/judge/");



        boolean sharedRanking = path.equals("/judge/ranking.xhtml");

        if (sharedRanking) {
            boolean allowed = userSessionBean.hasRole(Role.ADMINISTRADOR) || userSessionBean.hasRole(Role.JUEZ);
            if (!allowed) {
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard.xhtml");
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        if (adminOnly && !userSessionBean.hasRole(Role.ADMINISTRADOR)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard.xhtml");
            return;
        }
        if (judgeOnly && !userSessionBean.hasRole(Role.JUEZ)) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/dashboard.xhtml");
            return;
        }

        chain.doFilter(request, response);
    }
}