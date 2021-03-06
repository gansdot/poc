package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.ForcecaseData;
@Repository
public class ForceclientJdbcRepository {
	
	
	@Autowired
	JdbcTemplate jdbcTemplate;

	class ForcecaseDataRowMapper implements RowMapper<ForcecaseData> {
		@Override
		public ForcecaseData mapRow(ResultSet rs, int rowNum) throws SQLException {
			ForcecaseData dataCollect = new ForcecaseData();
			dataCollect.setCaseId(rs.getInt("CASE_ID"));
			dataCollect.setSfCaseId(rs.getString("SF_CASE_ID"));
			dataCollect.setCaseNumber(rs.getString("SF_CASE_NUMBER"));			
			dataCollect.setCaseOwner(rs.getString("CASE_OWNER"));
			dataCollect.setDebitAccount(rs.getString("DEBIT_ACCOUNT"));
			dataCollect.setDebitAmount(rs.getDouble("DEBIT_AMOUNT"));
			dataCollect.setDebitDescription(rs.getString("DEBIT_DESCRIPTION"));
			dataCollect.setCreditAccount(rs.getString("CREDIT_ACCOUNT"));
			dataCollect.setBeneficiaryName(rs.getString("BENEFICIARY_NAME"));
			dataCollect.setCaseDatetime(rs.getString("CASE_DATETIME"));
			dataCollect.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
			dataCollect.setSwiftBic(rs.getString("SWIFT_BIC"));		
			return dataCollect;
		}

	}

	public List<ForcecaseData> findAll() {
		return jdbcTemplate.query("select * from POC.DATA_COLLECTION", new ForcecaseDataRowMapper());
	}

	public ForcecaseData findById(String caseNumber) {
		return jdbcTemplate.queryForObject("select * from POC.DATA_COLLECTION where SF_CASE_NUMBER = ?", new Object[] { caseNumber },
				new BeanPropertyRowMapper<ForcecaseData>(ForcecaseData.class));
	}

	public int deleteById(String id) {
		return jdbcTemplate.update("delete from POC.DATA_COLLECTION where SF_CASE_NUMBER = ?", new Object[] { id });
	}

	public int insert(ForcecaseData dataCollect) {
		return jdbcTemplate.update("insert into POC.DATA_COLLECTION (SF_CASE_ID, SF_CASE_NUMBER, CASE_OWNER, "
				+ "DEBIT_ACCOUNT, DEBIT_AMOUNT, DEBIT_DESCRIPTION,"
				+ "CREDIT_ACCOUNT,BENEFICIARY_NAME, CASE_DATETIME, EFFECTIVE_DATE, SWIFT_BIC) " 
				+ "values(?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { dataCollect.getSfCaseId(), dataCollect.getCaseNumber(), dataCollect.getCaseOwner(),
						dataCollect.getDebitAccount(), dataCollect.getDebitAmount(), dataCollect.getDebitDescription(),
						dataCollect.getCreditAccount(), dataCollect.getBeneficiaryName(), dataCollect.getCaseDatetime(),
						dataCollect.getEffectiveDate(),dataCollect.getSwiftBic()});
	}

	public int update(ForcecaseData dataCollect) {
		return jdbcTemplate.update("update POC.DATA_COLLECTION " + " set DEBIT_AMOUNT = ? where SF_CASE_NUMBER = ?",
				new Object[] { dataCollect.getDebitAmount(), dataCollect.getSfCaseId() });
	}


}
