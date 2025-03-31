<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>  
 <acme:input-textbox code="customer.bookingPassenger.form.label.booking" path="booking" readonly="true"/>
    <acme:input-select code="customer.bookingPassenger.form.label.passenger" path="passenger" choices="${passenger}"/>
            
    <jstl:choose>     
    
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="customer.bookingPassenger.form.button.addPassenger" action="/customer/booking-passenger/create?masterId=${booking.id}"/>
        </jstl:when>        
    </jstl:choose>
</acme:form>

	