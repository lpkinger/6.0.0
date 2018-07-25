<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet"
	href="<%=basePath%>resource/bootstrap/bootstrap.min.css" />
<style>
body,button,input,select,textarea {
	font: 12px/1.5 Tahoma, Arial, 'Microsoft Yahei', sans-serif;
}

body {
	z-index: 1;
	/* background-color: #dceaed; */
}

a {
	color: #6c6c6c;
}

.f14 {
	font-size: 14px;
}

.f18 {
	font-size: 18px;
}

.f24 {
	font-size: 24px;
}

.f36 {
	font-size: 36px;
}

.text-inverse {
	color: #f40;
}

.text-default {
	color: #3498db;
}

.text-bold {
	font-weight: bold;
	color: #333;
}

.main {
	position: absolute;
	left: 0;
	top: 0;
	right: 0;
	bottom: 0;
	width: 100%;
	bottom: 0;
	border-top: 1px solid #dcdcdc;
	background-repeat: no-repeat;
	background-position: center top;
	background-attachment: scroll;
	background-image:
		url("<%=basePath%>resource/images/screens/news_bg.jpg");
}

.main .q-wrap {
	position: absolute;
	top: 0;
	left: 0;
	bottom: 0;
	width: 340px;
	height: 100%;
	border-right: 1px solid #dcdcdc;
}

.q-wrap .header {
	width: 100%;
	height: 46px;
	line-height: 45px;
	padding-left: 20px;
	border-bottom: 1px solid #dcdcdc;
	position: relative;
}

.q-wrap .header strong {
	margin-right: 20px;
}

.q-wrap .header .dropdown-toggle {
	padding: 15px 0 16px 0;
}

.q-wrap .header .dropdown-toggle>i {
	padding: 0 20px;
}

.q-wrap .header .dropdown-menu {
	line-height: 1.68;
}

.q-wrap .header .dropdown-menu .form-group {
	margin-left: 15px;
}

.q-wrap .list {
	position: absolute;
	/* background-color: #fff; */
	left: 0;
	top: 46px;
	bottom: 47px;
	width: 100%;
	overflow-x: hidden;
	overflow-y: auto;
}

.q-wrap .list .item {
	position: relative;
	border-bottom: 1px solid #d9d9d9;
	padding: 5px 20px;
	cursor: pointer;
}

.q-wrap .list .item:hover {
	background-color: #f0f0f0
}

.q-wrap .list .item.active {
	background-color: #c2e0f4;
}

.q-wrap .list .item .title {
	height: 20px;
	line-height: 20px;
	font-size: 14px;
	max-width: 180px;
	color: #0a0a0a;
	text-overflow: ellipsis;
	white-space: nowrap;
	overflow: hidden;
}

.q-wrap .list .item .tool>i {
	display: none;
}

.q-wrap .list .item:hover .tool>i {
	display: inline-block;
}

.q-wrap .list .item .tool {
	position: absolute;
	color: #3498db;
	top: 6px;
	right: 15px;
}

.q-wrap .desc {
	margin-top: 5px;
	height: 20px;
	line-height: 20px;
	text-overflow: ellipsis;
	white-space: nowrap;
	line-height: 16px;
	overflow: hidden;
	height: 20px;
}

.q-wrap .footer {
	position: absolute;
	left: 0;
	bottom: 0;
	width: 100%;
	height: 47px;
	line-height: 45px;
	padding: 0 20px;
	border-top: 1px solid #dcdcdc;
	cursor: pointer;
}

.q-wrap .footer .btn {
	border: 1px solid transparent;
	border-radius: 500px;
	font-size: 12px;
	color: #555;
	padding: 3px;
	background-color: transparent;
	-webkit-transition: all .12s ease-in;
	transition: all .12s ease-in;
}

.q-wrap .footer .btn>i {
	width: 18px;
	height: 18px;
}

.q-wrap .footer .btn:hover {
	background-color: #3498db;
	border-color: #3498db;
	color: #fff;
}

