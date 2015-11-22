<!DOCTYPE html>
<html>
    <head>
        <meta name="layout" content="main"/>
        <title>Sum</title>
    </head>
    <body>
    <h3>Sum</h3>
    <g:form controller="math" action="sum">
        <label for="a">First Number</label><g:textField name="a"/><br/>
        <label for="b">Second Number</label><g:textField name="b"/><br/>
        <g:submitButton name="Get The Sum"/>
    </g:form>
    </body>
</html>