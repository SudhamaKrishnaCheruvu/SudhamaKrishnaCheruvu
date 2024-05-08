<%@page import="com.liferay.custom.date.facet.portlet.CustomDateFacetConfiguration"%>
<%@ include file="/init.jsp" %>
<%@page import="com.liferay.custom.daterange.facet.display.context.CustomDateFacetCalendarDisplayContext"%>
<%@page import="com.liferay.custom.daterange.facet.display.context.CustomDateFacetTermDisplayContext"%>
<%@page import="com.liferay.custom.daterange.facet.display.context.CustomDateFacetDisplayContext"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/clay" prefix="clay" %><%@
taglib uri="http://liferay.com/tld/ddm" prefix="liferay-ddm" %><%@
taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>

<%@ page import="com.liferay.petra.string.StringPool" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.HtmlUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %>
<portlet:defineObjects />
<script type="text/javascript" src="<%=request.getContextPath()%>/js/customdate.js"></script>
<%
	CustomDateFacetDisplayContext modifiedFacetDisplayContext = (CustomDateFacetDisplayContext)java.util.Objects.requireNonNull(request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT));
if (modifiedFacetDisplayContext.isRenderNothing()) {
	return;
}
CustomDateFacetTermDisplayContext customRangeModifiedFacetTermDisplayContext = modifiedFacetDisplayContext.getCustomRangeModifiedFacetTermDisplayContext();
CustomDateFacetCalendarDisplayContext modifiedFacetCalendarDisplayContext = modifiedFacetDisplayContext.getCustomDateFacetCalendarDisplayContext();
CustomDateFacetConfiguration modifiedFacetPortletInstanceConfiguration = modifiedFacetDisplayContext.getCustomDateFacetConfiguration();
//out.println("....................."+portletPreferences.getValue("customFieldName", "createDate"));
%>
<c:if test="<%=!modifiedFacetDisplayContext.isRenderNothing()%>">
	<aui:form method="get" name="fm">
		<aui:input autocomplete="off" name="inputFacetName" type="hidden" value="createdate" />
		<aui:input cssClass="facet-parameter-name" name="facet-parameter-name" type="hidden" value="<%=HtmlUtil.escapeAttribute(modifiedFacetDisplayContext.getParameterName())%>" />
		<aui:input name="start-parameter-name" type="hidden" value="<%=modifiedFacetDisplayContext.getPaginationStartParameterName()%>" />
		<liferay-ddm:template-renderer
			className="<%=CustomDateFacetDisplayContext.class.getName()%>"
			contextObjects='<%=
				HashMapBuilder.<String, Object>put(
					"customRangeModifiedFacetTermDisplayContext", customRangeModifiedFacetTermDisplayContext
				).put(
					"modifiedFacetCalendarDisplayContext", modifiedFacetCalendarDisplayContext
				).put(
					"modifiedFacetDisplayContext", modifiedFacetDisplayContext
				).put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
			displayStyle=""
			displayStyleGroupId="<%= modifiedFacetDisplayContext.getDisplayStyleGroupId() %>"
			entries="<%= modifiedFacetDisplayContext.getCustomDateFacetTermDisplayContexts() %>"
		>
			<liferay-ui:panel-container
				extended="<%= true %>"
				id='<%= liferayPortletResponse.getNamespace() + "facetModifiedPanelContainer" %>'
				markupView="lexicon"
				persistState="<%= true %>"
			>
				<liferay-ui:panel
					collapsible="<%= true %>"
					cssClass="search-facet"
					id='<%= liferayPortletResponse.getNamespace() + "facetModifiedPanel" %>'
					markupView="lexicon"
					persistState="<%= true %>"
					title="<%=portletPreferences.getValue("customFieldName", "createDate") %>"
				>
					<ul class="list-unstyled modified">
					
						<%
						for (CustomDateFacetTermDisplayContext termDateFacetTermDisplayContext : modifiedFacetDisplayContext.getCustomDateFacetTermDisplayContexts()) {
						%>
							<li class="facet-value" name="<%= liferayPortletResponse.getNamespace() + "range_" + termDateFacetTermDisplayContext.getLabel() %>">
								<a href="<%= termDateFacetTermDisplayContext.getRangeURL() %>">
									<span class="term-name <%= termDateFacetTermDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>">
										<liferay-ui:message key="<%= termDateFacetTermDisplayContext.getLabel() %>" />
									</span>
									<small class="term-count">
<%-- 										(<%= termDateFacetTermDisplayContext.getFrequency() %>) --%>
									</small>
								</a>
							</li>
						<%
						}
						%>
						<li class="facet-value" name="<%= liferayPortletResponse.getNamespace() + "range_" + customRangeModifiedFacetTermDisplayContext.getLabel() %>">
							<a class="custom-range-link" href="javascript:;" id="<portlet:namespace /><%= customRangeModifiedFacetTermDisplayContext.getLabel() + "-toggleLink" %>">
								<span class="term-name <%= customRangeModifiedFacetTermDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>"><liferay-ui:message key="<%= customRangeModifiedFacetTermDisplayContext.getLabel() %>" />&hellip;</span>
								<c:if test="<%= customRangeModifiedFacetTermDisplayContext.isSelected() %>">
									<small class="term-count">
