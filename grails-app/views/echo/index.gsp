<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Simple Ajax</title>
        <g:javascript library="jquery"/>
        <style>
            #echoBox {
                border: 1px solid #000; width: 300px; height: 100px; background-color: #c0c0c0;
            }
        </style>
	</head>
	<body>
        <div id="echoBox">
        </div>
        <g:remoteField controller="echo" action="shout" update="echoBox"/>
	</body>
</html>