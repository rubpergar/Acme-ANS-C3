
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="any.weather-dashboard.list.label.city" path="city" width="20%"/>
	<acme:list-column code="any.weather-dashboard.list.label.country" path="country" width="20%"/>
	<acme:list-column code="any.weather-dashboard.list.label.date" path="date" width="20%"/>
</acme:list>