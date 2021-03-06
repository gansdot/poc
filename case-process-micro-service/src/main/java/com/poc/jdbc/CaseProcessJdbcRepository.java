package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.CaseRegister;

@Repository
public class CaseProcessJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	class CaseRegisterRowMapper implements RowMapper<CaseRegister> {
		@Override
		public CaseRegister mapRow(ResultSet rs, int rowNum) throws SQLException {
			CaseRegister caseRegister = new CaseRegister();
			caseRegister.setId(rs.getInt("id"));
			caseRegister.setCaseId(rs.getString("case_id"));
			caseRegister.setName(rs.getString("name"));
			caseRegister.setStatus(rs.getString("status"));
			return caseRegister;
		}

	}

	public List<CaseRegister> findAll() {
		return jdbcTemplate.query("select * from POC.CASE_REGISTER", new CaseRegisterRowMapper());
	}

	public CaseRegister findById(String id) {
		return jdbcTemplate.queryForObject("select * from POC.CASE_REGISTER where id=?", new Object[] { id },
				new BeanPropertyRowMapper<CaseRegister>(CaseRegister.class));
	}

	public int deleteById(String id) {
		return jdbcTemplate.update("delete from POC.CASE_REGISTER where id=?", new Object[] { id });
	}

	public int insert(CaseRegister caseRegister) {
		return jdbcTemplate.update("insert into POC.CASE_REGISTER (case_id, name, status) " + "values(?,  ?, ?)",
				new Object[] { caseRegister.getCaseId(), caseRegister.getName(), caseRegister.getStatus() });
	}

	public int update(CaseRegister caseRegister) {
		return jdbcTemplate.update("update POC.CASE_REGISTER " + " set name = ?, status = ? " + " where id = ?",
				new Object[] { caseRegister.getName(), caseRegister.getStatus(), caseRegister.getId() });
	}
}
