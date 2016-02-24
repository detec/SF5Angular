<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Openbox SF-5 settings editor - Login</title>
<sec:csrfMetaTags/>
</head>
<body>
	<h1>Openbox SF-5 settings editor </h1>
   <h2>Login</h2>
<c:if test="${not empty SPRING_SECURITY_LAST_EXCEPTION}">
      <font color="red">
        Your login attempt was not successful due to <br/><br/>
        <c:out value="${SPRING_SECURITY_LAST_EXCEPTION.message}"/>.
      </font>
</c:if>
   
   <form name='f' action="login" method='POST'>
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
 No account? <a href="register">Register</a>
</body>
</html>
