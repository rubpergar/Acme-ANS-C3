<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="customer.booking.form.label.locatorCode" path="locatorCode"/>    
    <acme:input-select 
            code="customer.booking.form.label.flight" 
            path="selectedFlight" 
            choices="${flight}" />
    <acme:input-textbox code="customer.booking.form.label.purchaseMoment" path="purchaseMoment" readonly="true"/>    
    <acme:input-select code="customer.booking.form.label.travelClass" path="travelClass" choices="${travelClass}"/>    
    <acme:input-money code="customer.booking.form.label.price" path="price" readonly="true"/>
    <acme:input-textbox code="customer.booking.form.label.lastNibble" path="lastNibble"/>
        
    <jstl:choose>     
        <jstl:when test="${_command == 'show' && isDraft == false}">
            <acme:button code="customer.booking.passengers" action="/customer/booking-passenger/list?masterId=${id}"/>
        </jstl:when>
        <jstl:when test="${acme:anyOf(_command, 'show|update|publish|delete')  && isDraft == true}">
            <acme:button code="customer.booking.passengers" action="/customer/booking-passenger/list?masterId=${id}"/>
            <acme:submit code="customer.booking.form.button.update" action="/customer/booking/update"/>
            <acme:submit code="customer.booking.form.button.publish" action="/customer/booking/publish"/>
            <acme:submit code="customer.booking.form.button.delete" action="/customer/booking/delete"/>
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="customer.booking.form.button.create" action="/customer/booking/create"/>
        </jstl:when>        
    </jstl:choose>
</acme:form>

	
