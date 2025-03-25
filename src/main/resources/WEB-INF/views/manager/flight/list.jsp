
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.tag" path="tag"  width="20%"/>
	<acme:list-column code="manager.flight.list.label.cost" path="cost" width="20%" />
	<acme:list-column code="manager.flight.list.label.description" path="description" width="20%" />
	<acme:list-column code="manager.flight.list.label.selfTransfer" path="selfTransfer" width="20%" />
	<acme:list-column code="manager.flight.list.label.isDraft" path="isDraft" width="20%" />
	
</acme:list>
<acme:button code="manager.flight.create" action="/manager/flight/create"/>
<acme:button code="manager.flight.delete" action="/manager/flight/delete"/>
<acme:button code="manager.flight.update" action="/manager/flight/update"/>
<acme:button code="manager.flight.publish" action="/manager/flight/publish"/>