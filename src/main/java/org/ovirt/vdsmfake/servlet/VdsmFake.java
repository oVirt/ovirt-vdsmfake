package org.ovirt.vdsmfake.servlet;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ovirt.vdsmfake.domain.VdsmManager;

public class VdsmFake extends HttpServlet {

    @Inject
    private VdsmManager vdsmManager;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StringBuilder output = new StringBuilder();
        output.append("Hello, this is VDSM Fake!").append("\n");
        output.append("===== Statistics ======").append("\n");
        output.append("running hosts: ").append(vdsmManager.getHostCount()).append("\n");
        output.append("running vms: ").append(vdsmManager.getRunningVmsCount()).append("\n");
        response.setContentType("text/plain");
        response.getWriter().write(output.toString());
        return;
    }
}
