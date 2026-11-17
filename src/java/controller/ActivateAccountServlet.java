package controller;

import dal.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import tool.PasswordGenerator;

/**
 * Public: handles the "Activate Account" button from the account-creation
 * email. URL: /activate-account?token=xxxx
 * Only the SHA-256 hash of the raw token ever touches the database - see
 * AccountDAO / PasswordGenerator.hashToken().
 */
@WebServlet("/activate-account")
public class ActivateAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String rawToken = request.getParameter("token");
        String tokenHash = PasswordGenerator.hashToken(rawToken);

        AccountDAO accountDAO = new AccountDAO();
        int result = accountDAO.activateByTokenHash(tokenHash);
        accountDAO.closeConnection();

        request.setAttribute("activated", result == 1);
        request.getRequestDispatcher("/activate_account.jsp").forward(request, response);
    }
}
