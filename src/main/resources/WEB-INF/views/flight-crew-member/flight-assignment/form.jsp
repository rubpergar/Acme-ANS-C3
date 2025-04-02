<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-select code="member.flight-assignment.form.label.duty" path="duty" choices="${duty}"/>
	<acme:input-moment code="member.flight-assignment.form.label.lastUpdateMoment" path="lastUpdateMoment" readonly="true"/>		
	<acme:input-select code="member.flight-assignment.form.label.status" path="status" choices="${status}"/>	
	<acme:input-textbox code="member.flight-assignment.form.label.remarks" path="remarks"/>	
	<acme:input-select code="member.flight-assignment.form.label.leg" path="leg" choices="${legs}"/>	
	<acme:input-select code="member.flight-assignment.form.label.flightCrewMember" path="flightCrewMember" choices="${members}"/>	
	
	<jstl:choose>	 
		<jstl:when test="${_command == 'show' && draftMode == false}">
			<acme:button code="member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
		</jstl:when>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete|publish') && draftMode == true}">
			<acme:button code="member.flight-assignment.form.button.activity-log" action="/flight-crew-member/activity-log/list?masterId=${id}"/>
			<acme:submit code="member.flight-assignment.form.button.update" action="/flight-crew-member/flight-assignment/update"/>
			<acme:submit code="member.flight-assignment.form.button.delete" action="/flight-crew-member/flight-assignment/delete"/>
			<acme:submit code="member.flight-assignment.form.button.publish" action="/flight-crew-member/flight-assignment/publish"/>
		</jstl:when>
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="member.flight-assignment.form.button.create" action="/flight-crew-member/flight-assignment/create"/>
		</jstl:when>		
	</jstl:choose>
	
</acme:form>