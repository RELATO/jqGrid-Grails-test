<h3>Subtract</h3>
<g:formRemote name="subForm" url="[controller:'math', action:'difference']" update="subtraction">
    <label for="a">First Number</label><g:textField name="a"/><br/>
    <label for="b">Second Number</label><g:textField name="b"/><br/>
    <g:submitButton name="Get The Difference"/>
</g:formRemote>