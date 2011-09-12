<%@page pageEncoding="UTF-8" isELIgnored="false" session="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>slim3bbs</title>
<link href="/css/bbs.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<div id="page">
		<h1>slim3bbs</h1>
		<hr>
		<div class="err">${errors.message}</div>
		<form method="post" action="deleteEntry" style="display: inline" onsubmit="return confirm('削除しますか？')">
			<input type="hidden" ${f:hidden("key")}/>
			<input type="hidden" ${f:hidden("password")}/>
			<input type="submit" value="削除" class="button" />
		</form>
		<hr>
		<form method="post" action="updateEntry">
			<input type="hidden" ${f:hidden("key)}/>
			<input type="hidden" ${f:hidden("password")}/>
			<table>
				<thead>
					<tr>
						<td colspan="2">記事の修正</td>						
					</tr>
				</thead>
				<tbody>
					<tr>
						<t
					</tr>
				</tbody>
			</table>
	</div>
</body>
</html>