.content-wrap {
	position: absolute;
	top: 0;
	left: 340px;
	right: 0;
	bottom: 0;
	height: 100%;
	overflow-x: hidden;
	overflow-y: hidden;
}

.content-wrap .tbar {
	width: 100%;
	height: 46px;
	line-height: 45px;
	border-bottom: 1px solid #dcdcdc;
	padding: 0 15px;
	position: relative;
}

.tbar .btn {
	border-color: #e6e7ec;
	border-radius: 0;
}

.tbar .btn:hover {
	border-color: #ccc;
}

.tbar .btn>span {
	margin-right: 5px;
}

.tbar .dropdown:hover>.dropdown-menu {
	margin-top: -3px;
}

.comment {
	padding: 15px;
	position: absolute;
	top: -500px;
	left: 0;
	right: 0;
	height: 148px;
	z-index: 100;
	border-bottom: 1px solid #dcdcdc;
	-webkit-transition: top 0.3s ease-in-out;
	-moz-transition: top 0.3s ease-in-out;
	transition: top 0.3s ease-in-out;
}

.comment-wrap .item {
	position: relative;
	line-height: normal;
	border-bottom: 1px dashed #eee;
}

.comment-wrap .item .title {
	margin-top: 10px;
	margin-bottom: 5px;
}

.comment-wrap .item .desc {
	margin-bottom: 10px;
}

.content-wrap .content {
	position: absolute;
	top: 46px;
	left: 0;
	width: 100%;
	bottom: 0;
	z-index: 10;
	-webkit-transition: top 0.3s ease-in-out;
	-moz-transition: top 0.3s ease-in-out;
	transition: top 0.3s ease-in-out;
	overflow-x: hidden;
	overflow-y: auto;
}

.content .header {
	padding: 15px;
	height: 74px;
	border-bottom: 1px solid #dcdcdc;
}

.header .header_top {
	position: relative;
}

.header_top .title {
	font-size: 14px;
	font-weight: bold;
	height: 22px;
}

.header_top .feedback {
	position: absolute;
	right: 15px;
	top: 0;
	padding: 0 0 0 55px;
	text-align: right;
	color: #777;
	padding: 0 0 0 55px;
}

.content .body {
	padding: 25px 15px;
	/* background-color: #fff; */
}

.content .attach {
	padding: 25px;
	border-top: 1px solid #dcdcdc;
}

.attach .item {
	position: relative;
	cursor: pointer;
	width: 180px;
	height: 180px;
	border: 1px solid #d6d6d6;
	margin-bottom: 25px;
	margin-right: 25px;
	-webkit-box-shadow: 2px 2px 8px rgba(0, 0, 0, .2);
	-moz-box-shadow: 2px 2px 8px rgba(0, 0, 0, .2);
	box-shadow: 2px 2px 8px rgba(0, 0, 0, .2);
	zoom: 1;
}

.attach .item .title {
	position: absolute;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 30px;
	line-height: 30px;
	text-align: center;
	padding: 0 5px;
	color: #222;
	border-top: 1px solid #ccc;
	background-color: #fff;
	z-index: 10;
	text-overflow: ellipsis;
	white-space: nowrap;
	overflow: hidden;
}

.attach .item .bg {
	position: absolute;
	width: 100%;
	height: 149px;
	line-height: 149px;
	left: 0;
	top: 0;
	overflow: hidden;
	text-align: center;
	vertical-align: middle;
	background-color: #f6f6f9;
	color: #999;
}

.attach .item .download {
	position: absolute;
	left: 0;
	top: 119px;
	width: 0;
	height: 30px;
	line-height: 30px;
	background: #090909;
	opacity: 0;
	text-align: center;
	z-index: 99;
	-webkit-transition: width 0.2s ease-in-out;
	-moz-transition: width 0.2s ease-in-out;
	transition: width 0.2s ease-in-out;
}

.attach .item:hover .download {
	width: 100%;
	opacity: .5;
}

