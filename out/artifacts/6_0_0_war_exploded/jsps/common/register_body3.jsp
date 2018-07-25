<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//
EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<html>
	<head>
		<meta http-equiv="Content-type" content="text/html;charset=utf-8">
	</head>
	<body style="">
		<div id="wrap">
					<div id="content" style="height: 1000px;">
					<p id="whereami"></p>
					<form action="note.do" method="post" name="form1">
						<table cellpadding="0" cellspacing="0">
							<tr>
								<td><h4>第一步：查看您的电子邮箱</h4></td>
							</tr>
							<tr>
								<td>
									我们给您发送了验证邮件，邮件地址为：<a href="#" style="color:red;">${user.email}</a>
									<span class="red"><span id="lblEmail">&nbsp;${user.nickname}</span></span>
									<span class="t1">请登录您的邮箱收信。</span><br/><br/><br/>
								</td>
							</tr>
							<tr>
								<td><h4>第二步：输入验证码</h4></td>
							</tr>
							<tr>
								<td>
									<span>输入您收到邮件中的验证码：</span>
									<input name="code" type="text" id="validatecode" class="yzm_text" value='${user.email_verify_code}-${user.id}'/>
									<input type="button" class="button" value="确认 &raquo;" onclick="javascript:toggleTo(4);"/>
									<span id="errorMsg" class="no_right">${error}</span>
								</td>
							</tr>
						</table>
					</form>
				</div>
		</div>
	</body>
</html>