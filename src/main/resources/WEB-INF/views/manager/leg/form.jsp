<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<acme:form>
	<acme:input-textbox code="manager.leg.list.label.flightNumber" path="flightNumber"/>	
	<acme:input-moment code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture"/>	
	<acme:input-moment code="manager.leg.list.label.scheduledArrival" path="scheduledArrival"/>	
	<acme:input-select code="manager.leg.list.label.status" path="status" choices="${status}"/>
	<acme:input-select code="manager.leg.list.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
	<acme:input-select code="manager.leg.list.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="manager.leg.list.label.aircraft" path="aircraft" choices="${aircrafts}"/>
	<acme:input-integer code="manager.leg.list.label.duration" path="duration" readonly="true"/>	
	
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete') && isDraft == true && isDraftFlight == true}">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.create.button" action="/manager/leg/create?masterId=${masterId}"/>
		</jstl:when>		
	</jstl:choose>		
</acme:form>