package com.poc.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.poc.model.Debit;

@Repository
public class DebitJdbcRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	class DebitRowMapper implements RowMapper<Debit> {
		@Override
		public Debit mapRow(ResultSet rs, int rowNum) throws SQLException {
			Debit debit = new Debit();
			return debit;
		}

	}

	public List<Debit> findAll() {
		return jdbcTemplate.query("select * from POC.DEBIT", new DebitRowMapper());
	}

	public Debit findById(String sfCaseId) {
		return jdbcTemplate.queryForObject("select * from POC.DEBIT where SF_CASE_ID=?", new Object[] { sfCaseId },
				new BeanPropertyRowMapper<Debit>(Debit.class));
	}

	public int deleteById(String sfCaseId) {
		return jdbcTemplate.update("delete from POC.DEBIT where SF_CASE_ID=?", new Object[] { sfCaseId });
	}

	public int insert(Debit debit) {
		return jdbcTemplate.update("insert into POC.DEBIT (SF_CASE_ID, DEBIT_ACCOUNT, DEBIT_AMOUNT) values(?,  ?, ?)",
				new Object[] { debit.getSfCaseId(), debit.getDebitAccount(), debit.getDebitAmount() });
	}


}
