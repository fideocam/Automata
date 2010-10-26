<?php 
$id = (int)$_REQUEST['gallery'];
delete($id);
function delete($id){
	
	if ( file_exists('../config.php') ){include('../config.php');}
	mysql_connect('localhost',$username,$password);	
	//delete all the files in the gallery 
	@mysql_select_db($database) or die('Unable to select database');
	//todo: faster way to check for mass doubles before deleting using distinct, unique or join in the original
	$query2= "select name from kuvat where galleria = " . $id; 
	$result2=mysql_query($query2);
	$num=mysql_numrows($result2);
	$i=0;
	$dir = "../images/";
	while ($i < $num) {
		//delete image file
		$nimi = mysql_result($result2,$i,"name");
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
			echo "Image '" . $nimi . "' removed. </br>"; 
		}else{
			echo "Image '" . $nimi . "' noticed in another gallery and file not removed permanently. </br>";
		}
		$i2++;
		}
		//remove image from db
		@mysql_select_db($database) or die('Unable to select database');
		$query= "delete from kuvat where name = '" . $nimi . "' and galleria=" . $id; 
		$result=mysql_query($query);
		//echo "Image " .  $nimi . " removed. ";
		$i++;
	}
	@mysql_select_db($database) or die('Unable to select database');
	$query= "delete from galleriat where id = " . $id; 
	$result=mysql_query($query);
	mysql_close();
	echo "gallery #" . $id . " removed. Notice that the deleted image files may have been used in other galleries also. <a href=\"index.php\">Back to index page</a>";
}
?>