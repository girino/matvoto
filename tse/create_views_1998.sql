create view prefeito_zona as 
SELECT *
FROM candidato
WHERE CODIGO_CARGO=9
ORDER BY NOME_MUNICIPIO;

create view municipio_2_turnos as select CODIGO_MUNICIPIO, NOME_MUNICIPIO, SUM(QTD_APTOS), SUM(QTD_APTOS_TOT) from detalhe_votacao 
where CODIGO_CARGO=9
group by CODIGO_MUNICIPIO, NOME_MUNICIPIO having SUM(QTD_APTOS)> 200000 order by SUM(QTD_APTOS);

create view prefeito_municipio as
SELECT ANO_ELEICAO, NUM_TURNO, SIGLA_UF, SIGLA_UE, CODIGO_MUNICIPIO, NOME_MUNICIPIO, NUMERO_CAND, SQ_CANDIDATO, NOME_CANDIDATO, NOME_URNA_CANDIDATO, DESC_SIT_CAND_TOT, NUMERO_PARTIDO, SIGLA_PARTIDO, SUM(TOTAL_VOTOS) as TOTAL_VOTOS
FROM prefeito_zona pz
GROUP BY ANO_ELEICAO, NUM_TURNO, SIGLA_UF, SIGLA_UE, CODIGO_MUNICIPIO, NOME_MUNICIPIO, NUMERO_CAND, SQ_CANDIDATO, NOME_CANDIDATO, NOME_URNA_CANDIDATO, DESC_SIT_CAND_TOT, NUMERO_PARTIDO, SIGLA_PARTIDO;

create view vencedores_turno_1 as
SELECT * from prefeito_municipio pm1
WHERE NUM_TURNO=1
  AND EXISTS (SELECT * FROM municipio_2_turnos WHERE pm1.CODIGO_MUNICIPIO = municipio_2_turnos.CODIGO_MUNICIPIO)
  AND TOTAL_VOTOS = (select MAX(TOTAL_VOTOS) 
			from prefeito_municipio pm2 
			WHERE pm2.CODIGO_MUNICIPIO = pm1.CODIGO_MUNICIPIO
			  AND pm2.NUM_TURNO=1
		    );

create view vencedores_turno_2 as
SELECT * from prefeito_municipio pm1
WHERE NUM_TURNO=2
  AND TOTAL_VOTOS = (select MAX(TOTAL_VOTOS) 
                        from prefeito_municipio pm2 
                        WHERE pm2.CODIGO_MUNICIPIO = pm1.CODIGO_MUNICIPIO
                          AND pm2.NUM_TURNO=2
                    );

create view diferenca_turnos as
SELECT t1.CODIGO_MUNICIPIO, t1.NOME_MUNICIPIO, 
	t1.SIGLA_PARTIDO, t1.NOME_URNA_CANDIDATO, 
	t1.TOTAL_VOTOS, t2.SIGLA_PARTIDO, 
	t2.NOME_URNA_CANDIDATO, t2.TOTAL_VOTOS 
FROM vencedores_turno_1 t1 
	JOIN vencedores_turno_2 t2 ON t1.CODIGO_MUNICIPIO = t2.CODIGO_MUNICIPIO 
WHERE t1.NUMERO_CAND != t2.NUMERO_CAND;

-- encontra proporcao
--.header on
--.mode tabs
--SELECT (SELECT count(*) from diferenca_turnos) as TURN_OVER, 
--	(SELECT count(*) from municipio_2_turnos) as TOTAL;
