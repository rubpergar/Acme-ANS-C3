
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list.label.registrationMoment" path="registrationMoment" width="5%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.description" path="description" width="5%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.type" path="type" width="5%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.status" path="status" width="5%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.draftMode" path="draftMode" width="5%"/>
</acme:list>
<acme:button code="assistanceAgent.claim.create" action="/assistance-agent/claim/create"/>

