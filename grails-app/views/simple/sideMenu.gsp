<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Simple Ajax</title>
        <g:javascript library="jquery"/>
	</head>
	<body>
        <div style='float: left; width: 20%; min-height: 500px; background-color: #c0c0c0;'>
            <g:set var="theID" value="${999}"/>
            <g:set var="firstNumber" value="${5}"/>
            <g:set var="secondNumber" value="${7}"/>

            <g:remoteLink controller="simple" action="renderString"
                update="mainContent">Render String</g:remoteLink><br/>

            <g:remoteLink controller="simple" action="renderPage"
                update="mainContent">Render Page</g:remoteLink><br/>

            <g:remoteLink controller="simple" action="renderView"
                update="mainContent">Render View</g:remoteLink><br/>

            <g:remoteLink controller="simple" action="renderTemplate"
                update="mainContent">Render Template</g:remoteLink><br/>

            <g:remoteLink controller="simple" action="withIdParam" id="${theID}"
                update="mainContent">With ID Parameter</g:remoteLink><br/>

            <g:remoteLink controller="simple" action="withCustomParam"
                params="${[a:firstNumber, b:secondNumber]}"
                update="mainContent">With Custom Parameter</g:remoteLink><br/>
        </div>
        <div id="mainContent" style='float: right; width: 80%; min-height: 500px; background-color: #c0ffc0;'>
            <p>This is the main content</p>
        </div>
	</body>
</html>