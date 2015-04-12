<?php
function addTypes($arr) {
	$types = "";
	foreach ($arr as $a) {
		if (is_int($a)) {
			$types .= "i";
		} else if (is_float($a)) {
			$types .= "d";
		} else {
			$types .= "s";
		}
	} 
	return array_merge(array($types), $arr);
}

function refValues($arr){
	if (strnatcmp(phpversion(),'5.3') >= 0) //Reference is required for PHP 5.3+
	{
		$refs = array();
		foreach($arr as $key => $value)
			$refs[$key] = &$arr[$key];
		return $refs;
	}
	return $arr;
}
function iQuery($conn, $sql, $arrParams, $arrBindNames=false) {
	$result = new stdClass();
	$mysqli = $conn;
	if ($stmt = $mysqli->prepare($sql)) {
		$method = new ReflectionMethod('mysqli_stmt', 'bind_param');
		$method->invokeArgs($stmt, refValues($arrParams));
		$stmt->execute();
		$meta = $stmt->result_metadata();
		if (!$meta) {
			$result->affected_rows = $stmt->affected_rows;
			$result->insert_id = $stmt->insert_id;
		} else {
			$stmt->store_result();
			$params = array();
			$row = array();
			if ($arrBindNames) {
				for ($i=0,$j=count($arrBindNames); $i<$j; $i++) {
					$params[$i] = &$row[$arrBindNames[$i]];
				}
			} else {
				while ($field = $meta->fetch_field()) {
					$params[] = &$row[$field->name];
				}
			}
			$meta->close();
			$method = new ReflectionMethod('mysqli_stmt', 'bind_result');
			$method->invokeArgs($stmt, $params);
			$result->rows = array();
			while ($stmt->fetch()) {
				//$obj = new stdClass();
				$obj = array();
				foreach($row as $key => $val) {
					//$obj->{$key} = $val;
					$obj[$key] = $val;
				}
				$result->rows[] = $obj;
			}
			$stmt->free_result();
		}
		$stmt->close();
	} else {
		echo "Failed to run query: (" . $mysqli->errno . ") " . $mysqli->error;
	}
	return $result;
}

class DBAccess { 
	const DEFAULT_HOST = "127.0.0.1";
	const DEFAULT_DB = "tse";
	const DEFAULT_USER = "root";
	const DEFAULT_PWD = "";

	private $conn;
	private $dbname;
	private $dbhost;
	private $dbuser;
	private $dbpwd;

	public  function __construct($dbhost=DBAccess::DEFAULT_HOST, $dbname=DBAccess::DEFAULT_DB, $dbuser=DBAccess::DEFAULT_USER,
			$dbpwd=DBAccess::DEFAULT_PWD) {
		$this->dbhost = $dbhost;
		$this->dbname = $dbname;
		$this->dbuser = $dbuser;
		$this->dbpwd = $dbpwd;
		
		// Create connection
		$this->conn = new mysqli($dbhost, $dbuser, $dbpwd, $dbname);
		
		// Check connection
		if ($this->conn->connect_error) {
			die("Connection failed: " . $this->conn->connect_error);
		}
	}
	
	public function mbind_param_do() {
		$params = array_merge($this->mbind_types, $this->mbind_params);
		return call_user_func_array(array($this, 'bind_param'), $this->makeValuesReferenced($params));
	}
	
	private function makeValuesReferenced($arr){
		$refs = array();
		foreach($arr as $key => $value)
			$refs[$key] = &$arr[$key];
		return $refs;
	
	}

	public function listAll($sql, $params, $fields) {
		$res = iQuery($this->conn, $sql, addTypes($params));
		return $res->rows;
	}
	
