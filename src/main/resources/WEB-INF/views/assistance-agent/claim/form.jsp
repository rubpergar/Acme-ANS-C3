<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.email" path="email"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type" choices="${type}"/>
	<acme:input-select code="assistanceAgent.claim.form.label.status" path="status" choices="${status}"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.leg" path="leg"/>	
	
</acme:form>