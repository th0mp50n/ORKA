<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page import="nl.dtls.annotator.redux.security.auth.Provider" %>

<c:forEach items="${providers}" var="provider">
    <c:if test="${provider.configured}">
        <a ng-href="<%=((Provider)pageContext.getAttribute("provider")).getAuthorizeUrl(request)%>" class="btn btn-default">
	        <img src="${provider.icon}"> <span>Sign in with ${provider.displayName}</span>
	    </a>
    </c:if>
</c:forEach>