	public function listAllVotesByCoalition($state, $year) {
		$SQL = "	SELECT SIGLA_UF, "
				. "		TIPO_LEGENDA, "
				. "		NOME_COLIGACAO, "
				. "		COMPOSICAO_LEGENDA,"
				. "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as VOTOS "
				. "	FROM votacao_partido "
				. "	WHERE ANO_ELEICAO=? "
				. "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				. "	  AND SIGLA_UF=? "
				. "	  AND TIPO_LEGENDA='C'"
				. "	GROUP BY SIGLA_UF, NOME_COLIGACAO"
				. " UNION ALL"
				. "	SELECT SIGLA_UF, "
				. "		TIPO_LEGENDA, "
				. "		SIGLA_PARTIDO, "
				. "		COMPOSICAO_LEGENDA,"
				. "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as VOTOS "
				. "	FROM votacao_partido " 
				. "	WHERE ANO_ELEICAO=? "
				. "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				. "	  AND SIGLA_UF=? " 
				. "	  AND TIPO_LEGENDA='P'"
				. "	GROUP BY SIGLA_UF, NUMERO_PARTIDO";
		
		$fields = array(
				"SIGLA_UF",
				"TIPO_LEGENDA",
				"NOME_COLIGACAO",
				"COMPOSICAO_LEGENDA",
				"VOTOS"
		);
		$params = array($year,  $state, $year, $state);
		return $this->listAll($SQL, $params, $fields);
	}
	public function listAllVotesByParty($state, $year) {
		$SQL = " SELECT SIGLA_UF, "
				. "		SIGLA_PARTIDO, "
				. "		sum(QTDE_VOTOS_NOMINAIS)+SUM(QTDE_VOTOS_LEGENDA) as VOTOS "
				. "	FROM votacao_partido " 
				. "	WHERE ANO_ELEICAO=? "
				. "	  AND DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				. "	  AND SIGLA_UF=? " 
				. "	GROUP BY SIGLA_UF, NUMERO_PARTIDO";
		
		$fields = array(
				"SIGLA_UF",
				"SIGLA_PARTIDO",
				"VOTOS",
		);
		$params = array($year,  $state);
		return $this->listAll($SQL, $params, $fields);
	}

	public function listBestNForCoalition($state, $year, $coalition, $n) {
		$SQL = "select NOME_URNA_CANDIDATO, "
				. "   SIGLA_PARTIDO,"
				. "   sum(TOTAL_VOTOS) as VOTOS "
				. " from consolidated "
				. " where sigla_uf = ? "
				. "   and ano_eleicao = ? "
				. "   and COMPOSICAO_LEGENDA = ? "
				. "   and DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				. " group by sq_candidato "
				. " order by sum(total_votos) desc "
				. " limit ?";
		
		$fields = array(
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"VOTOS",
		);
		$params = array($state,  $year, $coalition, $n);
		return $this->listAll($SQL, $params, $fields);
	}

	public function listBestNForParty($state, $year, $party, $n) {
		$SQL = "select NOME_URNA_CANDIDATO, "
				. "   SIGLA_PARTIDO,"
				. "   sum(TOTAL_VOTOS) as VOTOS "
				. " from consolidated "
				. " where sigla_uf = ? "
				. "   and ano_eleicao = ? "
				. "   and SIGLA_PARTIDO = ? "
				. "   and DESCRICAO_CARGO = 'DEPUTADO FEDERAL' "
				. " group by sq_candidato "
				. " order by sum(total_votos) desc "
				. " limit ?";

		$fields = array(
				"NOME_URNA_CANDIDATO",
				"SIGLA_PARTIDO",
				"VOTOS",
		);
		$params = array($state, $year, $party, $n);
		return $this->listAll($SQL, $params, $fields);
	}	
}

require_once 'ApportionmentMethods.php';
		
$state = "SP";
$year = 2014;
		
$a = new DBAccess();
$ret = $a->listAllVotesByCoalition($state, $year);
(new DHontMethod())->calculateChairs($ret, "VOTOS", "CHAIRS", 70);
$elected = array();
foreach ($ret as $m) {
	$chairs = $m["CHAIRS"];
	$coalition = $m["COMPOSICAO_LEGENDA"];
	$elected = array_merge($elected, $a->listBestNForCoalition($state, $year, $coalition, $chairs));
	//print_r($a->listBestNForCoalition($state, $year, $coalition, $chairs));
}
uasort($elected, function($a, $b) {
	if ($a['VOTOS'] == $b['VOTOS']) return 0;
	return ($a['VOTOS'] > $b['VOTOS'])?1:-1;
});
foreach ($elected as $map) {
	print $map['NOME_URNA_CANDIDATO']. " (" . $map['SIGLA_PARTIDO'] . ") => " . $map['VOTOS'] . "\n";
}

?>
