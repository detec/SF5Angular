<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Openbox SF-5 settings editor - print setting</title>
<sec:csrfMetaTags/>
<style>
table, th, td {
    border: 1px solid black;
    border-collapse: collapse;
 
}
th, td {
    padding: 5px;    
}
</style>
</head>
<body>
 

<b><c:out value="${bean.name}" /></b>

<table>
<tr>
<td>ID </td> <td><b><c:out value="${bean.id}" /></b></td> <td>Print date: </td><td><b><fmt:formatDate pattern="dd-MM-yyyy HH:mm" value="${sessiondate}" /></b></td>
</tr>
  </table> 
  <br>
  <table>
   <tr>
   <th></th> <!-- satindex header -->
   <th>#</th>
   <th>Tr. #</th>
   <th>Trans. freq</th>
   <th>Satellite</th>
   <th>Pol.</th>
   <th>Speed</th>
   <th>FEC</th>
   <th>Comment</th>
   </tr>
   <c:forEach items="${bean.dataSettingsConversion}" var="DataSC" varStatus="x">
   <c:if test="${DataSC.lineNumber == ((DataSC.satindex - 1) * 4 + 1)}">
   <tr>
   <td></td>
   </tr>
   </c:if>
  <tr>
  <td style="visibility:${DataSC.lineNumber == ((DataSC.satindex - 1) * 4 + 1) ? 'visible' : 'hidden'}" > <b><c:out value="${DataSC.satindex}"/></b></td>
   <td><c:out value="${DataSC.lineNumber}" /></td>
   <td><c:out value="${DataSC.tpindex}" /></td>
   <td><c:out value="${DataSC.transponder}" /></td>
   <td><c:out value="${DataSC.transponder.satellite}" /></td>
   <td><c:out value="${DataSC.transponder.polarization}" /></td>
   <td><c:out value="${DataSC.transponder.speed}" /></td>
   <td><c:out value="${DataSC.transponder.FEC}" /></td>
   <td><c:out value="${DataSC.note}"/></td>
   
  </tr>  
   </c:forEach>  
  </table>
   
</body>
</html>