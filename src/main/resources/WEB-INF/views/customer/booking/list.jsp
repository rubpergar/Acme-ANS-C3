<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.booking.list.label.locatorCode" path="locatorCode"  width="20%"/>
		<acme:list-column code="customer.booking.list.label.flight" path="flight"  width="20%"/>
	<acme:list-column code="customer.booking.list.label.purchaseMoment" path="purchaseMoment" width="20%" />
	<acme:list-column code="customer.booking.list.label.travelClass" path="travelClass" width="20%" />
	<acme:list-column code="customer.booking.list.label.price" path="price" width="20%" />
	<acme:list-column code="customer.booking.list.label.isDraft" path="isDraft" width="20%" />
	
</acme:list>
<acme:button code="customer.booking.create" action="/customer/booking/create"/>
<acme:button code="customer.booking.update" action="/customer/booking/update"/>
<acme:button code="customer.booking.publish" action="/customer/booking/publish"/>