<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.lastUpdate" path="lastUpdate" width="10%"/>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.stepUndergoing" path="stepUndergoing" width="10%"/>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.resolutionPercentage" path="resolutionPercentage" width="10%"/>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.status" path="status" width="10%"/>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.resolution" path="resolution" width="10%"/>
	<acme:list-column code="assistanceAgent.trackingLog.list.label.draftMode" path="draftMode" width="10%"/>
</acme:list>

<jstl:if test="${showCreate}">
	<acme:button code="assistanceAgent.trackingLog.form.button.create" action="/assistance-agent/tracking-log/create?masterId=${masterId}"/>
</jstl:if>

