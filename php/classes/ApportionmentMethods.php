<?php
interface ApportionmentMethod {

	public function calculateChairs(&$dbResults,
			$key, $outKey, $totalChairs);
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