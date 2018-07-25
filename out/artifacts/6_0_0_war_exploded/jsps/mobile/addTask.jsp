<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8" />
<meta name="viewport"
	content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=0, width=device-width">
<meta http-equiv="Cache-Control" content="no-siteapp" />
<title>任务处理-UAS管理系统</title>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<base href="<%=basePath%>" />
<meta name="description"
	content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<meta name="keywords" content="USOFTCHINA.COM,ERP,SCM,CRM,MRP,企业管理,优软" />
<link href="resource/weui/weui.min.css" rel="stylesheet">
<link href="resource/fontawesome/css/font-awesome.min.css" rel="stylesheet">
<link href="jsps/mobile/addTask.css" rel="stylesheet"/>
</head>
<body ontouchstart>
	<!-- container Start -->
	<div class="container js_container">
		<div class="page" id="main">
			<div class="bd">
				<div class="weui_cells weui_cells_form">
					<div class="weui_cell">
						<div class="weui_cell_hd">
							<label class="weui_label">任务名称&nbsp;</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							<input name="name" type="text" class="weui_input" placeholder="请输入任务名称" />
						</div>
					</div>
					<div class="weui_cell weui_cell_input">
						<div class="weui_cell_hd">
							<label class="weui_label">开始日期&nbsp;</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							<input name="startdate" type="datetime-local" class="weui_input" placeholder="点击选择开始时间"/>
						</div>
						<div class="weui_cell_ft"><i class="fa fa-calendar"></i></div>
					</div>
					<div class="weui_cell">
						<div class="weui_cell_hd">
							<label class="weui_label">持续时间(h)&nbsp;</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							<input name="continuetime" type="number" class="weui_input" placeholder="输入持续时间(单位：小时)" />
						</div>
					</div>
				<!-- <div class="weui_cell weui_cell_input">
						<div class="weui_cell_hd">
							<label class="weui_label">结束日期</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							<input name="enddate" type="datetime-local" class="weui_input" placeholder="点击选择结束时间"/>
						</div>
						<div class="weui_cell_ft"><i class="fa fa-calendar"></i></div>
					</div>
					 <div class="weui_cell">
						<div class="weui_cell_hd">
							<label class="weui_label">持续时间</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							<input name="duration" type="number" class="weui_input"/>
						</div>
						<div class="weui_cell_ft">小时</div>
					</div> -->
					 <div class="weui_cell weui_cell_switch">
                        <div class="weui_cell_hd weui_cell_primary">需要回复</div>
                        <div class="weui_cell_ft">
                            <input name="type" class="weui_switch" type="checkbox"/>
                        </div>
                    </div>
				</div>
				<div class="weui_cells weui_cells_form">
					<div class="weui_cell">
						<div class="weui_cell_hd">
							<label class="weui_label">处理人</label>
						</div>
						<div class="weui_cell_bd weui_cell_primary">
							已指定<span class="checkedSize">0</span>个任务处理人
						</div>
						<div class="weui_cell_ft openUserTab">
							<i class="fa fa-plus-circle fa-lg"></i>
						</div>
					</div>
					<div class="weui_cell checkedUsers">
		                <div class="weui_cell_bd weui_cell_primary">
		                	<!--  <button class="weui_btn weui_btn_mini weui_btn_warn" id="defaultName"><i class="fa fa-close"></i></button> -->	
		                </div>
		            </div>
				</div>
				<div class="weui_cells_title">任务描述</div>
				<div class="weui_cells weui_cells_form">
					<div class="weui_cell">
						<div class="weui_cell_bd weui_cell_primary">
							<textarea rows="3" name="description" class="weui_textarea" placeholder="为了让处理人更清楚，请输入任务的描述"></textarea>
						</div>
					</div>
				</div>
				<div class="weui_btn_area">
                    <a class="weui_btn weui_btn_primary" id="commit">确定</a>
                </div>
			</div>
			
			<div class="ft">
				&nbsp;&nbsp;
			</div>
		</div>
		
		<!-- userTab Start -->
		<div class="page userTab" id="userTab">
			<div class="bd">
				<div class="weui_cells header">
					<div class="weui_cell">
						<div class="weui_cell_hd closeUserTab">
							<i class="fa fa-arrow-left"></i>
						</div>
						<div class="weui_cell_bd weui_cell_primary">选择任务指定人</div>
						<div class="weui_cell_ft closeUserTab">
							<button class="weui_btn weui_btn_mini weui_btn_primary check">确定</button>
						</div>
					</div>
				</div>
				<div>
					<div class="weui_cells_title">
						已指定<span class="checkedSize">0</span>个任务指定人
					</div>
					<div class="weui_cells">
						<div class="weui_cell checkedUsers">
							<div class="weui_cell_bd weui_cell_primary">
							<!-- <button class="weui_btn weui_btn_mini weui_btn_warn" id="defaultName"><i class="fa fa-close"></i></button> -->    	
							</div>
						</div>
					</div>
				</div>
				<div class="weui_cells">
					<div class="weui_cell">
						<div class="weui_cell_bd weui_cell_primary">
							<input id="search_input" type="search" class="weui_input"
								placeholder="搜索" />
						</div>
						<div id="search_icon" class="weui_cell_ft">
							<i class="fa fa-search"></i>
						</div>
					</div>
				</div>
				<div id="searchResult">
					<div class="weui_cells_title">请输入指定人姓名关键词搜索</div>
					<div class="weui_cells weui_cells_checkbox">
						<!-- <label class="weui_cell weui_check_label" for="A001">
	                        <div class="weui_cell_bd weui_cell_primary">
	                            <p>
	                            	<i class="fa fa-user"></i> A001 <span>[B2B平台开发科]</span>
	                            </p>
	                        </div>
	                        <div class="weui_cell_ft">
	                            <input type="checkbox" class="weui_check" name="A001" id="A001">
	                            <i class="weui_icon_checked"></i>
	                        </div>
	                    </label> -->
					</div>
				</div>
				<div class="uas"><span id="firstspan" class="active">UAS系统<i class="fa fa-caret-right"></i></span></div>
				<div class="weui_cells weui_cells_access firstdepto">
				
				</div>
			</div>
		</div>
		<!-- userTab End -->
		
		<!-- success message Start -->
		<div class="page" id="successPage">
            <div class="weui_msg">
                <div class="weui_icon_area"><i class="weui_icon_success weui_icon_msg"></i></div>
                <div class="weui_text_area">
                    <h2 class="weui_msg_title">添加任务成功</h2>
                    <p class="weui_msg_desc">任务名称：<span class="message-content msg_title"></span></p>
                    <p class="weui_msg_desc">开始时间：<span class="message-content msg_startdate"></span></p>
                    <p class="weui_msg_desc">持续时间(h)：<span class="message-content msg_continuetime"></span></p>
                    <p class="weui_msg_desc">处理人：<span class="message-content msg_resourcename"></span></p>
                    <p class="weui_msg_desc">需要回复：<span class="message-content msg_type"></span></p>
                </div>
                <div class="weui_opr_area">
                    <p class="weui_btn_area">
                    </p>
                </div>
                <div class="weui_extra_area">
                    <span>请关闭页面返回上一层</span>
                </div>
            </div>
        </div>
		<!-- success message End -->
		
		<!-- loading Start-->
	    <div id="loadingToast" class="weui_loading_toast" ng-if="loading">
	        <div class="weui_mask_transparent"></div>
	        <div class="weui_toast">
	            <div class="weui_loading">
	                <div class="weui_loading_leaf weui_loading_leaf_0"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_1"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_2"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_3"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_4"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_5"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_6"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_7"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_8"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_9"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_10"></div>
	                <div class="weui_loading_leaf weui_loading_leaf_11"></div>
	            </div>
	            <p class="weui_toast_content">数据加载中</p>
	        </div>
	    </div>
		<!-- loading End -->
		
		<!-- dialog Start -->
        <div class="weui_dialog_alert" id="dialog" style="display: none;">
            <div class="weui_mask"></div>
            <div class="weui_dialog">
                <div class="weui_dialog_hd"><strong class="weui_dialog_title">提示</strong></div>
                <div class="weui_dialog_bd"></div>
                <div class="weui_dialog_ft">
                    <a class="weui_btn_dialog primary">确定</a>
                </div>
            </div>
        </div>
        <!-- dialog End -->
		
	</div>
	<!-- container End -->
		
</body>
<script>
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
<script type="text/javascript" src="resource/zepto/zepto.min.js"></script>
<script type="text/javascript" src="jsps/mobile/addTask.js"></script>
</html>