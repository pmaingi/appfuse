<%@ page language="java" isErrorPage="true" %>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/page" prefix="page"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>

<page:applyDecorator name="default">

    <title><fmt:message key="errorPage.title"/></title>

    <h1><fmt:message key="errorPage.heading"/></h1>
    <%-- Error Messages --%>
    <logic:messagesPresent>
        <logic:present name="error">    
        <div class="error">    
            <html:messages id="error">
                <c:out value="${error}" escapeXml="false"/><br/>
            </html:messages>
        </div>
        </logic:present>
    </logic:messagesPresent>
 <% if (exception != null) { %>
    <pre><% exception.printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if ((Exception)request.getAttribute("javax.servlet.error.exception") != null) { %>
    <pre><% ((Exception)request.getAttribute("javax.servlet.error.exception"))
                           .printStackTrace(new java.io.PrintWriter(out)); %></pre>
 <% } else if (pageContext.findAttribute("org.apache.struts.action.EXCEPTION") != null) { %>
    <bean:define id="exception2" name="org.apache.struts.action.EXCEPTION"
     type="java.lang.Exception"/>
    <c:if test="${exception2 != null}">
        <pre><% exception2.printStackTrace(new java.io.PrintWriter(out));%></pre>
    </c:if>
    <%-- only show this if no error messages present --%>
    <c:if test="${exception2 == null}">
        <fmt:message key="errors.none"/>
    </c:if>
 <% } %>
</page:applyDecorator>