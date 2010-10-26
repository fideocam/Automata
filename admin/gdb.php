<?php 
	function getList() {
		if ( file_exists('../config.php') ){
			include('../config.php');
		}
		mysql_connect($host,$username,$password);
		@mysql_select_db($database) or die('Unable to select database');
		//$sql = "SELECT kuvat.name as image, min(kuvat.jarjestys), galleriat.* FROM kuvat, galleriat where kuvat.galleria = galleriat.id having min(kuvat.jarjestys) order by jarjestys";
		//$sql = "SELECT kuvat.name as image, min(kuvat.jarjestys), galleriat.* FROM kuvat, galleriat where kuvat.galleria = galleriat.id group by kuvat.galleria order by jarjestys";
		//SELECT kuvat.name as image, galleriat.* FROM galleriat, kuvat where kuvat.galleria = galleriat.id and having min(kuvat.jarjestys)
//TODO: ylemmät palauttaa jostain syystä vain ne joissa on jo yksi kuva	
		$sql = "select galleriat.* from galleriat order by jarjestys";
		$recordSet = mysql_query($sql);
		$results = array();
		while($row = mysql_fetch_assoc($recordSet)) {
			$results[] = $row;
		}
		return $results;
	}
	function updateList($orderArray) {
		if ( file_exists('../config.php') ){include('../config.php');}
		$orderid = 1;
		mysql_connect($host,$username,$password);
		@mysql_select_db($database) or die('Unable to select database');
		foreach($orderArray as $catid) {
			$catid = (int) $catid;		
			$sql = "UPDATE galleriat SET jarjestys={$orderid} WHERE id=" . $catid;
			$recordSet = mysql_query($sql);
			$orderid++;
		}
	}
?>