<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<meta name="description"
	content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta name="keywords" content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<link href="<%=basePath%>resource/bootstrap/bootstrap.min.css"
	rel="stylesheet">
<link href="<%=basePath%>resource/bootstrap/bootstrap-datetimepicker.min.css"
	rel="stylesheet">
<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="customervisit.css" type="text/css" />
<title>客户拜访记录-UAS管理系统</title>
<script type="text/javascript">
	var username = '<%=session.getAttribute("em_name")%>';
	var em_code = '<%=session.getAttribute("em_code")%>';
	var em_depart ='<%=session.getAttribute("em_depart")%>';
	var em_master = '<%=session.getAttribute("em_master")%>';
	var basePath = (function() {
		var fullPath = window.document.location.href;
		var path = window.document.location.pathname;
		var subpos = fullPath.indexOf('//');
		var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
		if (subpos > -1)
			fullPath = fullPath.substring(subpos + 2);
		var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
		sname = (['/jsps','/workfloweditor','/resource','/system','/process','/demo','/exam','/oa','/opensys','/mobile'].indexOf(sname) > -1 ? '/' : sname);
		return subpath + fullPath.substring(0, pos) + sname + (sname == '/' ? '' : '/');
	})();
</script>
</head>
<body>
	<!-- top start -->
	<div id="top" class="container">
		<div class="top-sysname">
			<a href="<%=basePath%>"><span>优软UAS</span>&nbsp;&nbsp;<span style="font-size:15px; font-weight:normal">移动版</span></a>
		</div>
		<div class="top-sysrelation">
			<span class="glyphicon glyphicon-user"></span>&nbsp;<span id="username">管理员</span>&nbsp;|&nbsp;
			<a href="<%=basePath%>"><span class="glyphicon glyphicon-home">				
				</span>&nbsp;<span>首页</span>
			</a>
		</div>
	</div>
	<!-- top end -->
	<!-- main start -->
	<div id="mian" class="container">
		<div id="title">
			<span class="glyphicon glyphicon-pencil"></span>&nbsp;<span id="handler_name">新建客户拜访记录</span>
			<div class="list-index"><a href="#">列表<span class="glyphicon glyphicon-list-alt"></span></a></div>
		</div>
		<!-- 基本信息 start -->
		<div class="baseinfo-title">
			<span class="glyphicon glyphicon-tasks"></span><span>&nbsp;基本信息</span>
			
		</div>
		<div id="baseinfo">
			<form class="form-horizontal" id="formStore">
				<div class="control-group">
					<label class="control-label text-right" for="vr_code">单据编号：</label>
					<input type="text" id="vr_code" name="vr_code" class="form-control">
					<div class="checkbox has-success form-remark">
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_type">拜访类型：</label>
					<select type="text" id="vr_type" name="vr_type" class="form-control">
						
					</select>
					<div class="form-remark">
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_way">拜访方式：</label>
					<select type="text" id="vr_way" name="vr_way" class="form-control">
						
					</select>
					<div class="form-remark">
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_visittime">拜访时间：</label>
					<div class="input-append date" id="datetimepicker" date-format="yyyy-MM-dd hh:mm:ss">
					    <input class="form-control" type="text" id="vr_visittime" name="vr_visittime" date-format="yyyy-MM-dd hh:mm:ss" >
					    <span class="add-on glyphicon glyphicon-calendar" style="padding-left:5px;"></span>
				    </div>
					<div class="form-remark">
						<button type="button" id="settime_now" class="btn btn-primary btn-xs">NOW</button>
					</div>
				</div>
				<div class="control-group dropdown">
					<label class="control-label text-right" for="vr_cuuu">客户编号：</label>
					<input type="text" id="vr_cuuu" name="vr_cuuu" class="form-control" 
						placeholder="输入编号关键字点击按钮搜索">
					<div class="form-remark" data-toggle="dropdown">
						<button id="cuuuDb" type="button" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-search"></span></button>
					</div>
					<div id="suggest_cuuu" class="dropdown-menu suggest-box">
						<div class="">
							<ul class="suggest-result">
								
							</ul>
						</div>
					</div>
				</div>
				<div class="control-group dropdown">
					<label class="control-label text-right" for="vr_cuname">客户名称：</label>
					<input type="text" id="vr_cuname" name="vr_cuname" class="form-control" 
						placeholder="输入名称关键字点击按钮搜索">
					<div class="form-remark" data-toggle="dropdown">
						<button id="cunameDb" type="button" class="btn btn-primary btn-xs"><span class="glyphicon glyphicon-search"></span></button>
					</div>
					<div id="suggest_cuname" class="dropdown-menu suggest-box" style="width:60%;">
						<div class="">
							<ul class="suggest-result">
								
							</ul>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_title">拜访主题：</label>
					<input type="text" id="vr_title" name="vr_title" class="form-control">
					<div class="form-remark">
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_visitplace">拜访地点：</label>
					<input type="text" id="vr_visitplace" name="vr_visitplace" class="form-control">
					<div class="form-remark">
						<button id="location" type="button" class="btn btn-primary btn-xs">定位</button>
						<div id="location_map" style="dispaly:inline-block;width:0px;height:0px;"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_defaultorname">所属组织：</label>
					<input type="text" id="vr_defaultorname" name="vr_defaultorname" class="form-control">
					<div class="form-remark">
						
					</div>
				</div>
				<div class="control-group">
					<label class="control-label text-right" for="vr_detail" style="display:block;">拜访内容：</label>
					<textarea type="text" id="vr_detail" name="vr_detail" style="height:auto;" class="form-control form-textarea" rows=6></textarea>
					<div class="form-remark">
						
					</div>
				</div>

			</form>
		</div>
		<!-- 基本信息 end -->
		<!-- 拜访人员 start -->
		<div class="de-title">
			<span class="glyphicon glyphicon-user"></span><span>&nbsp;拜访人员</span>
			<div class="add-perpeo" id="add_visit_perpeo">
				<span class="glyphicon glyphicon-plus"></span>
			</div>
		</div>
		<div id="visit_perpeo">
			<form id="pl">
			<table class="table">
				<tr><th width="30%">姓名</th><th width="50%">职务</th><th width="10%"></th></tr>
				<tr class="pl">
					<td width="30%">
						<div><input name="pl_name" class="form-control" type="text"></div>
					</td>
					<td width="50%">
						<div><input name="pl_position" class="form-control" type="text"></div>
					</td>
					<td with="10%"></td>
				</tr>
				<tr class="pl">
					<td width="30%">
						<div><input name="pl_name" class="form-control" type="text"></div>
					</td>
					<td width="50%">
						<div><input name="pl_position" class="form-control" type="text"></div>
					</td>
					<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>
				</tr>
			</table>
			</form>
		</div>
		<!-- 拜访人员 end -->
		<!-- 洽谈对象 start -->
		<div  class="de-title">
			<span class="glyphicon glyphicon-link"></span><span>&nbsp;洽谈对象</span>
			<div class="add-perpeo" id="add_customer_perpeo">
				<span class="glyphicon glyphicon-plus"></span>
			</div>
		</div>
		<div id="customer_perpeo">
			<form id="cup">
			<table class="table">
				<tr><th width="20%">姓名</th><th width="30%">职务</th><th width="40%">联系电话</th><th width="10%"></th></tr>
				<tr class="cup">
					<td width="20%">
						<div><input name="cup_name" class="form-control" type="text"></div>
					</td>
					<td width="30%">
						<div><input name="cup_position" class="form-control" type="text"></div>
					</td>
					<td width="40%"><div><input name="cup_tel" class="form-control" type="number"></div></td>
					<td with="10%"></td>
				</tr>
				<tr class="cup">
					<td width="20%">
						<div><input name="cup_name" class="form-control" type="text"></div>
					</td>
					<td width="30%">
						<div><input name="cup_position" class="form-control" type="text"></div>
					</td>
					<td width="40%"><div><input name="cup_tel" class="form-control" type="number"></div></td>
					<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>
				</tr>
			</table>
			</form>
		</div>
		<!-- 恰谈对象 end -->
		<!-- 推广项目信息 start -->
		<div  class="de-title">
			<span class="glyphicon glyphicon-share"></span><span>&nbsp;推广项目信息</span>
			<div class="add-perpeo" id="add_productInfo">
				<span class="glyphicon glyphicon-plus"></span>
			</div>
		</div>
		<div id="productInfo">
			<form id="pi">
			<table class="table">
				<tr>
					<th width="30%">项目名称</th><th width="30%">推广品牌</th>
					<th width="0%"  style="display:none;">推广产品型号</th><th width="30%">推广进度</th><th width="10%"></th>
				</tr>
				<tr class="pi">
					<td width="30%">
						<div><input name="pi_prodname" class="form-control" type="text" readonly placeholder="点击选择" ></div>
					</td>
					<td width="30%">
						<div><input name="pi_brand" class="form-control" type="text" readonly></div>
					</td>
					<td width="0%"  style="display:none;">
						<div><input name="pi_model" class="form-control" type="text" readonly></div>
					</td>
					<td width="30%">
						<div>
							<select name="pi_projprogress" class="form-control" type="number">
								<option value="初次推广">初次推广</option>
								<option value="报价">报价</option>
								<option value="送样">送样</option>
								<option value="样品验证">样品验证</option>
								<option value="量产">量产</option>
								<option value="结案">结案</option>
							</select>
						</div>
					</td>
					<td with="10%"></td>
				</tr>
				<tr class="pi">
					<td width="30%">
						<div><input name="pi_prodname" class="form-control" type="text" readonly placeholder="点击选择"></div>
					</td>
					<td width="30%">
						<div><input name="pi_brand" class="form-control" type="text" readonly></div>
					</td>
					<td width="0%" style="display:none;">
						<div><input name="pi_model" class="form-control" type="text" readonly></div>
					</td>
					<td width="30%">
						<div>
							<select name="pi_projprogress" class="form-control" type="number">
								<option value="初次推广">初次推广</option>
								<option value="报价">报价</option>
								<option value="送样">送样</option>
								<option value="样品验证">样品验证</option>
								<option value="量产">量产</option>
								<option value="结案">结案</option>
							</select>
						</div>
					</td>
					<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>
				</tr>
			</table>
			</form>
		</div>
		<!-- 推广项目信息 end -->
		<!-- 费用报销 start -->
		<div  class="de-title">
			<span class="glyphicon glyphicon-usd"></span><span>&nbsp;费用报销</span>
			<div class="add-perpeo" id="add_feedBack">
				<span class="glyphicon glyphicon-plus"></span>
			</div>
		</div>
		<div id="feedBack">
			<form id="fb">
			<table class="table">
				<tr><th width="35%">费用用途</th><th width="20%">金额</th><th width="35%">备注</th><th width="10%"></th></tr>
				<tr class="fb">
					<td width="35%">
						<div><input name="vrd_d1" class="form-control" type="text" readonly placeholder="点击选择"></div>
					</td>
					<td width="20%">
						<div><input name="vrd_n7" class="form-control" type="number"></div>
					</td>
					<td width="35%"><div><input name="vrd_d3" class="form-control" type="text"></div></td>
					<td with="10%"></td>
				</tr>
				<tr class="fb">
					<td width="35%">
						<div><input name="vrd_d1" class="form-control" type="text" readonly placeholder="点击选择"></div>
					</td>
					<td width="20%">
						<div><input name="vrd_n7" class="form-control" type="number"></div>
					</td>
					<td width="35%"><div><input name="vrd_d3" class="form-control" type="text"></div></td>
					<td with="10%"><span style="color:#FF6600;" class="glyphicon glyphicon-remove"></span></td>
				</tr>
			</table>
			</form>
		</div>
		<!-- 费用报销 end -->
		<!-- 操作按钮 start -->
		<div id="buttons">
			<div class="buttons-item">
				<button id="btn_cancel" type="button" class="btn btn-default">重置</button>
			</div>
			<div class="buttons-item">
				<button id="btn_save" type="button" class="btn btn-success">保存</button>
			</div>
		</div>
		<!-- 操作按钮 end -->
	</div>
	<!-- main end -->
	<!-- footer start -->
	<div id="footer" class="container text-center">
		<div>深圳市优软科技有限公司</div>
		<div>Copyright @ 2012 All Rights Reserved</div>
	</div>
	<!-- footer end -->

	<!-- modal -->
	<div class="modal-backdrop in" style="z-index: 9998; display: none"></div>
	<div class="modal in" id="dialog" style="z-index: 9999; display: none">
		<div class="modal-dialog modal-sm" style="height:100%; padding-top:10%;">
			<div class="modal-content">
				<div class="modal-header text-center">
					<strong class="modal-title text-lg"></strong>
					<!-- <button type="button" class="pull-right btn btn-xs btn-default"
						onclick="closePage()">直接退出</button> -->
				</div>
				<div class="modal-body text-center"></div>
			</div>
		</div>
	</div>
	<!-- loading -->
	<div class="loading-container" style="display:none;">请稍等...</div>

	<!-- DbFind 模态框 start -->
	<div class="modal" id="dbFindModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	  <div class="modal-dialog">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
	        <h4 class="modal-title" id="myModalLabel">选择项目信息</h4>
	      </div>
	      <div class="modal-body">
	        <!-- 待加载... -->
	      </div>
	      <div class="modal-footer">
	        
	      </div>
	    </div>
	  </div>
	</div>
	<!-- DbFind 模态框 ned -->

	<script type="text/javascript"
		src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>resource/bootstrap/bootstrap.min.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>resource/bootstrap/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>resource/bootstrap/bootstrap-datetimepicker.zh-CN.js"></script>
	<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=FbfVB7Q1uZqja0w6s8PIfgyG"></script>
	<script type="text/javascript" src="customervisit.js"></script>
</body>

</html>