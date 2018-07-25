<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//
EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<html>
	<head>
		<meta http-equiv="Content-type" content="text/html;charset=utf-8">
		<style type="text/css">
			.s1{
				color: blue;			
			}
		</style>
	</head>
	<body>
		<div id="wrap">
				<div id="content" style="height: 1000px;">
					<p id="whereami"></p>
					<form action="" method="post" id="form1">
						<table cellpadding="0" cellspacing="0" border="0" class="form_table">
							<tr>
								<td valign="middle" align="right">企业名称:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" id="en_Name" name="en_Name" onblur="checkName();"/>
								</td>
								<td><span id="en_Name_err" class="s1">&nbsp;*请输入企业在工商局注册时使用的名称</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">简&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Shortname" id="en_Shortname"/>
								</td>
								<td><span id="en_Shortname_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">英文名称:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Name_En" id="en_Name_En"/>
								</td>
								<td><span id="en_Name_En_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">企业类型:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Type" id="en_Type"/>
								</td>
								<td><span id="en_Type_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">公司法人:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Corporation" id="en_Corporation"/>
								</td>
								<td><span id="en_Corporation_err" class="s1">*请输入企业在工商局注册时的法人</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">商业登记证号:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" name="en_Businesscode" id="en_Businesscode"/>
								</td>
								<td><span id="en_Businesscode_err" class="s1">*请输入企业在工商局注册时得到的编号</span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" id="en_Tel" name="en_Tel" />
								</td>
								<td><span id="en_Tel_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">传&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;真:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" id="en_Fax" name="en_Fax" />
								</td>
								<td><span id="en_Fax_err" class="s1"></span></td>
							</tr>
							<tr>
								<td valign="middle" align="right">默认送货地址:</td>
								<td valign="middle" align="left">
									<input type="text" class="inputgri" id="en_Deliveraddr" name="en_Deliveraddr" />
								</td>
								<td><span id="en_Deliveraddr_err" class="s1"></span></td>
							</tr>
						</table>
						<p style="padding-left: 20%;">
							<input type="button" class="button" value="下一步 &raquo;" onclick="javascript:toggleTo(2);"/>
						</p>
					</form>
				</div>
		</div>
	</body>
</html>