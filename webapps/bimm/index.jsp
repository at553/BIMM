
<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>BIMM</title>

    <!-- Bootstrap Core CSS -->
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <link href="css/bimm.css" rel="stylesheet">

    <!-- Custom CSS -->
    <style>
    body {
        padding-top: 70px;
        /* Required padding for .navbar-fixed-top. Remove if using .navbar-static-top. Change if height of navigation changes. */
    }
    </style>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
        <div class="container">
            <!-- Brand and toggle get grouped for better mobile display -->
            <div class="navbar-header">
                <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="navbar-brand">BIMM Homepage</a>
            </div>
            <!-- Collect the nav links, forms, and other content for toggling -->
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                </ul>
            </div>
            <!-- /.navbar-collapse -->
        </div>
        <!-- /.container -->
    </nav>

    <!-- Page Content -->
    <div class="container">
        <div class="row">
            <div class="col-lg-12 text-center">
                <h1>Biomedical Image Metadata Markup</h1>
                <p class="lead">Enter Unique Identifier Below To Search for Similar Images</p>
            </div>
        </div> 
	</div>
    <!-- Page Content -->
    <div class="container-fluid">  
		<div class="row">
         	<!-- <div class="col-md-4 jumbotron">
		        <form action="/BIMM/bimmservlet" method="get" id="bimmform" role="form" >
		        	<div class="form-group">
		  				<label for="usr">Image UID:</label>
		  				<input type="text" class="form-control" name="aimInputUID" id="usr">
		  			</div>
					<input type="submit" value="Submit"/>
				</form>
			</div>-->
			<div class="col-md-12" >
			<h1 class="display-2">Selected Image</h1>
				<div class="container" >
				<div class="row">
				        <div class="col-md-6" id="selecteduid">
				        
				        
				         
				        </div>
				   </div>
				</div>
				<hr>
				<h1 class="display-3">Matching Images</h1>
				<div><button type="button" class="btn btn-secondary" disabled id="register" title="Registers relevance selections on click">Register</button></div>
				<div class="container" id ="thumbnail">
					
				  
				</div>
			</div>
		</div>

        <!-- /.row -->
    <!-- /.container -->

    <!-- jQuery Version 1.11.1 -->
    <script src="js/jquery-3.1.0.min.js"></script>

    <!-- Bootstrap Core JavaScript -->
    <script src="js/bootstrap.min.js"></script>
    <script src="js/bimm.js"></script>

</body>

</html>
