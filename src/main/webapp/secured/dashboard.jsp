<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h1>Dashboard</h1>

        <form action="${pageContext.request.contextPath}/logout">
            <input type="submit" value="Logout">
        </form>

    </body>
</html>
