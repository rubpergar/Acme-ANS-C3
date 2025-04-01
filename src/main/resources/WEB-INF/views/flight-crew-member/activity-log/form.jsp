<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="member.activity-log.form.label.registrationMoment" path="lastUpdateMoment" readonly="true"/>		
	<acme:input-textbox code="member.activity-log.form.label.type" path="type"/>	
	<acme:input-textbox code="member.activity-log.form.label.description" path="description"/>	
	<acme:input-integer code="member.activity-log.form.label.severityLevel" path="severityLevel"/>	
	<acme:input-select code="member.activity-log.form.label.flightAssignment" path="flightAssignment" choices="${members}" readonly="true"/>	
	
	<jstl:choose>	 
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:submit code="member.activity-log.form.button.update" action="/flight-crew-member/activity-log/update"/>
			<acme:submit code="member.activity-log.form.button.delete" action="/flight-crew-member/activity-log/delete"/>
			<acme:submit code="member.activity-log.form.button.publish" action="/flight-crew-member/activity-log/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="member.activity-log.form.button.create" action="/flight-crew-member/activity-log/create"/>
		</jstl:when>		
	</jstl:choose>
	
</acme:form>