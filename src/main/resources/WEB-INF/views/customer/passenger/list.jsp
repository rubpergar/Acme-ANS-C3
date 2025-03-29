<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="customer.passenger.list.label.fullName" path="fullName"  width="20%"/>
	<acme:list-column code="customer.passenger.list.label.email" path="email" width="20%" />
	<acme:list-column code="customer.passenger.list.label.passportNumber" path="passportNumber" width="20%" />
	<acme:list-column code="customer.passenger.list.label.dateOfBirth" path="dateOfBirth" width="20%" />
	<acme:list-column code="customer.passenger.list.label.isDraft" path="isDraft" width="20%" />
	
</acme:list>
<acme:button code="customer.passenger.create" action="/customer/passenger/create"/>
<acme:button code="customer.passenger.update" action="/customer/passenger/update"/>
<acme:button code="customer.passenger.publish" action="/customer/passenger/publish"/>