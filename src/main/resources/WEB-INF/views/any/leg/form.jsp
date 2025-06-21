<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>
<acme:form>
	<acme:input-textbox code="manager.leg.list.label.flightNumber" path="flightNumber" placeholder ="${codeIATA}" />
	<acme:input-moment code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture"/>	
	<acme:input-moment code="manager.leg.list.label.scheduledArrival" path="scheduledArrival"/>	
	<acme:input-select code="manager.leg.list.label.status" path="status" choices="${status}"/>
	<acme:input-select code="manager.leg.list.label.departureAirport" path="departureAirport" choices="${departureAirports}"/>
	<acme:input-select code="manager.leg.list.label.arrivalAirport" path="arrivalAirport" choices="${arrivalAirports}"/>
	<acme:input-select code="manager.leg.list.label.aircraft" path="aircraft" choices="${aircrafts}"/>
	<acme:input-integer code="manager.leg.list.label.duration" path="duration" readonly="true"/>	
		
</acme:form>