<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>簡易Twitter</title>
<link href="./css/style.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div class="main-contents">
		<div class="header">
			<c:if test="${ empty loginUser }">
				<a href="login">ログイン</a>
				<a href="signup">登録する</a>
			</c:if>

			<c:if test="${ not empty loginUser }">
				<div class="profile">
					<div class="name">
						<h2>
							<c:out value="${loginUser.name}" />
						</h2>
					</div>
					<div class="account">
						@
						<c:out value="${loginUser.account}" />
					</div>
					<div class="description">
						<c:out value="${loginUser.description}" />
					</div>
				</div>
			</c:if>

			<c:if test="${ not empty loginUser }">
				<a href="./">ホーム</a>
				<a href="setting">設定</a>
				<a href="logout">ログアウト</a>
			</c:if>
		</div>

		<c:if test="${ not empty errorMessages }">
			<div class="errorMessages">
				<ul>
					<c:forEach items="${errorMessages}" var="errorMessage">
						<li><c:out value="${errorMessage}" />
					</c:forEach>
				</ul>
			</div>
			<c:remove var="errorMessages" scope="session" />
		</c:if>

		<div class="date">
			日付
			<form action="./" method="get">
				<input type="date" value="${start}" name="start"/>
				～
				<input type="date" value="${end}" name="end"/>
				<input type="submit" value="絞込" />
			</form>
		</div>

		<div class="form-area">
			<c:if test="${ isShowMessageForm }">
				<form action="message" method="post">
					いま、どうしてる？<br />
					<textarea name="text" cols="100" rows="5" class="tweet-box">
					</textarea>
					<br />
					<input type="submit" value="つぶやく"/>（140文字まで）
				</form>
			</c:if>
		</div>

		<div class="messages">
			<c:forEach items="${messages}" var="message">
				<div class="message">
					<div class="account-name">
						<span class="account">
						<a href="./?user_id=<c:out value="${message.userId}"/> ">
							<c:out value="${message.account}" />
						</a>
						</span>
						<span class="name"><c:out value="${message.name}" />
						</span>
					</div>
				</div>

				<div class="text">
					<pre>
					<c:out value="${message.text}" />
					</pre>
				</div>

				<div class="date">
					<fmt:formatDate value="${message.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
				</div>
				<c:if test="${loginUser.id == message.userId }">
					<div class="buttonArea">
						<form action="delete" method="post">
						<input type="hidden" value="${message.id}" name="deleteMessageId" />
						<input type="submit" value="削除" />
						</form>
						<form action="edit" method="get">
						<input type="hidden" value="${message.id}" name="updateMessageId" />
						<input type="submit" value="編集" />
						</form>
					</div>
				</c:if>

				<div class="comment">
				<c:forEach items="${comments}" var="comment">
					<!-- 条件式：messageのidと、commentsのmessage_idが一緒だったら -->
					<c:if test="${ message.id == comment.messageId }">
					<!-- なにを繰り返すのか：アカウント名、名前、返信内容、返信日時を表示するのを -->
						<div class="account-name">
							<span class="account">
							<!-- <a hreでリンクをつくる。どうやって飛ぶか？ = 返信した人のuser_idで -->
							<a href="./?user_id=<c:out value="${comment.userId}"/> ">
								<c:out value="${user.account}" />
							</a>
							</span>
							<span class="name">
								<c:out value="${comment.name}" />
							</span>
						</div>
					<div class="text">
						<pre>
							<c:out value="${comment.text}" />
						</pre>
					</div>
					<div class="date">
						<fmt:formatDate value="${comment.createdDate}" pattern="yyyy/MM/dd HH:mm:ss" />
					</div>
					</c:if>
				</c:forEach>
				</div>

				<div class="comment-form-area">
				<c:if test="${ not empty loginUser }">
					<form action="comment" method="post">
					<input type="hidden" value="${message.id}" name="commentMessageId" />
					返信
					<br />
					<textarea name="text" cols="100" rows="5" class="comment-box">
					</textarea>
					<br />
					<input type="submit" value="返信" />
					</form>
				</c:if>
				</div>
			</c:forEach>
		</div>
	</div>
		<div class="copyright">Copyright(c)MisatoUsui</div>
</body>
</html>
