package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import model.CandidateModel;

public class CandidateDAO extends DBContext {

    public CandidateModel findByEmail(String email) {
        if (c == null) return null;
        String sql = "SELECT CandidateID, CandidateName, Address, Email, PhoneNumber, Nationality "
                + "FROM Candidate WHERE Email = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CandidateModel m = new CandidateModel();
                    m.setCandidateId(rs.getInt("CandidateID"));
                    m.setCandidateName(rs.getString("CandidateName"));
                    m.setAddress(rs.getString("Address"));
                    m.setEmail(rs.getString("Email"));
                    m.setPhoneNumber(rs.getString("PhoneNumber"));
                    m.setNationality(rs.getString("Nationality"));
                    return m;
                }
            }
        } catch (Exception e) {
            System.err.println("CandidateDAO.findByEmail error: " + e.getMessage());
        }
        return null;
    }

    public boolean isPhoneTaken(String phone, String excludingEmail) {
        if (c == null) return false;
        String sql = "SELECT COUNT(*) FROM Candidate WHERE PhoneNumber = ? AND Email <> ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, excludingEmail == null ? "" : excludingEmail);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            System.err.println("CandidateDAO.isPhoneTaken error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Creates a new Candidate row for someone applying without logging in.
     * PasswordHash is filled with a random, unusable value since there is no
     * candidate login flow yet - it only exists to satisfy the NOT NULL column.
     */
    public int createCandidate(CandidateModel m, String randomPasswordHash) {
        if (c == null) return -1;
        String sql = "INSERT INTO Candidate (CandidateName, Address, Email, PhoneNumber, Nationality, PasswordHash) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getCandidateName());
            ps.setString(2, m.getAddress());
            ps.setString(3, m.getEmail());
            ps.setString(4, m.getPhoneNumber());
            ps.setString(5, m.getNationality());
            ps.setString(6, randomPasswordHash);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("CandidateDAO.createCandidate error: " + e.getMessage());
        }
        return -1;
    }
}
