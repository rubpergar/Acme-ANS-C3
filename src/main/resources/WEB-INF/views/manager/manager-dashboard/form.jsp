<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
	<acme:input-textbox code="manager.dashboard.form.label.rankingPosition" path="rankingPosition"/>
	<acme:input-textbox code="manager.dashboard.form.label.yearsToRetirement" path="yearsToRetirement"/>
	<acme:input-double code="manager.dashboard.form.label.onTimeDelayedLegsRatio" path="onTimeDelayedLegsRatio"/>
	<acme:input-textbox code="manager.dashboard.form.label.mostPopularAirport" path="mostPopularAirport"/>
	<acme:input-textbox code="manager.dashboard.form.label.leastPopularAirport" path="leastPopularAirport"/>
	<acme:input-textbox code="manager.dashboard.form.label.numberofLegsByStatus" path="numberofLegsByStatus"/>
	<acme:input-textbox code="manager.dashboard.form.label.averageFlightCost" path="averageFlightCost"/>
	<acme:input-textbox code="manager.dashboard.form.label.deviationFlightCost" path="deviationFlightCost"/>
	<acme:input-textbox code="manager.dashboard.form.label.maximumFlightCost" path="maximumFlightCost"/>
	<acme:input-textbox code="manager.dashboard.form.label.minimumFlightCost" path="minimumFlightCost"/>
</acme:form>