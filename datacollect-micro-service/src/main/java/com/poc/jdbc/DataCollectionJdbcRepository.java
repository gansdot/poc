package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.DataCollection;

@Repository
public class DataCollectionJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	class DataCollectionRowMapper implements RowMapper<DataCollection> {
		@Override
		public DataCollection mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataCollection dataCollect = new DataCollection();
			dataCollect.setCaseId(rs.getInt("CASE_ID"));
			dataCollect.setCaseNumber(rs.getString("CASE_NUMBER"));
			dataCollect.setCaseOwner(rs.getString("CASE_OWNER"));
			dataCollect.setDebitAccount(rs.getString("DEBIT_ACCOUNT"));
			dataCollect.setDebitAmount(rs.getDouble("DEBIT_AMOUNT"));
			dataCollect.setDebitDescription(rs.getString("DEBIT_DESCRIPTION"));
			dataCollect.setCreditAccount(rs.getString("CREDIT_ACCOUNT"));
			dataCollect.setSfCaseId(rs.getString("SF_CASE_ID"));
			dataCollect.setBeneficiaryName(rs.getString("BENEFICIARY_NAME"));
			dataCollect.setCaseDatetime(rs.getString("CASE_DATETIME"));
			dataCollect.setEffectiveDate(rs.getString("EFFECTIVE_DATE"));
			dataCollect.setSwiftBic(rs.getString("SWIFT_BIC"));		
			return dataCollect;
		}

	}

	public List<DataCollection> findAll() {
		return jdbcTemplate.query("select * from POC.DATA_COLLECTION", new DataCollectionRowMapper());
	}

	public DataCollection findById(String caseId) {
		return jdbcTemplate.queryForObject("select * from POC.DATA_COLLECTION where caseId=?", new Object[] { caseId },
				new BeanPropertyRowMapper<DataCollection>(DataCollection.class));
	}

	public int deleteById(String id) {
		return jdbcTemplate.update("delete from POC.DATA_COLLECTION where id=?", new Object[] { id });
	}

	public int insert(DataCollection dataCollect) {
		return jdbcTemplate.update("insert into POC.DATA_COLLECTION (SF_CASE_ID, CASE_NUMBER, CASE_OWNER, "
				+ "DEBIT_ACCOUNT, DEBIT_AMOUNT, DEBIT_DESCRIPTION,"
				+ "CREDIT_ACCOUNT,BENEFICIARY_NAME, CASE_DATETIME, EFFECTIVE_DATE, SWIFT_BIC) " 
				+ "values(?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { dataCollect.getSfCaseId(), dataCollect.getCaseNumber(), dataCollect.getCaseOwner(),
						dataCollect.getDebitAccount(), dataCollect.getDebitAmount(), dataCollect.getDebitDescription(),
						dataCollect.getCreditAccount(), dataCollect.getBeneficiaryName(), dataCollect.getCaseDatetime(),
						dataCollect.getEffectiveDate(),dataCollect.getSwiftBic()});
	}

	public int update(DataCollection dataCollect) {
		return jdbcTemplate.update("update POC.DATA_COLLECTION " + " set DEBIT_AMOUNT = ? where SF_CASE_ID = ?",
				new Object[] { dataCollect.getDebitAmount(), dataCollect.getSfCaseId() });
	}
}