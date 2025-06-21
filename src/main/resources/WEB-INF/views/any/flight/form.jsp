<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.flight.form.label.tag" path="tag"/>	
	<acme:input-money code="manager.flight.form.label.cost" path="cost"/>	
	<acme:input-textarea code="manager.flight.form.label.description" path="description"/>	
	<acme:input-moment code="manager.flight.form.label.scheduledDeparture" path="scheduledDeparture" readonly="true"/>
	<acme:input-moment code="manager.flight.form.label.scheduledArrival" path="scheduledArrival" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.originCity" path="originCity" readonly="true"/>
	<acme:input-textbox code="manager.flight.form.label.destinationCity" path="destinationCity" readonly="true"/>
	<acme:input-integer code="manager.flight.form.label.layovers" path="layovers" readonly="true"/>
	<acme:input-checkbox code="manager.flight.form.label.selfTransfer" path="selfTransfer"/>

	<acme:button code="manager.flight.legs"  action="/any/leg/list?masterId=${id}"/>
	
</acme:form>