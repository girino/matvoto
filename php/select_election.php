<!DOCTYPE unspecified PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<?php 
require_once 'classes/DBAccess.php';

$dbaccess = new DBAccess();
$years = $dbaccess->listYears();
$states = $dbaccess->listUF();

function makeLink($year, $state) {
	return '<a href="results.php?YEAR='.$year.'&UF='.$state.'">'.$year.'</a>';
}

?>

<table>
  <tr>
    <th>UF\Ano</th>
  <?php
	foreach ($years as $year) {
?>
    <th><?php echo $year['ANO_ELEICAO']; ?></th>
<?php 
	}
  ?>
  </tr>
<?php 
	foreach ($states as $state) {

?>
  <tr>
    <th><?php echo $state['SIGLA_UF']; ?></th>
  <?php
		foreach ($years as $year) { 
?>
    <td><?php echo makeLink($year['ANO_ELEICAO'], $state['SIGLA_UF']); ?></td>
<?php
		} 
?>
  </tr>
<?php 
	}
?>
</table>

<?php
