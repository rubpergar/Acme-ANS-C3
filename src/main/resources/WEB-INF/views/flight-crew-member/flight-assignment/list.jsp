
<%@page language="java"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="member.flight-assignment.list.label.duty" path="duty"  width="25%"/>
	<acme:list-column code="member.flight-assignment.list.label.status" path="status" width="25%" />
	<acme:list-column code="member.flight-assignment.list.label.remarks" path="remarks" width="25%" />
	<acme:list-column code="member.flight-assignment.list.label.draftMode" path="draftMode" width="25%" />
	
	<acme:list-payload path="payload"/>
	
</acme:list>

<acme:button code="member.flight-assignment.list.button.create" action="/flight-crew-member/flight-assignment/create"/>

<!-- 
<jstl:if test="${_command == 'listUncompleted'}">
	<acme:button code="member.flight-assignment.list.button.create" action="/flight-crew-member/flight-assignment/create"/>
</jstl:if> 
-->