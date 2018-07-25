<!-- 
使用说明：
路径：/jsps/mobile/commonForm/commonForm.jsp
参数：[例]?caller=Purchase&formCondition=pu_idIS50692311&gridCondition=pd_puidIS50692311&_readOnly=true
参数说明：	caller			=	要显示页面的caller
			formCondition	=	form条件，IS代替=
			gridCondition	=	grid条件，IS代替=，只支持单grid
			_readOnly		=	true(可选，页面模式为只读模式，无按钮，不可输入操作)
更新时间：suntg 2015年9月11日16:50:09
 -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
		<link href="<%=basePath%>resource/bootstrap/bootstrap.min.css"bootstrap
			rel="stylesheet">
		<link href="http://cdn.bootcss.com/bootstrap-datetimepicker/3.1.3/css/bootstrap-datetimepicker.min.css" rel="stylesheet">
		<base href="<%=basePath%>jsps/mobile/" />
		<link rel="stylesheet" href="commonForm/commonForm.css" type="text/css" />
		<title></title>
		<script type="text/javascript">
			var username = '<%=session.getAttribute("em_name")%>';
			var em_code = '<%=session.getAttribute("em_code")%>';
			var em_depart ='<%=session.getAttribute("em_depart")%>';
			var master = '<%=session.getAttribute("master")%>';
			var basePath = (function() {
				var fullPath = window.document.location.href;
				var path = window.document.location.pathname;
				var subpos = fullPath.indexOf('//');
				var subpath = subpos > -1 ? fullPath.substring(0, subpos + 2) : '';
				if (subpos > -1)
					fullPath = fullPath.substring(subpos + 2);
				var pos = fullPath.indexOf(path), sname = path.substring(0, path.substr(1).indexOf('/') + 1);
				return subpath + fullPath.substring(0, pos) + (sname == '/ERP' ? '/ERP/' : '/');
			})();
		</script>
	</head>
	<body>
	<!-- top start -->
	<!-- <div id="top" class="container">
		<div class="top-sysname">
			<a href="<%=basePath%>"><span>UAS管理系统</span></a>
		</div>
		<div class="top-sysrelation">
			<span class="glyphicon glyphicon-user"></span>&nbsp;<span id="username">管理员</span>&nbsp;|&nbsp;
			<a href="<%=basePath%>"><span class="glyphicon glyphicon-home">				
				</span>&nbsp;<span>首页</span>
			</a>
		</div>
	</div> -->
	<!-- top end -->
	<!-- main start -->
	<div id="mian" class="container">
		<div id="title">
			<span class="glyphicon glyphicon-pencil"></span>&nbsp;<span id="handler_name"></span>
		</div>
		<!-- 基本信息 start -->
		<div class="baseinfo-title" name="base_title">
			<span class="glyphicon glyphicon-tasks"></span><span>&nbsp;基本信息</span>
		</div>
		<div id="baseinfo" name="baseinfo">
			<form class="form-horizontal">
				<!-- <div>
					<div class="control-group">
						<div class="fieldlabel">
							<label class="control-label text-right" for="vr_code">单据编号：</label>
						</div>
						<div class="field">
							<input type="text" id="vr_code" name="vr_code" class="form-control">
						</div>
						<div class="fieldremark">
							
						</div>
					</div>
				</div> -->
			</form>
			<div class="spread-baseinfo text-center">
				展开查看主表信息<span class="glyphicon glyphicon-share-alt"></span>
			</div>
		</div>
		<!-- 基本信息end -->
		<!-- 明细 start -->
		<div id="details" name="details" class="hidden">
			<div class="de-title" name="detail_title">
				<span>&nbsp;明细信息</span>
			</div>
			<div id="de-form">
				<form class="form-horizontal">
					
				</form>
			</div>
			<div class="de-body">
				<div class="text-center add-detail">
					<div id="add-detail">添加明细行</div>
					<div id="detail-handlers">
						<div class="btn-group btn-group-xs btn-group-justified">
							<div class="btn-group">
								<button id="de-delete" type="button" disabled class="btn btn-default line">
									<span class="glyphicon glyphicon-trash"></span>&nbsp;删除
								</button>
							</div>
							<div class="btn-group">
								<button id="de-modify" type="button" disabled class="btn btn-default text-success line">
									<span class="glyphicon glyphicon-edit"></span>&nbsp;修改
								</button>
							</div>
							<div class="btn-group">
								<button id="de-add" type="button" class="btn btn-default text-success line">
									<span class="glyphicon glyphicon-check"></span>&nbsp;添加
								</button>
							</div>
							<div class="btn-group">
								<button id="de-cancle" type="button" class="btn btn-default text-danger line">
									<span class="glyphicon glyphicon-collapse-up"></span>&nbsp;取消
								</button>
							</div>
						</div>
					</div>
				</div>
				<div id="de-content">
					<!-- <div class="detail-sub">
						<div class="sub-order"><span class="number">1</span></div>基于 Bootstrap 的衍生品也逐渐多了起来，就连WordPress插件都有专门基于Bootstrap样式的。倡萌最近发现国人搭建的 Bootstrap中文网 -- www.bootcss.com ，对Bootstrap的文档进行了翻译整理，方便更多热爱这个CSS框架的攻城师们分享、交流自己在前端设计、开发方面的心得。
					</div>
					<div class="detail-sub">
						<div class="sub-order"><span class="number">2</span></div><span class="sub-delete"><span class="glyphicon glyphicon-remove"></span></span>基于 Bootstrap 的衍生品也逐渐多了起来，就连WordPress插件都有专门基于Bootstrap样式的。倡萌最近发现国人搭建的 Bootstrap中文网 -- www.bootcss.com ，对Bootstrap的文档进行了翻译整理，方便更多热爱这个CSS框架的攻城师们分享、交流自己在前端设计、开发方面的心得。
					</div>
					<div class="detail-sub">
						<div class="sub-order"><span class="number">3</span></div><span class="sub-delete"><span class="glyphicon glyphicon-remove"></span></span>基于 Bootstrap 的衍生品也逐渐多了起来，就连WordPress插件都有专门基于Bootstrap样式的。倡萌最近发现国人搭建的 Bootstrap中文网 -- www.bootcss.com ，对Bootstrap的文档进行了翻译整理，方便更多热爱这个CSS框架的攻城师们分享、交流自己在前端设计、开发方面的心得。
					</div> -->
				</div>
			</div>
		</div>
		<!-- 明细 end -->
		<!-- 按钮组 start-->
		<div id="buttons">
			<div class="btn-group btn-group-xs btn-group-justified">
				<div class="btn-group">
					<button id="erpSaveButton" type="button" class="btn btn-success line"
						id="erpSaveButton">
						<span class="glyphicon glyphicon-ok-circle"></span>&nbsp;保存
					</button>
				</div>
			</div>
			<div class="btn-group btn-group-xs btn-group-justified">
				<div class="btn-group">
					<button id="erpDeleteButton" type="button"
						class="btn btn-default line text-error">
						<span class="glyphicon glyphicon-remove-circle"></span>&nbsp;删除
					</button>
				</div>
				<div class="btn-group">
					<button id="erpUpdateButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-refresh"></span>&nbsp;更新
					</button>
				</div>
				<div class="btn-group">
					<button id="erpAddButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-plus-sign"></span>&nbsp;新增
					</button>
				</div>
			</div>
			<div class="btn-group btn-group-xs btn-group-justified">
				<div class="btn-group">
					<button id="erpSubmitButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-floppy-saved"></span>&nbsp;提交
					</button>
				</div>
				<div class="btn-group">
					<button id="erpResSubmitButton" type="button" class="btn btn-default line text-danger">
						<span class="glyphicon glyphicon-floppy-remove"></span>&nbsp;反提交
					</button>
				</div>
				<div class="btn-group">
					<button id="erpAuditButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-hand-up"></span>&nbsp;审核
					</button>
				</div>
				<div class="btn-group">
					<button id="erpResAuditButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-hand-down"></span>&nbsp;反审核
					</button>
				</div>
				<div class="btn-group">
					<button id="erpEndButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-pause"></span>&nbsp;结案
					</button>
				</div>
				<div class="btn-group">
					<button id="erpResEndButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-play"></span>&nbsp;反结案
					</button>
				</div>
				<div class="btn-group">
					<button id="erpBannedButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-eye-close"></span>&nbsp;禁用
					</button>
				</div>
				<div class="btn-group">
					<button id="erpResBannedButton" type="button" class="btn btn-default line">
						<span class="glyphicon glyphicon-eye-open"></span>&nbsp;反禁用
					</button>
				</div>
			</div>
		</div>
		<!-- 按扭组 end -->
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
	        <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">关闭</span></button>
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
	</body>
	<script type="text/javascript"
		src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
	<script type="text/javascript"
		src="<%=basePath%>resource/bootstrap/bootstrap.min.js"></script>
	<script type="text/javascript" src="<%=basePath%>resource/bootstrap/moment.js"></script>
	<script src="http://cdn.bootcss.com/bootstrap-datetimepicker/3.1.3/js/bootstrap-datetimepicker.min.js"></script>
	<script type="text/javascript" src="http://cdn.bootcss.com/bootstrap-datetimepicker/2.1.30/js/locales/bootstrap-datetimepicker.zh-CN.js"></script>
	<script type="text/javascript" src="commonForm/util.js"></script>
	<script type="text/javascript" src="commonForm/dbFind.js"></script>
	<script type="text/javascript" src="commonForm/commonForm.js"></script>
</html>