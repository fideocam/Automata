<?php

function add2db2($gallery, $fname, $nimi, $lapimitta, $hinta, $pysty){

	if ( file_exists('../config.php') ){include('../config.php');}
	mysql_connect($host,$username,$password);
	@mysql_select_db($database) or die( "Unable to select database on update");
	$query = "select max(jarjestys)+1 as top from kuvat where galleria=" . $gallery;
	$result = mysql_query($query);	
	$num=@mysql_numrows($result);
	if($num > 0){
		$top = (int)mysql_result($result,0,"top");
	}else{
		$top = 1;
	}
	$query2 = "INSERT INTO kuvat (galleria, name, nimi, lapimitta, hinta, pysty, jarjestys) VALUES (" . $gallery . ",'" . $fname . "','" . $nimi . "'," . $lapimitta . "," . $hinta . "," . $pysty . ", " . $top . ")";
	mysql_query($query2);	
	mysql_close();	
}

function add2db($fname, $nimi, $lapimitta, $hinta, $pysty){
	if ( file_exists('../config.php') ){include('../config.php');}	
	mysql_connect($host,$username,$password);
	@mysql_select_db($database) or die( "Unable to select database on update");

	$query = "select max(jarjestys)+1 as top from kuvat";
	$result = mysql_query($query);

	
	$top = (int)mysql_result($result,0,"top");

	$query2 = "INSERT INTO kuvat (name, nimi, lapimitta, hinta, pysty, jarjestys) VALUES ('" . $fname . "','" . $nimi . "'," . $lapimitta . "," . $hinta . "," . $pysty . ", " . $top . ")";

	mysql_query($query2);	
	mysql_close();	
}

function do_upload($fname) {
  	$temp_name = $_FILES[$fname]['tmp_name'];
     	$file_type = $_FILES[$fname]['type'];
     	$file_size = $_FILES[$fname]['size'];
     	$result    = $_FILES[$fname]['error'];
	$file_name = $_FILES[$fname]['name'];

	$image_upload_dir = "../images/";

     	//File Name Check
    	if ( $temp_name =="") {
         	$message = "no file";
         	return $message;
    	}
    	//File Size Check - under 3 megaa
    	else if ( $file_size > 3000000) {
       		$message = "file too big";
        	return $message;
    	}else if ( $file_type == "image/jpg" || $file_type == "image/pjpeg" || $file_type == "image/jpeg") {
     		$file_path = $image_upload_dir.$file_name;
	    	$message  =  move_uploaded_file($temp_name, $file_path);
    	}else{
		$message = "bad file";
        	return $message;
    	}
    	return $message;
}

?>

