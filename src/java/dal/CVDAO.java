package dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import model.CVModel;

public class CVDAO extends DBContext {

    public int createCV(CVModel cv) {
        if (c == null) return -1;
        String sql = "INSERT INTO CV (CandidateID, FullName, Address, Email, Position, NumberExp, "
                + "Education, Field, CurrentSalary, Birthday, Nationality, Gender, FileData) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cv.getCandidateId());
            ps.setString(2, cv.getFullName());
            ps.setString(3, cv.getAddress());
            ps.setString(4, cv.getEmail());
            ps.setString(5, cv.getPosition());
            if (cv.getNumberExp() != null) ps.setInt(6, cv.getNumberExp()); else ps.setNull(6, Types.INTEGER);
            ps.setString(7, cv.getEducation());
            ps.setString(8, cv.getField());
            ps.setBigDecimal(9, cv.getCurrentSalary());
            ps.setDate(10, cv.getBirthday());
            ps.setString(11, cv.getNationality());
            ps.setString(12, cv.getGender());
            ps.setString(13, cv.getFileData());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            System.err.println("CVDAO.createCV error: " + e.getMessage());
        }
        return -1;
    }
}
