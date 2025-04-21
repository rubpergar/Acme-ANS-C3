<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="administrator.airport.list.label.name" path="name" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.IATAcode" path="codeIATA" width="10%"/>
	<acme:list-column code="administrator.airport.list.label.scope" path="scope" width="10%"/>
	
</acme:list>
<jstl:if test="${_command == 'list'}">
	<acme:button code="administrator.airport.list.button.create" action="/administrator/airport/create"/>
</jstl:if>	

