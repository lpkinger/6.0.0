<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//
EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<style>
			table{
				font-size:14px;
			}
			.td1{
				color:rgb(244,134,125);
			}
			
			.h2{
				background-color:#F0F0F0;
			}
			#m1{
				width:100px;
				height:30px;
				font-size:15px;
				font-weight:900;
				background-color:#FFEC8B;
			}
		</style>
	</head>
	<body>
		<div id="wrap">
			<div id="content" style="height: 1000px;">
					<p id="whereami">
					</p>
					<h2 class="h2">
						基本资料:<a href="javascript:toggleTo(1);">修改</a>
					</h2>
					<table  width="60%">
						<tr>
							<td class="td1">企业名称:</td>
							<td id="en_Name_td"></td>
							<td class="td1">简&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;称</td>
							<td id="en_Shortname_td"></td>
						</tr>
						<tr>
							<td class="td1">英文名称:</td>
							<td id="en_Name_En_td"></td>
							<td class="td1">企业类型:</td>
							<td id="en_Type_td"></td>
						</tr>
						<tr>
							<td class="td1">公司法人:</td>
							<td id="en_Corporation_td">
							</td>
							<td class="td1">商业登记证号:</td>
							<td id="en_Businesscode_td">
							</td>
						</tr>
						<tr>
							<td class="td1">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话:</td>
							<td id="en_Tel_td">
							</td>
							<td class="td1">传&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;真:</td>
							<td id="en_Fax_td">
							</td>
						</tr>
						<tr>
							<td class="td1">默认送货地址:</td>
							<td id="en_Deliveraddr_td">
							</td>
						</tr>
					</table>
					<br/>
					<h2 class="h2">
						其他信息:<a href="javascript:toggleTo(2);">修改</a>
					</h2>
					<table width="60%">
						<tr>
							<td class="td1">注册地址:</td>
							<td id="en_Address_td"></td>
							<td class="td1">注册资本</td>
							<td id="en_Registercapital_td"></td>
						</tr>
						<tr>
							<td class="td1">公司网址:</td>
							<td id="en_Url_td"></td>
							<td class="td1">纳税人识别人:</td>
							<td id="en_Taxcode_td">
							</td>
						</tr>
						<tr>
							<td class="td1">注册时间:</td>
							<td id="en_Time_td">
							</td>
							<td class="td1">管理员名:</td>
							<td id="en_Admin_td">
							</td>
						</tr>
						<tr>
							<td class="td1">管理员电话:</td>
							<td id="en_Adminphone_td">
							</td>
							<td class="td1">管理员邮箱:</td>
							<td id="en_Email_td">
							</td>
						</tr>
						<tr>
							<td class="td1">附件:</td>
							<td id="en_Attachment_td">
							</td>
						</tr>
						<tr></tr>
						<tr>
							<td><a href="javascript:;">优软软件注册协议</a></td>
						</tr>
						<tr>
							<td><input type="checkbox" checked="checked" id="check"/>同意优软软件注册协议</td>
						</tr>
					</table>
					<p style="padding-left: 20%;">
							<input type="button" class="button" value="确认并注册&raquo;" onclick="javascript:register();"/>
					</p>
			</div>
		</div>
	</body>
</html>