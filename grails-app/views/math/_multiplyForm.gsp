<h3>Multiply</h3>
<g:form>
    <label for="a">First Number</label><g:textField name="a"/><br/>
    <label for="b">Second Number</label><g:textField name="b"/><br/>
    <g:submitToRemote url="[controller:'math', action:'product']" update="multiplication" value="Get The Product"/>
</g:form>