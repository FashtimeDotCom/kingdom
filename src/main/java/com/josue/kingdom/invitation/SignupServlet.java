/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.josue.kingdom.invitation;

import com.josue.kingdom.invitation.entity.Invitation;
import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Josue
 */
@WebServlet(name = "Signup", urlPatterns = {"/signup"})
public class SignupServlet extends HttpServlet {

    @Inject
    InvitationControl control;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {

            String token = request.getParameter("token");
            //TODO if exists show some message
            //Validate all signup on Control
            if (control.isSignup(token)) {
                Invitation invitationByToken = control.getInvitationByToken(token);

                request.getSession().setAttribute("token", token);
                request.getSession().setAttribute("email", invitationByToken.getTargetEmail());
                request.getRequestDispatcher("/signup.jsp").forward(request, response);
            } else {
                //TODO manager already exists
            }

//            http://localhost:8080/credential-manager/api/invitations/join?token=2fe7f705-b887-4156-a6c7-6f761edf10b9
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
