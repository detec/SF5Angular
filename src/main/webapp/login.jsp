<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>


<html>
<head>
<meta charset="UTF-8">
<title>Openbox SF-5 settings editor - login and registration</title>
<sec:csrfMetaTags/>
</head>
<body>
	<h1>Openbox SF-5 settings editor </h1>
   
<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
      <font color="red">
        Your login attempt was not successful due to <br/><br/>
        <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </font>
</c:if>
 <c:if test="${not empty viewErrMsg}" >
 <div style="color:Red;"> ${viewErrMsg}</div>
 </c:if>
      <table>
         <tr>
         <td>
         <h2>Sign in</h2>
   <form name='loginform' action="login" method='POST'>
      <table>
         <tr>
            <td>User:</td>
            <td><input type='text' id="username" name="username"/></td>
         </tr>
         <tr>
            <td>Password:</td>
            <td><input type='password' id="password" name="password"/></td>
         </tr>
         <tr>
            <td><input name="submit" type="submit" value="Submit" /></td>
         </tr>
      </table>

       
  	<input type="hidden" name="${_csrf.parameterName}"
                value="${_csrf.token}" />    
  	</form>
  <a href="/registration/register.html">Sign up</a>
      </td>
      
      
      <td>
      <%--  
       <h2>Sign up</h2>

 <c:if test="${not empty viewErrMsg}" >
 <div style="color:Red;"> ${viewErrMsg}</div>
 </c:if>
 

 
    <form:form modelAttribute="user" method="POST" enctype="utf8">
        <br>
        <table>
        <tr>
        <td><label>Username
            </label>
        </td>
        <td><form:input path="username" value="" /></td>
        <form:errors path="username" element="div" cssStyle="color:Red"/>
    </tr>
    <tr>
        <td><label>Password
            </label>
        </td>
        <td><form:input path="password" value="" type="password" /></td>
        <form:errors path="password" element="div" cssStyle="color:Red"/>
    </tr>
    <tr>
        <td><label>Confirm password
            </label>
        </td>
        <td><form:input path="matchingPassword" value="" type="password" /></td>
        <form:errors element="div" cssStyle="color:Red"/>
    </tr>
    </table>
        <button type="submit">Submit</button>
        
        	<input type="hidden" name="${_csrf.parameterName}"
                value="${_csrf.token}" />
    </form:form> --%>
      </td>
      </tr>
    </table>

</body>
</html>
