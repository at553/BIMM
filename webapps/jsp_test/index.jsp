<html>
<title>Example Web Application</title></head>
<body>
<p>This is a static document with a form in it. And I have edited it. And the time is <%= new java.util.Date() %></p>

<form method="POST" action="servlet"/>
<input name="field" type="text" />
<input type="submit" value="Submit" />
</form>
</body>
</html>
