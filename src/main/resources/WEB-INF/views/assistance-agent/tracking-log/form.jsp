<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-moment code="assistanceAgent.trackingLog.form.label.lastUpdate" path="lastUpdate" readonly="true"/>
	<acme:input-textbox code="assistanceAgent.trackingLog.form.label.stepUndergoing" path="stepUndergoing"/>
	<acme:input-double code="assistanceAgent.trackingLog.form.label.resolutionPercentage" path="resolutionPercentage"/>
	<acme:input-select code="assistanceAgent.trackingLog.form.label.status" path="status" choices="${status}"/>
	<acme:input-textarea code="assistanceAgent.trackingLog.form.label.resolution" path="resolution"/>
		
	<jstl:choose>
		<jstl:when test="${acme:anyOf(_command, 'show|update|delete')  && draftMode == true}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.update" action="/assistance-agent/trackingLog/update"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.delete" action="/assistance-agent/trackingLog/delete"/>
			<acme:submit code="assistanceAgent.trackingLog.form.button.publish" action="/assistance-agent/trackingLog/publish"/>
		</jstl:when>
		
		<jstl:when test="${_command == 'create'}">
			<acme:submit code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create?masterId=${masterId}"/>
		</jstl:when>	
	</jstl:choose>
</acme:form>