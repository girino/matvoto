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
	
	public List<Map<String, Object>> listAllVotesByCoalition(String state, int year) throws SQLException {
		String SQL = "	SELECT SIGLA_UF, "
				+ "		TIPO_LEGENDA, "
				+ "		NOME_COLIGACAO, "
				+ "		COMPOSICAO_LEGENDA,"
				+ "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos "
				+ "	FROM votacao_partido "
				+ "	WHERE ANO_ELEICAO=? "
				+ "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
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
				+ "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
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
		Object[] params = new Object[] {year,  state, year, state };
		return listAll(SQL, params, fields);
	}
	public List<Map<String, Object>> listAllVotesByParty(String state, int year) throws SQLException {
		String SQL = " SELECT SIGLA_UF, "
				+ "		SIGLA_PARTIDO, "
				+ "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos "
				+ "	FROM votacao_partido " 
				+ "	WHERE ANO_ELEICAO=? "
				+ "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				+ "	  AND SIGLA_UF=? " 
				+ "	GROUP BY SIGLA_UF, NUMERO_PARTIDO";
		
		String[] fields = new String[] {
				"SIGLA_UF",
				"SIGLA_PARTIDO",
				"VOTOS",
		};
		Object[] params = new Object[] {year,  state };
		return listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> listBestNForCoalition(String state, int year, String coalition, int n) throws SQLException {
		String SQL = "select NOME_URNA_CANDIDATO, "
				+ "   SIGLA_PARTIDO,"
				+ "   sum(TOTAL_VOTOS) as votos "
				+ " from consolidated "
				+ " where sigla_uf = ? "
				+ "   and ano_eleicao = ? "
				+ "   and COMPOSICAO_LEGENDA = ? "
				+ "   and DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				+ " group by sq_candidato "
				+ " order by sum(total_votos) desc "
				+ " limit ?";
		
		String[] fields = new String[] {
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"VOTOS",
		};
		Object[] params = new Object[] {state,  year, coalition, n };
		return listAll(SQL, params, fields);
	}

	public List<Map<String, Object>> listBestNForParty(String state, int year, String party, int n) throws SQLException {
		String SQL = "select NOME_URNA_CANDIDATO, "
				+ "   SIGLA_PARTIDO,"
				+ "   sum(TOTAL_VOTOS) as votos "
				+ " from consolidated "
				+ " where sigla_uf = ? "
				+ "   and ano_eleicao = ? "
				+ "   and SIGLA_PARTIDO = ? "
				+ "   and DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				+ " group by sq_candidato "
				+ " order by sum(total_votos) desc "
				+ " limit ?";
		
		String[] fields = new String[] {
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"VOTOS",
		};
		Object[] params = new Object[] {state,  year, party, n };
		return listAll(SQL, params, fields);
	}
	
}
