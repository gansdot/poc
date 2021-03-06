package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.Credit;

@Repository
public class CreditJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	class CreditRowMapper implements RowMapper<Credit> {
		@Override
		public Credit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Credit credit = new Credit();
			credit.setSfCaseId(rs.getString("SF_CASE_ID"));
			credit.setCreditAccount(rs.getString("CREDIT_ACCOUNT"));
			credit.setCreditAmount(rs.getDouble("CREDIT_AMOUNT"));
			credit.setBeneficiaryName(rs.getString("BENEFICIARY_NAME"));
			credit.setSwiftBic(rs.getString("SWIFT_BIC"));
			credit.setCreditDatetime(rs.getString("CREDIT_DATETIME"));
			return credit;
		}

	}

	public List<Credit> findAll() {
		return jdbcTemplate.query("select * from POC.CREDIT", new CreditRowMapper());
	}

	public Credit findById(String id) {
		return jdbcTemplate.queryForObject("select * from POC.CREDIT where SF_CASE_ID = ?", new Object[] { id },
				new BeanPropertyRowMapper<Credit>(Credit.class));
	}

	public int deleteById(String id) {
		return jdbcTemplate.update("delete from POC.CREDIT where SF_CASE_ID = ?", new Object[] { id });
	}

	public int insert(Credit credit) {
		return jdbcTemplate.update("insert into POC.CREDIT (SF_CASE_ID, CREDIT_ACCOUNT, CREDIT_AMOUNT, BENEFICIARY_NAME, SWIFT_BIC, CREDIT_DATETIME) " + "values(?,?,?,?,?,?)",
				new Object[] { credit.getSfCaseId(), credit.getCreditAccount(), credit.getCreditAmount(), credit.getBeneficiaryName(), credit.getSwiftBic(),credit.getCreditDatetime() });
	}

	
}
