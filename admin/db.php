<?php 

	function getList($gallery) {
		if ( file_exists('../config.php') ){include('../config.php');}
		/*
		$this->conn = mysql_connect($host, $username, $password);
		mysql_select_db($database,$this->conn);
		*/
		mysql_connect($host,$username,$password);
		@mysql_select_db($database) or die('Unable to select database');
		$sql = "SELECT * FROM kuvat where galleria = " . $gallery . " ORDER BY jarjestys";		
		//$recordSet = mysql_query($sql,$this->conn);
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
			$sql = "UPDATE kuvat SET jarjestys={$orderid} WHERE id=" . $catid;
			//$recordSet = mysql_query($sql,$this->conn);
			$recordSet = mysql_query($sql);
			$orderid++;
		}
	}
?>