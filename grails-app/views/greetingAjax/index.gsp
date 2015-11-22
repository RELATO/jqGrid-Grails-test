<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Simple Ajax</title>
        <g:javascript library="jquery"/>
        <style>
            #greetingBox {
                border: 1px solid #000; width: 300px; height: 100px; background-color: #c0c0c0;
            }
        </style>
	</head>
	<body>
        <div id="greetingBox">
        </div>

        <div>
            <button onclick="<g:remoteFunction controller="greetingAjax" action="count" update="greetingBox"/>">Count</button>
        </div>

        <div>
            <input type="text" name="name" onchange="greetName(this.value)"/>
        </div>

        <script>
            <g:remoteFunction controller="greetingAjax" action="initialGreeting" update="greetingBox"/>

            function greetName(name) {
                <g:remoteFunction controller="greetingAjax" action="greetName" update="greetingBox" params="'name='+name"/>
            }
        </script>

	</body>
</html>