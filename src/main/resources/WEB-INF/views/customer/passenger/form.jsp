<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
    <acme:input-textbox code="customer.passenger.form.label.fullName" path="fullName"/>    
    <acme:input-email code="customer.passenger.form.label.email" path="email"/>
    <acme:input-textbox code="customer.passenger.form.label.passportNumber" path="passportNumber"/>  
    <acme:input-textbox code="customer.passenger.form.label.dateOfBirth" path="dateOfBirth"/>     
    <acme:input-textarea code="customer.passenger.form.label.specialNeeds" path="specialNeeds"/>  
    <acme:input-checkbox code="customer.passenger.form.isDraft" path="isDraft" readonly="true"/>
        
    <jstl:choose>     
        <jstl:when test="${acme:anyOf(_command, 'show|update')  && isDraft == true}">
            <acme:submit code="customer.passenger.form.button.update" action="/customer/passenger/update"/>
        </jstl:when>
        <jstl:when test="${_command == 'create'}">
            <acme:submit code="customer.passenger.form.button.create" action="/customer/passenger/create"/>
        </jstl:when>        
    </jstl:choose>
</acme:form>

	