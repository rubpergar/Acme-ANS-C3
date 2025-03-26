<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.leg.list.label.flightNumber" path="flightNumber"/>	
	<acme:input-moment code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture"/>	
	<acme:input-moment code="manager.leg.list.label.scheduledArrival" path="scheduledArrival"/>	
	<acme:input-select code="manager.leg.list.label.status" path="status" choices="${status}"/>
	<acme:input-textbox code="manager.leg.list.label.arrivalAirport" path="arrivalAirport"/>
	<acme:input-textbox code="manager.leg.list.label.departureAirport" path="departureAirport"/>
	<acme:input-textbox code="manager.leg.list.label.aircraft" path="aircraft"/>
	
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && isDraft == false}">
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish')  }">
			<acme:submit code="manager.leg.form.button.update" action="/manager/leg/update"/>
			<acme:submit code="manager.leg.form.button.delete" action="/manager/leg/delete"/>
			<acme:submit code="manager.leg.form.button.publish" action="/manager/leg/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="manager.leg.create.button" action="/manager/leg/create"/>
		</jstl:when>		
	</jstl:choose>
	
</acme:form>