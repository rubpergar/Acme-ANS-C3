<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="authenticated.manager.form.label.identifierNumber" path="identifierNumber"/>
	<acme:input-integer code="authenticated.manager.form.label.yearsOfExperience" path="yearsOfExperience"/>
	<acme:input-moment code="authenticated.manager.form.label.birthDate" path="dateOfBirth"/>
	<acme:input-textbox code="authenticated.airline-manager.form.label.pictureLink" path="pictureLink"/>
	
	<jstl:if test="${_command == 'create'}" >
	<acme:input-select code="authenticated.manager.form.label.airline" path="airline" choices="${airlineChoices}"/>
		<acme:submit code="authenticated.manager.form.button.create" action="/authenticated/manager/create"/>
	</jstl:if>
	
	<jstl:if test="${_command == 'update'}" >
		<acme:input-select code="authenticated.manager.form.label.airline" path="airline" choices="${airlineChoices}" readonly="true"/>
		<acme:submit code="authenticated.manager.form.button.update" action="/authenticated/manager/update"/>
	</jstl:if>
	
</acme:form>
