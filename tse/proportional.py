#!/usr/bin/python

import MySQLdb

db = MySQLdb.connect(host="localhost",
                     user="root",
                      db="tse")

sql_partidos = """
	SELECT SIGLA_UF, 
		TIPO_LEGENDA, 
		NOME_COLIGACAO, 
		COMPOSICAO_LEGENDA,
		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos 
	FROM votacao_partido 
	WHERE ANO_ELEICAO=2014 
	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' 
	  AND SIGLA_UF='DF' 
	  AND TIPO_LEGENDA='C'
	GROUP BY SIGLA_UF, NOME_COLIGACAO
UNION ALL
	SELECT SIGLA_UF, 
		TIPO_LEGENDA, 
		SIGLA_PARTIDO, 
		COMPOSICAO_LEGENDA,
		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as votos 
	FROM votacao_partido 
	WHERE ANO_ELEICAO=2014 
	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' 
	  AND SIGLA_UF='DF' 
	  AND TIPO_LEGENDA='P'
	GROUP BY SIGLA_UF, NUMERO_PARTIDO
"""

cur = db.cursor() 
cur.execute(sql_partidos)
for row in cur.fetchall() :
    print row
