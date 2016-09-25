<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<!--HEADER-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>About BIMM</title>
<!--LINK TO STYLESHEETS AND EXTERNAL JAVASCRIPT -->
<link rel="stylesheet" type="text/css" href="styles.css" />

<!--FAVICON-->
<link rel="icon" type="image/x-icon" href="img/favicon.ico" /> 
<link rel="shortcut icon" type="image/x-icon" 
href="img/favicon.ico" /> 

</head>

<!--BODY-->
<body>

<div id="bimmResult">
	<h1><br/><img src="img/logo.png" width="25" height="25" alt="Stanford" id="Stanford" name="Stanford"/>About BIMM</h1>
	    <!--MAIN Text Box-->
        <div class="imageContainer">

	<div class="metaInfo">		
		<a href="index.jsp">Restart</a><br/>     
	</div>
        	<div class="aboutContainer">
		   		<h2>Image Query for Diagnosis and Discovery</h2>
				<p>Authors: Francisco Gimenez, Katie Planey, Sushie Shankar, Vanessa Sochat, Linda Szabo</p>
		        
		        <h3>The Problem: Information in medical images is not accessible</h3>
				<p>Automated, digital acquisition of medical images across various imaging modalities offers a non-invasive method for the diagnosis and classification of disease.  Historically, the usage of a diagnostic image was limited to the single point in time when it was acquired to answer one specific question.  For example, a medical expert might read an image, make observations to support a diagnosis, and archive the image for future reference.  Missing from this procedure was a way to save both the image and the annotation for future query.</p>
				
				<h3>The Start of a Solution: Annotated Image Markup</h3>
				<p>The advent of Annotated Image Markup (AIM) has allowed experts to save annotations with medical images.  These annotated images are becoming increasingly available.  The next step, then, is to allow for the large-scale data mining of these image findings.</p>
				
				<h3>Why is this important?</h3>
				<p>What if a medical expert is having trouble diagnosing a tumor, and wants to look at tumors with similar shape to see how they were diagnosed?  What if a child has a brain scan to identify an attention network, and we want to know if his attention network looks more like a child with ADHD, or a healthy child?  Answering these sorts of questions requires a system of this sort that ties semantic knowledge to images, and allows for query of both.  This interface is a prototype of such a system.  The user can search for terms that describe a liver lesion using a controlled vocabulary, and return images for which that term applies with associated metadata.</p>       
	    	</div>
	    </div> <!-- Closing imageContainer -->
</div>

<!--FOOTER AND JAVASCRIPT-->
<h2 id="footer">
</h2>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="js/bimm.js"></script>

</body>
</html>
