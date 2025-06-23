
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.leg.list.label.flightNumber2" path="flightNumber"  width="20%"/>
	<acme:list-column code="manager.leg.list.label.scheduledDeparture" path="scheduledDeparture" width="20%" />
	<acme:list-column code="manager.leg.list.label.scheduledArrival" path="scheduledArrival" width="20%" />
	<acme:list-column code="manager.leg.list.label.status" path="status" width="20%" />
	<acme:list-column code="manager.leg.list.label.departureAirport" path="departureAirport" width="20%" />
	<acme:list-column code="manager.leg.list.label.arrivalAirport" path="arrivalAirport" width="20%" />
	<acme:list-column code="manager.leg.list.label.aircraft2" path="aircraft" width="20%" />
	
</acme:list>