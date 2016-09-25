<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<!--HEADER-->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>BIMM Results</title>

<!--LINK TO STYLESHEETS AND EXTERNAL JAVASCRIPT -->
<link rel="stylesheet" type="text/css" href="css/styles.css" />
<link rel="stylesheet" type="text/css" href="css/jquery.lightbox-0.5.css" media="screen" />

<!--FAVICON-->
<link rel="icon" type="image/x-icon" href="img/favicon.ico" /> 
<link rel="shortcut icon" type="image/x-icon" 
href="img/favicon.ico" />

</head>

<!--BODY-->
<body>

<div id="bimmResult">
		<h1>
			<br/>
			<img src="img/logo.png" width="25" height="25" alt="Stanford" id="Stanford" name="Stanford"/>
			Biomedical Image Query Results
		</h1>
		
		<div id="divForm">
		</div>
		<br>

        <!--MAIN RESULTS BOX-->
        <div class="imageContainer">
	        <div class="metaInfo">
				<a href="about.jsp">About</a> 
				<a href="index.jsp">Text Query</a>
				<a href="cbir">Image Query</a>
				 <table width=900px border=0 align=center summary=""> 
					<tr> 
						<td><div class="scrollBox"></div></td>
					</tr>
				</table> 
			</div>	

			<div class="imageQueryContainer">
			</div>
						
			<div class="imageResultsContainer">
			</div>
    </div> <!-- Closing imageContainer -->
</div>
<!--FOOTER AND JAVASCRIPT-->
<h2 id="footer"> </h2>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript" src="js/jquery.lightbox-0.5.js"></script>
<script type="text/javascript" src="js/bimm.js"></script>
<script type="text/javascript" src="js/util.js"></script>
<script type="text/javascript" src="js/resultsCBIR.js"></script>

<script type="text/javascript">
	//---Load the list of AIM files
	process_xml_listOfAIMfiles(<%= "\'"+request.getAttribute("xmlListOfAIMUID")+"\'" %>);
	
	//---Check if a aim query has been defined and CBIR results are available
	if(<%= "\'"+request.getAttribute("xmlResult")+"\'" %> != "null" && <%= "\'"+request.getAttribute("xmlResult")+"\'" %> != ""){
		process_xml_resultsCBIR(<%= "\'"+request.getAttribute("xmlResult")+"\'" %>);
	}else{
		$('.imageQueryContainer').html("<b>Please select an AIM file</b>");
	}
	
	//--- Select all links with lightbox class
	$(function() {
		$('a.lightbox').lightBox(); 
	});
</script>

</body>
</html>
