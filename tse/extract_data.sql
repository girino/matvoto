.header on
.mode tabs

-- select em todos 
SELECT * FROM diferenca_turnos;

-- encontra proporcao
SELECT (SELECT count(*) from diferenca_turnos) as TURN_OVER, 
	(SELECT count(*) from municipio_2_turnos) as TOTAL;
