<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>つぶやきの編集</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="main-contents">

		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" />
					</c:forEach>
				</ul>
			</div>
		</c:if>

		<c:if test="${ not empty loginUser }">
			<a href="./">ホーム</a>
			<a href="setting">設定</a>
			<a href="logout">ログアウト</a>
		</c:if>

		<form action="edit" method="post">
			<br />
			<input name="updateMessageId" value="${messages.id}" id="id"
				type="hidden" /> つぶやき<br />
			<textarea name="text" cols="35" rows="5" id="description">
				<c:out value="${messages.text}" />
			</textarea>
			<input type="submit" value="更新" />
			<br />
			<a href="./">戻る</a>
		</form>

		<div class="copyright">Copyright(c)MisatoUsui</div>
	</div>
</body>
</html>