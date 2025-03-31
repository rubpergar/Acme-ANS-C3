<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="assistanceAgent.claim.form.label.registrationMoment" path="registrationMoment"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.email" path="email"/>
	<acme:input-textbox code="assistanceAgent.claim.form.label.description" path="description"/>
	<acme:input-select code="assistanceAgent.claim.form.label.type" path="type" choices="${type}"/>
	<acme:input-select code="assistanceAgent.claim.form.label.status" path="status" choices="${status}"/>
	<acme:input-select code="assistanceAgent.claim.form.label.leg" path="selectedLeg" choices="${legs}"/>
		
	<jstl:choose>
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="assistanceAgent.claim" action="/assistance-agent/claim/list?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')  && draftMode == true}">
			<acme:submit code="assistanceAgent.claim.form.button.update" action="/assistance-agent/claim/update"/>
			<acme:submit code="assistanceAgent.claim.form.button.delete" action="/assistance-agent/claim/delete"/>
			
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistanceAgent.claim.form.button.create" action="/assistance-agent/claim/create"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>