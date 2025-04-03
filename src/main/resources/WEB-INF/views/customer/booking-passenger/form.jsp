<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>  
 <acme:input-textbox code="customer.bookingPassenger.form.label.booking" path="booking" readonly="true"/>     
    <jstl:choose>     
    
        <jstl:when test="${_command == 'create'}">
            <acme:input-select code="customer.bookingPassenger.form.label.passenger" path="passenger" choices="${passenger}"/>
            <acme:submit code="customer.bookingPassenger.form.button.addPassenger" action="/customer/booking-passenger/create?masterId=${booking.id}"/>
        </jstl:when>  
        <jstl:when test="${acme:anyOf(_command, 'show|delete')  && bookingIsDraft == true}">
                    <acme:input-select code="customer.bookingPassenger.form.label.passenger" path="passenger" choices="${passenger}" readonly="true"/>
            <acme:submit code="customer.bookingPassenger.form.button.deleteBookingPassenger" action="/customer/booking-passenger/delete"/>
        </jstl:when>      
        <jstl:when test="${bookingIsDraft == false || bookingIsDraft == null}">
            <acme:input-select code="customer.bookingPassenger.form.label.passenger" path="passenger" choices="${passenger}" readonly="true"/>
        </jstl:when>  
    </jstl:choose>
</acme:form>

	