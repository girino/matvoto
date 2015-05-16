package org.girino.tse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseAccess {

	public final static String DEFAULT_HOST = "localhost";
	public final static String DEFAULT_DB = "tse";
	public final static String DEFAULT_USER = "root";
	public final static String DEFAULT_PWD = "";

	Connection conn;
	String dbname;
	String dbhost;
	String dbuser;
	String dbpwd;

	public DatabaseAccess() throws SQLException {
		this(DEFAULT_HOST, DEFAULT_DB, DEFAULT_USER, DEFAULT_PWD);
	}

	public DatabaseAccess(String dbhost, String dbname, String dbuser,
			String dbpwd) throws SQLException {
		this.dbhost = dbhost;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpwd = dbpwd;
		conn = DriverManager.getConnection("jdbc:mysql://" + dbhost + "/"
				+ dbname, dbuser, dbpwd);
	}

	public List<Map<String, Object>> listAll(String sql, Object[] params, String[] fields) throws SQLException {
		PreparedStatement p = conn.prepareStatement(sql);
		for (int i = 0; i < params.length; i++) {
			p.setObject(i+1, params[i]);
		}
		
		ResultSet rs = p.executeQuery();
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		while(rs.next()) {
			Map<String, Object>  m = new HashMap<String, Object>();
			for (String field : fields) {
				m.put(field, rs.getObject(field));
			}
			ret.add(m);
		}
		rs.close();
		p.close();
		return ret;
	}
	
	public List<Map<String, Object>> listAllVotesByCoalition(String state, int year, String position) throws SQLException {
		String SQL = "	SELECT SIGLA_UF, "
				+ "		TIPO_LEGENDA, "
				+ "		NOME_COLIGACAO, "
				+ "		COMPOSICAO_LEGENDA,"
				+ "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos "
				+ "	FROM votacao_partido "
				+ "	WHERE ANO_ELEICAO=? "
				+ "	  AND DESCRICAO_CARGO = ? "
				+ "	  AND SIGLA_UF=? "
				+ "	  AND TIPO_LEGENDA='C'"
				+ "	GROUP BY SIGLA_UF, NOME_COLIGACAO"
				+ " UNION ALL"
				+ "	SELECT SIGLA_UF, "
				+ "		TIPO_LEGENDA, "
				+ "		SIGLA_PARTIDO, "
				+ "		COMPOSICAO_LEGENDA,"
				+ "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos "
				+ "	FROM votacao_partido " 
				+ "	WHERE ANO_ELEICAO=? "
				+ "	  AND DESCRICAO_CARGO = ? "
				+ "	  AND SIGLA_UF=? " 
				+ "	  AND TIPO_LEGENDA='P'"
				+ "	GROUP BY SIGLA_UF, NUMERO_PARTIDO";
		
		String[] fields = new String[] {
				"SIGLA_UF",
				"TIPO_LEGENDA",
				"NOME_COLIGACAO",
				"COMPOSICAO_LEGENDA",
				"VOTOS",
		};
		Object[] params = new Object[] {year, position, state, year, position, state };
		return listAll(SQL, params, fields);
	}
	public List<Map<String, Object>> listAllVotesByParty(String state, int year, String position) throws SQLException {
		String SQL = " SELECT SIGLA_UF, "
				+ "		SIGLA_PARTIDO, "
				+ "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos "
				+ "	FROM votacao_partido " 
				+ "	WHERE ANO_ELEICAO=? "
				+ "	  AND DESCRICAO_CARGO = ? "
				+ "	  AND SIGLA_UF=? " 
				+ "	GROUP BY SIGLA_UF, NUMERO_PARTIDO";
		
		String[] fields = new String[] {
				"SIGLA_UF",
				"SIGLA_PARTIDO",
				"VOTOS",
		};
		Object[] params = new Object[] {year, position, state};
		return listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> listBestNForCoalition(String state, int year, String coalition, int n, String position) throws SQLException {
		String SQL = "select NOME_URNA_CANDIDATO, "
				+ "   SIGLA_PARTIDO,"
				+ "   SIGLA_UF,"
				+ "   sum(TOTAL_VOTOS) as votos "
				+ " from consolidated "
				+ " where sigla_uf = ? "
				+ "   and ano_eleicao = ? "
				+ "   and COMPOSICAO_LEGENDA = ? "
				+ "   and DESCRICAO_CARGO = ? "
				+ " group by sq_candidato "
				+ " order by sum(total_votos) desc "
				+ " limit ?";
		
		String[] fields = new String[] {
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"SIGLA_UF",
				"VOTOS",
		};
		Object[] params = new Object[] {state,  year, coalition, position, n };
		return listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> listBestNForParty(String state, int year, String party, int n, String position) throws SQLException {
		String SQL = "select NOME_URNA_CANDIDATO, "
				+ "   SIGLA_PARTIDO, "
				+ "   SIGLA_UF,"
				+ "   sum(TOTAL_VOTOS) as votos "
				+ " from consolidated "
				+ " where sigla_uf = ? "
				+ "   and ano_eleicao = ? "
				+ "   and SIGLA_PARTIDO = ? "
				+ "   and DESCRICAO_CARGO = ? "
				+ " group by sq_candidato "
				+ " order by sum(total_votos) desc "
				+ " limit ?";
		
		String[] fields = new String[] {
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"SIGLA_UF",
				"VOTOS",
		};
		Object[] params = new Object[] {state,  year, party, position, n };
		return listAll(SQL, params, fields);
	}
	
	public List<Map<String, Object>> listBestN(String state, int year, int n, String position) throws SQLException {
		String SQL = "select NOME_URNA_CANDIDATO, "
				+ "   SIGLA_PARTIDO, "
				+ "   SIGLA_UF,"
				+ "   sum(TOTAL_VOTOS) as votos "
				+ " from consolidated "
				+ " where sigla_uf = ? "
				+ "   and ano_eleicao = ? "
				+ "   and DESCRICAO_CARGO = ? "
				+ " group by sq_candidato "
				+ " order by sum(total_votos) desc "
				+ " limit ?";
		
		String[] fields = new String[] {
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"SIGLA_UF",
				"VOTOS",
		};
		Object[] params = new Object[] {state,  year, position, n };
		return listAll(SQL, params, fields);
	}

	public Integer getChairsForElection(String state, int year, String position) throws SQLException {
		String SQL = "select QTDE_VAGAS " +
				"from vagas  " +
				"where ANO_ELEICAO = ? " +
				"  and UPPER(DESCRICAO_CARGO) = ? " +  
				"  and SIGLA_UF = ?";
		String[] fields = new String[] {
				"QTDE_VAGAS",
		};
		Object[] params = new Object[] {year, position, state};
		List<Map<String, Object>> ret = this.listAll(SQL, params, fields);
		return (Integer) ret.get(0).get("QTDE_VAGAS");
	}

	public List<Map<String, Object>> getVotesByParty(String state, int year,
			String party, String position) throws SQLException {
		String SQL = "select SIGLA_PARTIDO, " +
			    " SIGLA_UF, " +
				" sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as VOTOS " +
			" from votacao_partido " +
			" where SIGLA_PARTIDO = ? " +
			"  and ANO_ELEICAO  = ? " +
			"  and SIGLA_UF = ? " +
			"  and DESCRICAO_CARGO = ? " +
			"GROUP BY SIGLA_UF, NUMERO_PARTIDO;";
		String[] fields = new String[] {
				"SIGLA_PARTIDO",
				"SIGLA_UF",
			"VOTOS",
		};
		Object[] params = new Object[] {party, year, state, position};
		return this.listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> getVotesByPartyNational(int year,
			String party, String position) throws SQLException {
		String SQL = "select SIGLA_PARTIDO, " +
			    " 'BR' as SIGLA_UF, " +
				" sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as VOTOS " +
			" from votacao_partido " +
			" where SIGLA_PARTIDO = ? " +
			"  and ANO_ELEICAO  = ? " +
			"  and DESCRICAO_CARGO = ? " +
			"GROUP BY NUMERO_PARTIDO;";
		String[] fields = new String[] {
				"SIGLA_PARTIDO",
				"SIGLA_UF",
			"VOTOS",
		};
		Object[] params = new Object[] {party, year, position};
		return this.listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> listAllStates(int year, String position) throws SQLException {
		String SQL = "select distinct SIGLA_UF " +
				"from vagas  " +
				"where ANO_ELEICAO = ? " +
				"  and UPPER(DESCRICAO_CARGO) = ? ";
		String[] fields = new String[] {
				"SIGLA_UF",
		};
		Object[] params = new Object[] {year, position };
		return this.listAll(SQL, params, fields);
	}
	
}
