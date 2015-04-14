<?php
interface ApportionmentMethod {
	public function calculateChairs(&$dbResults,
			$key, $outKey, $totalChairs);
	
}

interface ElectionRule {
	public function listElectedOfficials($dbAccess, $state, $year, $position='DEPUTADO FEDERAL');
	public function listElectedParties($dbAccess, $state, $year, $position='DEPUTADO FEDERAL');
}

class ProportionalDHont implements ElectionRule {
	
	private $method;
	private $allowsCoalition;
	
	public  function __construct($allowsCoalition = true) {
		$this->method = new DHontMethod();
		$this->allowsCoalition = $allowsCoalition;
	}

	public function listElectedOfficials($dbAccess, $state, $year, $position='DEPUTADO FEDERAL') {
		if ($this->allowsCoalition) {
			$ret = $dbAccess->listAllVotesByCoalition($state, $year, $position);
			$groupingKey = "COMPOSICAO_LEGENDA";
			$listBestN = 'listBestNForCoalition';
		} else {
			$ret = $dbAccess->listAllVotesByParty($state, $year, $position);
			$groupingKey = "SIGLA_PARTIDO";
			$listBestN = 'listBestNForParty';
		}
		$chairs = $dbAccess->getChairsForElection($state, $year, $position);
		$this->method->calculateChairs($ret, "VOTOS", "CHAIRS", 70);
		
		$elected = array();
		foreach ($ret as $m) {
			$chairs = $m["CHAIRS"];
			
			$coalition = $m[$groupingKey];
			$elected = array_merge($elected, $dbAccess->$listBestN($state, $year, $coalition, $chairs, $position));
		}
		
		uasort($elected, function($a, $b) {
			if ($a['VOTOS'] == $b['VOTOS']) return 0;
			return ($a['VOTOS'] > $b['VOTOS'])?1:-1;
		});
		return $elected;
	}
	
	public function listElectedParties($dbAccess, $state, $year, $position='DEPUTADO FEDERAL') {
		
	}
}
	
class DHontMethod implements ApportionmentMethod {
	
	public function calculateChairs(&$dbResults,
			$key, $outKey, $totalChairs) {
		$votes = array();
		$chairs = array();
		foreach ($dbResults as $m) {
			$v = $m[$key];
			array_push($votes, $v);
			array_push($chairs, 0);
		}

		for ($chair = 0; $chair < $totalChairs; $chair++) {
			$max = -1.0;
			$maxpos = -1;
			for ($i = 0; $i < count($votes); $i++) {
				$v = $votes[$i];
				$c = $chairs[$i];
				$mean = (1.0*$v)/(1.0*$c+1.0);
				if ($mean > $max) {
					$max = $mean;
					$maxpos = $i;
				}
			}
			$chairs[$maxpos]++;
		}
		for ($i = 0; $i < count($votes); $i++) {
			$dbResults[$i][$outKey] = $chairs[$i];
		}

		return $chairs;
	}


}