<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%> 	
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>


<acme:form>
    <acme:input-textbox code="any.weather-dashboard.list.label.city" path="city" readonly="true"/>
    <acme:input-textbox code="any.weather-dashboard.list.label.country" path="country" readonly="true"/>
    <acme:input-textbox code="any.weather-dashboard.list.label.temperature" path="temperature" readonly="true"/>
    <acme:input-textbox code="any.weather-dashboard.list.label.humidity" path="humidity" readonly="true"/>
    <acme:input-textbox code="any.weather-dashboard.list.label.windSpeed" path="windSpeed" readonly="true"/>
    <acme:input-moment code="any.weather-dashboard.list.label.date" path="date" readonly="true"/>
    <acme:input-textbox code="any.weather-dashboard.list.label.description" path="description" readonly="true"/>
</acme:form>