<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>slim3bbs</title>
<link href="/css/bbs.css" rel="stylesheet" type="text/css">
</head>
<body>
	<div id="page">
		<h1>slim3bbs</h1>
		<div class="err">${errors.message}</div>
		<table>
			<thead>
				<tr>
					<td>
						<div class="read_subject">${f:h(head.subject)}</div>
					</td>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td>
						<div class="read_header">
							${f:h(head.username)}さん（<fmt:formatDate value="${head.postDate}" pattern="yyyy/MM/dd HH:mm:ss" />）
						</div>
						<hr>
						<div class="read_body">
							${f:br(f:h(body.text))}
						</div>
						<div class="read_footer">
							<span class="err">${errors.password}</span>
							<form method="post" action="edit" style="display:inline">
								<input type="hidden" name="key" value="${f:h(head.key)}">
								<input type="password" ${f:text("password")} class="password ${f:errorClass("password","err")}">
								<input type="submit" value="修正" class="button">
							</form>
						</div>
					</td>
				</tr>
			</tbody>
		</table>
		<hr class="separate_entry".>
		<c:forEach var="c" items="${commentList}" varStatus="cs">
			<table>
				<tbody>
					<tr>
						<td>
							<div class="read_header">
								No.${f:h(c.key.id)} : ${f:h(c.username)} さん (<fmt:formatDate value="${c.postDate}" pattern="yyyy/MM/dd HH:mm:ss" />)
							</div>
							<hr>
							<div class="read_body">
								${f:br(f:h(c.comment))}
							</div>
						</td>
					</tr>
				</tbody>
			</table>
			<div>&nbsp;</div>
		</c:forEach>
		<div class="err">${errors.post}</div>
		<form method="post" action="postComment" style="display: inline">
			<input type="hidden" name="key" value="${f:h(head.key)}">
			<table>
				<thead>
					<tr>
						<td colspan="2">
							コメントの投稿
						</td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td class="label">お名前</td>
						<td class="elem">
							<input type="text" ${f:text("username")} class="normal ${f:errorClass("username","err")}">
							<span class="err">${errors.username}</span>
						</td>
					</tr>
					<tr>
						<td>コメント</td>
						<td>
							<textarea name="comment" class="smalltext ${f:errorClass("comment","err")}">${f:h(comment)}</textarea>
							<div class="err">${errors.comment}</div>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<input type="submit" value="投稿する" class="button">
						</td>
					</tr>
				</tbody>
			</table>
		</form>
		<p><a href="index">戻る</a></p>
	</div>
</body>
</html>
