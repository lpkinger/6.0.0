<%@ page contentType="text/html; charset=UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<title>异常操作</title>
<style>
body {
	background-color: #f1f3f6;
	color: #555;
    font: 400 1em/1.45 Helvetica Neue,Helvetica,Arial,Hiragino Sans GB,STXihei,STHeiti,Microsoft YaHei,SimHei,sans-serif;
}
.x-container {
	width: 990px;
	margin: 30px auto
}
.x-well {
    border: 1px solid #e4e7ed;
    border-bottom-width: 0;
    border-radius: 4px;
    box-shadow: 0 1px 4px 0 rgba(204,209,217,.3);
}
.x-well .x-title{
	padding: 24px 30px 20px;
    border-bottom: 1px solid #e4e7ed;
    border-radius: 4px 4px 0 0;
    background-color: #f5f7fa;
    box-shadow: inset 0 1px 0 0 rgba(255,255,255,.6);
}
.x-well .x-block{
	font-size: 14px;
    padding: 25px 30px;
    border-bottom: 1px solid #e4e7ed;
    background-color: #fff;
}
ul {
	list-style: none;
}
ul li {
	padding: 10px 20px
}
.x-btn {
    display: inline-block;
    font-weight: 400;
    border: 1px solid transparent;
    outline: none;
    background-image: none;
    cursor: pointer;
    -webkit-user-select: none;
    user-select: none;
    text-align: center;
    text-decoration: none;
    vertical-align: middle;
    white-space: nowrap;
    box-sizing: border-box;
    padding: 8px 17px;
    font-size: 14px;
    line-height: 1;
    border-radius: 4px
}
.x-btn-default {
	color: #fff;
    border-color: #217ef2;
    background-color: #3890ff;
    background-image: -webkit-gradient(linear,left bottom,left top,from(hsla(0,0%,100%,.06)),to(hsla(0,0%,100%,.06))),-webkit-gradient(linear,left bottom,left top,from(rgba(9,109,236,.5)),to(rgba(76,155,255,.5)));
    background-image: -webkit-linear-gradient(bottom,hsla(0,0%,100%,.06),hsla(0,0%,100%,.06)),-webkit-linear-gradient(bottom,rgba(9,109,236,.5),rgba(76,155,255,.5));
    background-image: linear-gradient(0deg,hsla(0,0%,100%,.06) 0,hsla(0,0%,100%,.06)),linear-gradient(0deg,rgba(9,109,236,.5) 0,rgba(76,155,255,.5));
    box-shadow: inset 0 1px 0 hsla(0,0%,100%,.08), 0 1px 1px rgba(0,0,0,.08);
    text-shadow: 0 -1px 0 rgba(0,0,0,.1)
}
.x-btn-default:hover {
    background-image: -webkit-gradient(linear,left bottom,left top,from(rgba(9,109,236,.5)),to(rgba(76,155,255,.5)));
    background-image: -webkit-linear-gradient(bottom,rgba(9,109,236,.5),rgba(76,155,255,.5));
    background-image: linear-gradient(0deg,rgba(9,109,236,.5) 0,rgba(76,155,255,.5));
}
</style>
</head>
<body>
	<div class="x-container">
		<div class="x-well">
			<div class="x-title">出现异常</div>
			<div class="x-block">
				<p>${exceptionInfo}</p>
			</div>
			<div class="x-block">
				<p>您可以
				<ul>
					<li><a class="x-btn x-btn-default" href="javascript:window.location.reload();">刷新试试</a></li>
					<li><a class="x-btn x-btn-default" href="<%=basePath%>">重新登录</a></li>
				</ul>
				</p>
			</div>
		</div>
	</div>
</body>
</html>