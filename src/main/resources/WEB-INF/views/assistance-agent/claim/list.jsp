
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="assistanceAgent.claim.list.label.registrationMoment" path="registrationMoment" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.email" path="email" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.description" path="description" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.type" path="type" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.status" path="status" width="10%"/>
	<acme:list-column code="assistanceAgent.claim.list.label.draftMode" path="draftMode" width="10%"/>
</acme:list>