.attach .download a {
	color: #fff;
}

.bg .global_icon {
	margin: 20px auto;
}

.attach .summary {
	line-height: 20px;
	padding: 5px;
	border-top: 1px solid #ddd;
	border-bottom: 1px solid #ddd;
	position: relative;
	overflow: hidden;
	zoom: 1;
}
/*dropdown*/
.dropdown>.dropdown-toggle {
	border-style: solid;
	border-width: 0 1px;
	border-color: transparent;
}

.dropdown:hover>.dropdown-toggle {
	position: relative;
	background-color: #ffffff;
	border: 1px solid #eee;
	border-top: 0;
	border-bottom: 0;
	z-index: 100001;
}

.dropdown:hover>.dropdown-menu {
	display: block;
	position: absolute;
	border-color: #eee;
	webkit-box-shadow: none;
	box-shadow: none;
	margin-top: -1px;
	min-width: 100%;
	font-size: 12px;
}

.pull-right .dropdown-menu {
	border-radius: 4px 0 0 4px;
}

.btn .caret {
	margin-left: 15px;
}

.global_icon {
	background: url("<%=basePath%>resource/images/merge/full_ico.png")
		no-repeat bottom right;
}

.global_icon.larger {
	height: 118px;
	width: 118px;
}

.icon_xls_l {
	background-position: 0 -855px;
}

.icon_pdf_l {
	background-position: -120px -735px;
}

.icon_doc_l {
	background-position: -282px -495px;
}

.icon_txt_l {
	background-position: -360px -735px;
}

.icon_zip_l {
	background-position: -120px -855px;
}

.icon_image_l {
	background-position: -120px -615px;
}

.icon_undefined_l {
	background-position: 0 -735px;
}
</style>
</head>
<script type="text/javascript">
	function $(id) {
		return document.getElementById(id);
	}
