<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>

<acme:form>
		<acme:input-textbox code="administrator.aircraft.list.label.model" path="model" />
		<acme:input-textbox code="administrator.aircraft.list.label.registrationNumber" path="registrationNumber"/>
		<acme:input-integer code="administrator.aircraft.list.label.capacity" path="capacity"/>
		<acme:input-integer code="administrator.aircraft.list.label.cargoWeight" path="cargoWeight"/>
		<acme:input-select code="administrator.aircraft.list.label.status" path="status" choices="${status}"/>
		<acme:input-select code="administrator.aircraft.form.label.airline" path="airline" choices= "${airlines}"/>
		<acme:input-textbox code="administrator.aircraft.list.label.details" path="details"/>
		
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show|update|disable')}">
				<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>
				<acme:submit code="administrator.airport.form.button.update" action="/administrator/aircraft/update"/>
				<acme:submit code="administrator.airport.form.button.disable" action="/administrator/aircraft/disable"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
			    <input type="hidden" name="confirmation" value="false"/>
				<acme:input-checkbox code="administrator.aircraft.form.label.confirmation" path="confirmation"/>	
				<acme:submit code="administrator.aircraft.form.button.create" action="/administrator/aircraft/create"/>
			</jstl:when>		
	</jstl:choose>		
</acme:form>