<%-- 										(<%= customRangeModifiedFacetTermDisplayContext.getFrequency() %>) --%>
									</small>
								</c:if>
							</a>
						</li>
						<div class="custom-date-range" id="<portlet:namespace />customRange">
							<clay:col
								id='<%= liferayPortletResponse.getNamespace() + "customRangeFrom" %>'
								md="6"
							>
								<label for="<portlet:namespace />fromInput" class="hide-text">Search</label>
								
								<aui:field-wrapper label="from">
									<liferay-ui:input-date
										cssClass="modified-facet-custom-range-input-date-from"
										dayParam="fromDay"
										dayValue="<%= modifiedFacetCalendarDisplayContext.getFromDayValue() %>"
										disabled="<%= false %>"
										firstDayOfWeek="<%= modifiedFacetCalendarDisplayContext.getFromFirstDayOfWeek() %>"
										monthParam="fromMonth"
										monthValue="<%= modifiedFacetCalendarDisplayContext.getFromMonthValue() %>"
										name="fromInput"
										yearParam="fromYear"
										yearValue="<%= modifiedFacetCalendarDisplayContext.getFromYearValue() %>"
									/>
								</aui:field-wrapper>
							</clay:col>
							<clay:col
								id='<%= liferayPortletResponse.getNamespace() + "customRangeTo" %>'
								md="6"
							>
								<label for="<portlet:namespace />toInput" class="hide-text">Search</label>
								
								<aui:field-wrapper label="to">
									<liferay-ui:input-date
										cssClass="modified-facet-custom-range-input-date-to"
										dayParam="toDay"
										dayValue="<%= modifiedFacetCalendarDisplayContext.getToDayValue() %>"
										disabled="<%= false %>"
										firstDayOfWeek="<%= modifiedFacetCalendarDisplayContext.getToFirstDayOfWeek() %>"
										monthParam="toMonth"
										monthValue="<%= modifiedFacetCalendarDisplayContext.getToMonthValue() %>"
										name="toInput"
										yearParam="toYear"
										yearValue="<%= modifiedFacetCalendarDisplayContext.getToYearValue() %>"
									/>
								</aui:field-wrapper>
							</clay:col>
							<aui:button cssClass="modified-facet-custom-range-filter-button" disabled="<%= modifiedFacetCalendarDisplayContext.isRangeBackwards() %>" name="searchCustomRangeButton" value="search" />
						</div>
					</ul>
					<c:if test="<%= !modifiedFacetDisplayContext.isNothingSelected() %>">
						<aui:button cssClass="btn-link btn-unstyled facet-clear-btn" onClick="Liferay.Search.FacetUtil.clearSelections(event);" value="clear" />
					</c:if>
				</liferay-ui:panel>
			</liferay-ui:panel-container>
		</liferay-ddm:template-renderer>
	</aui:form>
	<aui:script use="liferay-search-create-date-facet">
		new Liferay.Search.CreateDateFacetFilter({
			form: A.one('#<portlet:namespace />fm'),
			fromInputDatePicker: Liferay.component(
				'<portlet:namespace />fromInputDatePicker'
			),
			fromInputName: '<portlet:namespace />fromInput',
			namespace: '<portlet:namespace />',
			searchCustomRangeButton: A.one(
				'#<portlet:namespace />searchCustomRangeButton'
			),
			toInputDatePicker: Liferay.component(
				'<portlet:namespace />toInputDatePicker'
			),
			toInputName: '<portlet:namespace />toInput',
		});
		Liferay.Search.FacetUtil.enableInputs(
			document.querySelectorAll('#<portlet:namespace />fm .facet-term')
		);
		
	</aui:script>
</c:if>

<script>
	$(".custom-range-link").click(function(){
		$(".custom-date-range").toggle();
	});
	
	$( document ).ready(function() {
		if (window.location.href.indexOf(fieldName+"From") > -1) {
			$(".custom-date-range").show();
		}
		else{
			$(".custom-date-range").hide();
		}
	});
</script>