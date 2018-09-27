package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.Audit;

@Repository
public class AuditJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	class AuditRowMapper implements RowMapper<Audit> {
		@Override
		public Audit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Audit audit = new Audit();
			return audit;
		}

	}

	public List<Audit> findAll() {
		return jdbcTemplate.query("select * from POC.DEBIT", new AuditRowMapper());
	}

	public Audit findById(String sfCaseId) {
		return jdbcTemplate.queryForObject("select * from POC.DEBIT where SF_CASE_ID=?", new Object[] { sfCaseId },
				new BeanPropertyRowMapper<Audit>(Audit.class));
	}

	public int deleteById(String sfCaseId) {
		return jdbcTemplate.update("delete from POC.AUDIT where SF_CASE_ID=?", new Object[] { sfCaseId });
	}

	public int insert(Audit audit) {
		return jdbcTemplate.update("insert into POC.AUDIT (SF_CASE_ID, TNX_NAME, TNX_STATUS,"
				+ "REQ_DATA, REQ_DATETIME) values(?, ?, ?, ?, ?)",
				new Object[] { audit.getSfCaseId(), audit.getTnxName(), audit.getTnxStatus(),audit.getReqData(), 
						audit.getReqDatetime() });
	}

	public int update(Audit audit) {
		return jdbcTemplate.update("update POC.AUDIT set TNX_STATUS = ?, RES_DATA = ?, RES_DATETIME = ?"
				+ " where SF_CASE_ID = ? AND TNX_NAME = ? ",
				new Object[] {audit.getTnxStatus(), audit.getResData(),  audit.getResDatetime(),audit.getSfCaseId(), 
						audit.getTnxName() });
	}


}
