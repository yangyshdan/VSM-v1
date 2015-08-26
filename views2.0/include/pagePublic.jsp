<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/jstl-core" prefix="c" %>
<%@ taglib uri="/tags/jstl-format" prefix="fmt" %>
<%@ taglib uri="/tags/jstl-function" prefix="fn" %>
<%@ taglib uri="/tags/stringHelper" prefix="strHelper" %>
<%@ taglib uri="oscache" prefix="oscache"%>
<%@ taglib uri="/tags/cos-cstatus" prefix="cs" %>
<c:set var="path" value="${pageContext.request.contextPath}"></c:set>
<%
 //去除jstl产生的空格
 out.clear();
%>
