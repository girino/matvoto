<!DOCTYPE unspecified PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">

<?php
require_once 'classes/DBAccess.php';
require_once 'classes/ApportionmentMethods.php';

$year = $_GET['YEAR'];
$state = $_GET['UF'];

if (!$year) $year = 2010;
if (!$state) $state = 'DF';

$dbaccess = new DBAccess();
$apportionment = new ProportionalDHont(false);


$elected = $apportionment->listElectedOfficials($dbaccess, $state, $year);

foreach ($elected as $map) {
	print $map['NOME_URNA_CANDIDATO']. " (" . $map['SIGLA_PARTIDO'] . ") => " . $map['VOTOS'] . "<br \>";
}

$elected = $apportionment->listElectedParties($dbaccess, $state, $year);

foreach ($elected as $map) {
	print $map['SIGLA_PARTIDO'] . " => " . $map['CHAIRS'] . " ( " . $map['VOTOS'] .")<br \>";
}

