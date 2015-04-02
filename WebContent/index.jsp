<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>PDF Uploader</title>
</head>
<body>
<form action="submitform" method="post" enctype="multipart/form-data">
Name : <input type="text" name="name" id="name"><br/>
Email : <input type="text" name="email" id="email"><br/>
PDF File : <input type="file" name="pdffile" id="pdffile"><br/>
<input type="Submit" value="Submit">
<br/>
<br/>
<a href="submitform">View Details</a>
</form>
</body>
</html>