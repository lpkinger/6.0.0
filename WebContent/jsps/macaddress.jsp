<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

 <a href="#" onclick="macs.getMacAddress();"> Get"first"MAC Address</a>
<br/>
<br/>
 <a href="#" onclick="macs.getMacAddressesJSON();"> Get all MAC Addresses</a>

 <!--[if !IE]> Firefox and others will use outer object -->
 <embed type="application/x-java-applet"
name="macaddressapplet"
width="0"
height="0"
code="MacAddressApplet"
archive="macaddressapplet.jar"
pluginspage="http://java.sun.com/javase/downloads/index.jsp"
 style="position:absolute; top:-1000px; left:-1000px;">
<noembed>
<!--<![endif]-->
<!---->
 <object classid="clsid:CAFEEFAC-0016-0000-FFFF-ABCDEFFEDCBA"
type="application/x-java-applet"
name="macaddressapplet"
 style="position:absolute; top:-1000px; left:-1000px;"
>
 <param name="code"value="MacAddressApplet">
 <param name="archive"value="macaddressapplet.jar">
 <param name="mayscript"value="true">
 <param name="scriptable"value="true">
 <param name="width"value="0">
 <param name="height"value="0">
</object>
 <!--[if !IE]> Firefox and others will use outer object -->
</noembed>
</embed>
<!--<![endif]-->

 <script type="text/javascript">
 var macs = {
 getMacAddress : function()
{
 document.macaddressapplet.setSep("-");
 alert("Mac Address ="+ document.macaddressapplet.getMacAddress() );
},

 getMacAddressesJSON : function()
{
 document.macaddressapplet.setSep(":");
 document.macaddressapplet.setFormat("%02x");
 var macs = eval( String( document.macaddressapplet.getMacAddressesJSON() ) );
 var mac_string ="";
 for( var idx = 0; idx < macs.length; idx ++ )
 mac_string +="t"+ macs[ idx ] +"n";

 alert("Mac Addresses = n"+ mac_string );
}
}
</script>

</body>

</html>