<?php 
$id = (int)$_REQUEST['id'];

delete($id);

function delete($id){
	
	if (file_exists('../config.php') ){include('../config.php');}
	mysql_connect('localhost',$username,$password);

	//delete file 
	//distinct, unique tjs.
	
	@mysql_select_db($database) or die('Unable to select database');
	$query2= "select name from kuvat where id = " . $id; 
	$result2=mysql_query($query2);
	$num=@mysql_numrows($result2);
	$i=0;
	if($num > 0){
	while ($i < $num) {
		$nimi = mysql_result($result2,$i,"name");
		$i++;
	}
	$dir = "../images/";
	
	//Check if duplicate image in another gallery
	@mysql_select_db($database) or die('Unable to select database');
	$query= "SELECT count(*) AS rec FROM kuvat k where name = '" . $nimi . "'"; 
	$result3=mysql_query($query);
	$num2=mysql_numrows($result3); 
	$i2=0;
	while ($i2 < $num2) {
		$rec = mysql_result($result3,$i2,"rec");
		if($rec < 2){
			unlink($dir . $nimi);
			echo "Image #" . $id . " removed. "; 
		}else{
			echo "Image " . $id . " noticed in another gallery and file not removed permanently. ";
		}
		$i2++;
	}
	}
	//remove from db

	@mysql_select_db($database) or die('Unable to select database');
	$query= "delete from kuvat where id = " . $id; 
	$result=mysql_query($query);
	mysql_close();
	
	//gallery.php?gallery=" . $gallery ."\">
	echo "<a href=\"index.php\">Takaisin etusivulle</a>";
	
	}
?>