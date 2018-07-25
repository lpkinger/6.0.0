<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//
EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<html>
	<head>
		<meta http-equiv="Content-type" content="text/html;charset=utf-8">
	</head>
	<body>
		<div id="wrap">
				<div id="content" style="height: 1000px;">
					<p id="whereami"></p>
					<form action="" method="post" id="form2">
						<table cellpadding="0" cellspacing="0" border="0"
							class="form_table">
							<tr>
								<td valign="middle" align="right">注册地址:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" id="en_Address" name="en_Address" />
								</td>
								<td><span id="en_Address_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">注册资本:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Registercapital" id="en_Registercapital"/>
								</td>
								<td><span id="en_Registercapital_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">公司网址:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Url" id="en_Url"/>
								</td>
								<td><span id="en_Url_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">纳税人识别人:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Taxcode" id="en_Taxcode"/>
								</td>
								<td><span id="en_Taxcode_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">注册时间:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Time" id="en_Time"/>
								</td>
								<td><span id="en_Time_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">管理员名:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Admin" id="en_Admin"/>
								</td>
								<td><span id="en_Admin_err" class="s1">&nbsp;*管理员联系资料非常重要,请认真填写</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">管理员电话:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Adminphone" id="en_Adminphone"/>
								</td>
								<td><span id="en_Adminphone_err" class="s1">&nbsp;*请输入管理员移动电话号</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">管理员邮箱:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Email" id="en_Email"/>
								</td>
								<td><span id="en_Email_err" class="s1">&nbsp;*请输入正确邮箱格式</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">附件:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Attachment" id="en_Attachment"/>
								</td>
								<td><span id="en_Attachment_err" class="s1"></span></td>
							</tr>
						</table>
						<p style="padding-left: 20%;">
							<input type="button" class="button" value="下一步 &raquo;" onclick="javascript:toggleTo(3);"/>
						</p>
					</form>
				</div>
		</div>
	</body>
</html>