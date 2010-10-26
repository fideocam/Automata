<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title>Gallery</title>
<script type="text/javascript" src="swfobject.js"></script>
<style type="text/css">	
	/* hide from ie on mac \*/
	html {
		height: 100%;
		overflow: hidden;
	}
	
	#flashcontent {
		height: 100%;
	}
	/* end hide */

	body {
		height: 100%;
		margin: 0;
		padding: 0;
		background-color: #181818;
		color:#ffffff;
	}
</style>
</head>
<body>
	<div id="flashcontent">AutoViewer requires JavaScript and the Flash Player. <a href="http://www.macromedia.com/go/getflashplayer/">Get Flash here.</a> </div>	
	<script type="text/javascript">
		var fo = new FlashObject("autoviewer.swf", "autoviewer", "100%", "100%", "8", "#181818");		
		//fo.addParam("scale", "noscale");
		//fo.addParam("xmlURL","gallerydata.php?id=<?php print (int)$_REQUEST['id'];?>");
		fo.addVariable("xmlURL","gallerydata.php?id=<?php print (int)$_REQUEST['id'];?>");
		fo.write("flashcontent");	
	</script>	
</body>
</html>