</script>
<body>
	<div class="main">
		<!-- 新闻列表 Start -->
		<div class="q-wrap">
			<div class="header">
				<div class="pull-left title">
					<strong class="f14">内部新闻</strong>已发布${totalCount}个
				</div>
				<div class="pull-right dropdown">
					<a class="dropdown-toggle"><i class="glyphicon glyphicon-list"></i></a>
					<div class="dropdown-menu" style="padding: 15px; width: 180px;">
						<form>
							<label><i class="glyphicon glyphicon-flag"></i>&nbsp;分页设置</label>
							<div class="form-group">
								<label class="radio-inline"> <input type="radio"
									name="pageSize" value="10" checked> 10
								</label> <label class="radio-inline"> <input type="radio"
									name="pageSize" value="20"> 20
								</label> <label class="radio-inline"> <input type="radio"
									name="pageSize" value="50"> 50
								</label>
							</div>
							<div class="checkbox">
								<label> <input type="checkbox" checked> 显示新闻快照
								</label>
							</div>
							<button class="btn btn-default btn-block btn-sm" type="submit">确
								定</button>
						</form>
					</div>
				</div>
			</div>
			<!-- 新闻展示 Start -->
			<script type="text/javascript">
				function getNews(id) {
					window.location.href = "<%=basePath%>oa/news/view.action?page=${page}&ne_id=" + id;
				}
			</script>
			<div class="list">
				<c:forEach items="${snapshot}" var="shot">
					<c:if test="${shot.ne_id == current.ne_id}">
						<div class="item active" onclick="getNews(${shot.ne_id})">
							<div class="title">${shot.ne_theme}</div>
							<div class="tool">${shot.ne_datestr}</div>
							<div>${shot.ne_releaser}</div>
							<div class="desc text-muted">${shot.ne_content}</div>
						</div>
					</c:if>
					<c:if test="${shot.ne_id != current.ne_id}">
						<div class="item" onclick="getNews(${shot.ne_id})">
							<div class="title">${shot.ne_theme}</div>
							<div class="tool">${shot.ne_datestr}</div>
							<div>${shot.ne_releaser}</div>
							<div class="desc text-muted">${shot.ne_content}</div>
						</div>
					</c:if>
				</c:forEach>
			</div>
			<!-- 新闻展示 End -->
			<!-- 分页 Start -->
			<div class="footer">
				<div class="pull-left">第${page}页，共${totalPage}页</div>
				<div class="pull-right">
					<c:if test="${page == 1 || totalPage == 1}">
						<a href="<%=basePath %>oa/news/view.action?page=1&pageSize=${pageSize}" class="btn" disabled>
							<i class="glyphicon glyphicon-step-backward"></i>
						</a>
						<a href="<%=basePath %>oa/news/view.action?page=${page-1}&pageSize=${pageSize}" class="btn" disabled>
							<i class="glyphicon glyphicon-chevron-left"></i>
						</a>
					</c:if>
					<c:if test="${page > 1}">
						<a href="<%=basePath %>oa/news/view.action?page=1&pageSize=${pageSize}" class="btn">
							<i class="glyphicon glyphicon-step-backward"></i>
						</a>
						<a href="<%=basePath %>oa/news/view.action?page=${page-1}&pageSize=${pageSize}" class="btn">
							<i class="glyphicon glyphicon-chevron-left"></i>
						</a>
					</c:if>
					<c:if test="${page == totalPage || totalPage == 1}">
						<a href="<%=basePath %>oa/news/view.action?page=${page+1}&pageSize=${pageSize}" class="btn" disabled>
							<i class="glyphicon glyphicon-chevron-right"></i>
						</a>
						<a href="<%=basePath %>oa/news/view.action?page=${totalPage}&pageSize=${pageSize}" class="btn" disabled>
							<i class="glyphicon glyphicon-step-forward"></i>
						</a>
					</c:if>
					<c:if test="${page < totalPage}">
						<a href="<%=basePath %>oa/news/view.action?page=${page+1}&pageSize=${pageSize}" class="btn">
							<i class="glyphicon glyphicon-chevron-right"></i>
						</a>
						<a href="<%=basePath %>oa/news/view.action?page=${totalPage}&pageSize=${pageSize}" class="btn">
							<i class="glyphicon glyphicon-step-forward"></i>
						</a>
					</c:if>
				</div>
			</div>
			<!-- 分页 End -->
		</div>
		<!-- 新闻反馈列表 End -->
		<!-- 新闻内容 Start -->
		<div class="content-wrap">
			<script type="text/javascript">
				var isCommentShow = false;
				function showCommentPane() {
					if(!isCommentShow) {
						$('comment').style.top = '46px';
						$('content').style.top = '194px';
						isCommentShow = true;
					} else
						hideCommentPane();
				}

				function hideCommentPane() {
					$('comment').style.top = '-500px';
					$('content').style.top = '46px';
					isCommentShow = false;
				}
			</script>
			<div class="tbar">
				<div class="pull-left">
					<a class="btn btn-default" href="javascript:showCommentPane();"><span
						class="glyphicon glyphicon-edit"></span>评论</a> <a
						class="btn btn-default"><span
						class="glyphicon glyphicon-trash"></span>删除</a>
				</div>
				<div class="pull-right dropdown">
					<a class="btn btn-default dropdown-toggle"><span
						class="glyphicon glyphicon-share"></span>查看评论<span class="caret"></span></a>
					<div class="dropdown-menu dropdown-menu-right comment-wrap"
						style="padding: 15px; width: 280px;">
						<c:if test="${current.comments.size() > 0}">
							<ul class="list-unstyled">
								<c:forEach items="${current.comments}" var="comment">
									<li class="item">
										<div class="title">
											<span class="text-bold">${comment.nc_caster}</span><span
												class="text-muted pull-right">${comment.nc_datestr}</span>
										</div>
										<div class="desc">${comment.nc_comment}</div>
									</li>
								</c:forEach>
							</ul>
						</c:if>
						<c:if test="${current.comments.size() == 0}">
							暂无评论<a href="javascript:showCommentPane()" class="pull-right">我来评论下</a>
						</c:if>
					</div>
				</div>
			</div>
			<div id="comment" class="comment">
				<form class="form-horizontal" method="post"
					action="<%=basePath%>oa/news/view/sendComment.action">
					<div class="form-group">
						<div class="col-sm-12">
							<textarea rows="3" class="form-control" name="nc_comment"
								placeholder="我要评论" autofocus="autofocus" required="required"></textarea>
						</div>
					</div>
					<input type="hidden" name="nc_neid" value="${current.ne_id}">
					<div class="form-group">
						<div class="col-sm-12">
							<div class="pull-right">
								<button type="submit" class="btn btn-info">发表</button>
								<a href="javascript:hideCommentPane();" class="btn btn-default">取消</a>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div id="content" class="content">
				<div class="header">
					<div class="header_top">
						<div class="title">${current.ne_theme}</div>
						<div class="feedback">${current.ne_datestr}</div>
					</div>
					<div class="text-muted">编辑：${current.ne_releaser}</div>
				</div>
				<div class="body">${current.ne_content}</div>
				<c:if test="${attachs != null}">
					<div class="attach">
						<ul class="list-unstyled list-inline">
							<c:forEach items="${attachs}" var="attach">
								<li class="item" title="${attach.fp_name}">
									<div class="bg">
										<c:choose>
											<c:when test="${attach.fp_type == 'pdf' }">
												<div class="global_icon larger icon_pdf_l"></div>
											</c:when>
											<c:when
												test="${attach.fp_type == 'xls' || attach.fp_type == 'xlsx' || attach.fp_type == 'et' }">
												<div class="global_icon larger icon_xls_l"></div>
											</c:when>
											<c:when
												test="${attach.fp_type == 'doc' || attach.fp_type == 'docx' }">
												<div class="global_icon larger icon_doc_l"></div>
											</c:when>
											<c:when test="${attach.fp_type == 'txt' }">
												<div class="global_icon larger icon_txt_l"></div>
											</c:when>
											<c:when
												test="${attach.fp_type == 'zip' || attach.fp_type == 'rar' }">
												<div class="global_icon larger icon_zip_l"></div>
											</c:when>
											<c:when
												test="${attach.fp_type == 'png' || attach.fp_type == 'jpg' || attach.fp_type == 'jpeg' || attach.fp_type == 'bmp' || attach.fp_type == 'gif' }">
												<div class="global_icon larger icon_image_l"></div>
											</c:when>
											<c:otherwise>
												<div class="global_icon larger icon_undefined_l"></div>
											</c:otherwise>
										</c:choose>
									</div>
									<div class="title">${attach.fp_name}</div>
									<div class="download">
										<a
											href="<%=basePath %>common/downloadbyId.action?id=${attach.fp_id}">下载</a>
									</div>
								</li>
							</c:forEach>
						</ul>
						<div class="summary">
							<span class="glyphicon glyphicon-paperclip"></span>&nbsp;&nbsp;共${attachs.size()}个附件
							<a class="pull-right btn btn-default btn-xs"> <span
								class="glyphicon glyphicon-save"></span>&nbsp;&nbsp;全部下载
							</a>
						</div>
					</div>
				</c:if>
			</div>
		</div>
		<!-- 新闻内容 End -->
	</div>
</body>
<script>
	function getUrlParam(name){   
	    var reg=new RegExp("(^|&)"+name+"=([^&]*)(&|$)");   
	    var r=window.location.search.substr(1).match(reg);   
	    if  (r!=null)   return decodeURI(r[2]); 
	    return   null;   
	}
	function autoScrollTo(ne_id){
		document.getElementsByClassName('active')[0].scrollIntoView(true);
	}
	var isJumpFromIndex = getUrlParam('isJumpFromIndex');
	if(isJumpFromIndex){
		var neid = getUrlParam('ne_id');
		autoScrollTo(neid);
	}
</script>
</html>