<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<acme:form>
		<acme:input-textbox code="administrator.airport.form.label.name" path="name" />
		<acme:input-textbox code="administrator.airport.form.label.IATAcode" path="codeIATA"/>
		<acme:input-select code="administrator.airport.form.label.scope" path="scope" choices="${scope}"/>
		<acme:input-textbox code="administrator.airport.form.label.city" path="city"/>
		<acme:input-textbox code="administrator.airport.form.label.country" path="country"/>
		<acme:input-textbox code="administrator.airport.form.label.web" path="web" />
		<acme:input-textbox code="administrator.airport.form.label.email" path="email"/>
		<acme:input-textbox code="administrator.airport.form.label.phone" path="phone"/>
		
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show|update')}">
				<acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>
				<acme:submit code="administrator.airport.form.button.update" action="/administrator/airport/update"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:input-checkbox code="administrator.airport.form.label.confirmation" path="confirmation"/>	
				<acme:submit code="administrator.airport.form.button.create" action="/administrator/airport/create"/>
			</jstl:when>		
	</jstl:choose>		
</acme:form>