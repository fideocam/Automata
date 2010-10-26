<html>
<head>
<title>THFIN tool</title>
<style type="text/css">
@import 'css/index.css';
</style>
</head>
<body>
<?php

	include_once("uploader.php");
	
	function open_image ($file) {
	
        //JPEG:
        $im = @imagecreatefromjpeg($file);
        if ($im !== false) { return $im; }

        // GIF:
        $im = @imagecreatefromgif($file);
        if ($im !== false) { return $im; }

        // PNG:
        $im = @imagecreatefrompng($file);
        if ($im !== false) { return $im; }

        return false;

}
	
	$gallery = (int)$_REQUEST['gallery'];
   if (@$_POST['Submit'] && $_FILES['imagefile']['name']){ 
	//lataa tiedot
		$name = $_FILES['imagefile']['name'];
		$nimi = $_REQUEST['nimi'];
		$radius = $_REQUEST['radius'];
		$hinta = $_REQUEST['hinta'];
	
		//echo $name . $nimi . $radius . $hinta;

	//tarkista tiedostotyyppi
		$file_type = $_FILES['imagefile']['type'];
		if ( $file_type == "image/jpg" || $file_type == "image/pjpeg" || $file_type == "image/jpeg") {


		//lataa kuva
			do_upload('imagefile');

		//selvitä onko pysty
			$pysty = 1;


			$Array = getimagesize("../images/".$name);
		
			//käännä järjestys jos ei futaa
			if($Array[1] >= $Array[0]){
				$pysty = 0;	
			}

//tähän lisätty

// skaalaa 800*533 kokoon	
		$image = open_image('../images/'.$name);
		if ($image === false) { 
			echo ('Unable to open image'); 

		}else{
			
			$width = imagesx($image);
			$height = imagesy($image);

	

//pysty on nolla, vaaka on yksi
			if($height >= $width){
				$pysty = 0;
				//echo "pysty";
				$new_width = 533;
				$new_height = 800;	
			}else{
				//echo "vaaka";
				$new_width = 800;
				$new_height = 533;
			}			
			
			if($new_width != $width && $new_height != $height){

			//joku mättää - resize tekee kaikista vaakoja!!

				$image_resized = imagecreatetruecolor($new_width, $new_height);
				imagecopyresampled($image_resized, $image, 0, 0, 0, 0, $new_width, $new_height, $width, $height);
        			imagejpeg($image_resized, "../images/".$name, 100);
				imagedestroy($image_resized); 
			}
		}

	//lisää kantaan
		//add2db($name, $nimi, $radius, $hinta, $pysty);
		add2db2($gallery, $name, $nimi, $radius, $hinta, $pysty);
		
		echo $name . " added. Import next picture's info or go <a href=\"gallery.php?gallery=" . $gallery ."\">back to gallery admin page</a>.";
		}
     	}else{
		echo "Add image info or go <a href=\"index.php\">back to admin page</a>.";
	}
		
			
?>

<form action="upload.php" name="upload" id="upload" ENCTYPE="multipart/form-data" method="post">
	File: <input type="file" name="imagefile"><br/>
	Name <input type="text" rows="1" name="nimi"><br/>
	Price <input type="text" name="hinta" value="0"><br/>
	Diameter <input type="text" name="radius" value="0"><br/>
	<input type="hidden" name="gallery" value="<?php echo $gallery?>">
  	<input type="submit" name="Submit" value="Upload">
</form>
</body>
</html>