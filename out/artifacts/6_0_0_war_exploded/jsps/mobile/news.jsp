<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<%
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ request.getContextPath() + "/";
%>
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, minimum-scale=1, user-scalable=no, minimal-ui">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<link rel="apple-touch-icon" href="images/apple-touch-icon.png" />
<link rel="apple-touch-startup-image"
	href="images/apple-touch-startup-image-320x460.png" />
<title>新闻公告</title>
<base href="<%=basePath%>jsps/mobile/" />
<link rel="stylesheet" href="style.css">
<link rel="stylesheet" href="<%=basePath %>jsps/mobile/css/powerFloat.css"  type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>jsps/mobile/css/xmenu.css"  type="text/css"></link>
<link
	href='http://fonts.useso.com/css?family=Source+Sans+Pro:400,300,700,900'
	rel='stylesheet' type='text/css' />
</head>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-1.8.0.min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-powerFloat-min.js"></script>
<script type="text/javascript"
	src="<%=basePath%>resource/jquery/jquery-xmenu.js"></script>
<script type="text/javascript">
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
	var hasshow = false, page = 1, pageSize = 10, notepage = 1, notePageSize = 10,currentType='content_news';
	window.onload = function() {
		new tab('info_li_now_','_',function() {
			     currentType=this['id'];
			    if(this['id']=='content_note' && $('#note>li').length==0){
			    	loadNoteData();
			    }
				});
	};
	Date.prototype.Format = function(fmt) { //author: meizz 
		var o = {
			"M+" : this.getMonth() + 1, //月份 
			"d+" : this.getDate(), //日 
			"h+" : this.getHours(), //小时 
			"m+" : this.getMinutes(), //分 
			"s+" : this.getSeconds(), //秒 
			"q+" : Math.floor((this.getMonth() + 3) / 3), //季度 
			"S" : this.getMilliseconds()
		//毫秒 
		};
		if (/(y+)/.test(fmt))
			fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "")
					.substr(4 - RegExp.$1.length));
		for ( var k in o)
			if (new RegExp("(" + k + ")").test(fmt))
				fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k])
						: (("00" + o[k]).substr(("" + o[k]).length)));
		return fmt;
	};
	$(document).ready(
			
			function() {
				var lasttime=new Date();
				$("#selectpos").xMenu({
					width : 600,
					eventType : "click", //事件类型 支持focus click hover
					dropmenu : "#m1",//弹出层
					hiddenID : "selectposhidden"//隐藏域ID		

				});
				$('#pages_maincontent').scroll(
						function() {
							if ($('#pages_maincontent').scrollTop() >= $(
									'#'+currentType+'_lists').height()
									- $(window).height()) {	
								if(new Date().getTime()-lasttime>2000){
									lasttime=new Date();
									if(currentType=='content_news'){
										page += 1;
										loadData();
									}
									else {
										notepage += 1;
										loadNoteData();
									}
								}
								
							}
						});

			});
	function loadNoteData(){
		$("#background,#progressBar").show(); 
    	$.ajax({
			type : "POST",
			url : basePath
					+ "mobile/note/getNotesByPage.action",
			data : {
				page : notepage,
				pageSize : notePageSize
			},
			cache : true,
			success : function(result) {
				$("#background,#progressBar").hide();  
				var data = result.note;
				$(data).each( 
						function(index, item) {
									html = '';
									html += "<li class='swipeout'><div class='swipeout-content item-content'><div class='post_entry'>";
									html += '<div class="post_details"><h2><a href="../../mobile/note/noteDetail.action?id='+item.no_id+'">'
											+ item.no_title
											+ ".</a></h2><p>"
											+ item.no_content
											+ "</p><span class='post_date'>"
											+ new Date(item.no_apptime).Format('yyyy-MM-dd hh:mm:ss.0')
											+ "</span> <span class='post_author'><a href='#'>"
											+ item.no_approver
											+ "</a></span><span class='post_comments'><a href='#'>0</a></span></div>";
									html += "</div></div></li>";
									$('#note').append(html);

								});
				top = document.body.scrollTop;
			}
		});	
	};
	function loadData() {
		var html;
		$("#background,#progressBar").show();
		   $.ajax({
					type : "POST",
					url : basePath + "mobile/news/getNewsByPage.action",
					data : {
						page : page,
						pageSize : pageSize,
					},
					cache : true,
					success : function(result) {
						$("#background,#progressBar").hide(); 
						var data = result.news;
						$(data).each(
								function(index, item) {						
											html = '';
											html += "<li class='swipeout'><div class='swipeout-content item-content'><div class='post_entry'>";
											html += "<div class='post_thumb'><img src="+item.headerImg+" alt='' title='' /></div>";
											html += "<div class='post_details'><h2><a href='#'>"
													+ item.ne_theme+ ".</a></h2><p>"
													+ item.ne_content
													+ "</p><span class='post_date'>"
													+ new Date(item.ne_releasedate).Format('yyyy-MM-dd hh:mm:ss.0')
													+ "</span> <span class='post_author'><a href='#'>"
													+ item.ne_releaser
													+ "</a></span><span class='post_comments'><a href='#'>0</a></span></div>";

											html += "</div></div></li>";
											$('#posts').append(html);

										});
						top = document.body.scrollTop;
					}
				});
	};

	function showMenu() {
		if (!hasshow) {
			$("#selectpos").xMenu({
				width : 600,
				eventType : "click", //事件类型 支持focus click hover
				dropmenu : "#m1",//弹出层
				hiddenID : "selectposhidden"//隐藏域ID		

			});
			hasshow = true;
		} else {
			$.powerFloat.hide();
			hasshow = false;
		}
	}
	function tab(o, s, cb, ev) { //tab切换类
		var $ = function(o) {
			return document.getElementById(o)
		};
		var css = o.split((s || '_'));
		if (css.length != 4)
			return;
		this.event = ev || 'onclick';
		o = $(o);
		if (o) {
			this.ITEM = [];
			o.id = css[0];
			var item = o.getElementsByTagName(css[1]);
			var j = 1;
			for (var i = 0; i < item.length; i++) {
				if (item[i].className.indexOf(css[2]) >= 0
						|| item[i].className.indexOf(css[3]) >= 0) {
					if (item[i].className == css[2])
						o['cur'] = item[i];
					item[i].callBack = cb || function() {
					};
					item[i]['css'] = css;
					item[i]['link'] = o;
					this.ITEM[j] = item[i];
					item[i]['Index'] = j++;
					item[i][this.event] = this.ACTIVE;
				}
			}
			return o;
		}
	}
	tab.prototype = {
		ACTIVE : function() {
			var $ = function(o) {
				return document.getElementById(o)
			};
			this['link']['cur'].className = this['css'][3];
			this.className = this['css'][2];
			try {
				$(this['link']['id'] + '_' + this['link']['cur']['Index']).style.display = 'none';
				$(this['link']['id'] + '_' + this['Index']).style.display = 'block';
			} catch (e) {
			}
			this.callBack.call(this);
			this['link']['cur'] = this;
		}
	}
</script>
<style>
.background { 
display: block; 
width: 100%; 
height: 100%; 
opacity: 0.4; 
filter: alpha(opacity=40); 
background:while; 
position: absolute; 
top: 0; 
left: 0; 
z-index: 2000; 
} 
.progressBar { 
display: block; 
width: 148px; 
height: 28px; 
position: fixed; 
top: 50%; 
left: 50%; 
margin-left: -74px; 
margin-top: -14px; 
padding: 10px 10px 10px 50px; 
text-align: left; 
line-height: 27px; 
font-size:13px;
font-weight: bold; 
position: absolute; 
z-index: 2001; 
} 

.tab {
	height: 34px;
	margin: auto;
	text-align: center;
}

.tab ul {
	list-style-type: none;
	padding-left: 0px;
}

.tab li {
	display: inline-block;
	padding: 0 30px;
	height: 34px;
	line-height: 34px;
	font-size: 18px;
	text-align: center;
	/* border-bottom:1px #ebf7ff solid; */
	cursor: pointer;
}

.tab li.now {
	color: #5299c4;
	background: #fff;
	font-weight: bold;
	border-bottom: 1px #cfedff solid;
	font-size: 18px;
}

.tablist {
	padding: 10px;
	font-size: 14px;
	line-height: 24px; /* border:1px #cfedff solid; */
	border-top: 0;
	display: none;
}

.block {
	display: block;
}

.red {
	color: #BD0A01;
}

#c {
	margin-top: 10px;
}

.c1,.c2 { /* width:378px; */
	line-height: 20px;
}

.c1 {
	color: #014CC9;
}

.c2 {
	color: #7E6095;
	display: none;
}
/* h3{font-size:16px;padding:5px 0;} */
</style>
<body>
	<div class="pages navbar-through toolbar-through">
		<div data-page="projects" class="page no-toolbar no-navbar">
			<div class="page-content">
				<div class="navbarpages">

					<div class="navbar_home_link">
						<a href="javascript:window.location.reload();" data-panel="left"
							class="open-panel"><img src="../../resource/images/user.png"
							alt="" title="" /></a>
					</div>
					<div class="navbar_page_center">企业新闻</div>
					<div class="menu_open_icon_white">
						<a id="selectpos" href="javascript:showMenu();"><img
							src="../../resource/images/menu_open.png" alt="" title="" /> </a>
					</div>
				</div>
				<div id="pages_maincontent">
					<div class="tab">
						<ul id="info_li_now_">
							<li id="content_news" class="now">时事新闻</li>
							<li id="content_note">公告通知</li>
						</ul>
					</div>
					<div>
						<div id="info_1" class="tablist block">
							<div class="list-block" id="content_news_lists">
								<ul class="posts" id="posts">
									<c:forEach items="${news}" var="info">
										<li class="swipeout">
											<div class="swipeout-content item-content">
												<div class="post_entry">
													<div class="post_thumb">
														<img src="${info.headerImg}" alt="" title="" />
													</div>
													<div class="post_details">
														<h2>
															<a href="../../mobile/news/newsDetail.action?ne_id=${info.ne_id}">${info.ne_theme}.</a>
														</h2>
														<p>${info.ne_content}</p>
														<span class="post_date">${info.ne_releasedate}</span> <span
															class="post_author"><a href="#">${info.ne_releaser}</a></span>
														<span class="post_comments"><a href="#">0</a></span>
													</div>
												</div>
											</div>
										</li>
									</c:forEach>
								</ul>
							</div>
						</div>
						<!--系统公告 -->
						<div id="info_2" class="tablist">
							<div class="list-block" id="content_note_lists">
								<ul class="posts" id="note">
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div id="m1" class="xmenu" style="display: none;">
			<dl>
				<dt class="open">热门网站</dt>
				<dd>
					<ul>
						<li rel="1"><a href="http://3g.qq.com">腾讯</a></li>
						<li rel="2"><a href="http://www.sohu.com/">搜狐</a></li>
						<li rel="3"><a href="http://sina.cn/">新浪</a></li>
						<li rel="4"><a href="http://www.163.com/">网易</a></li>
						<li rel="5"><a href="http://www.baidu.com/">百度</a>&nbsp;•&nbsp;<a
							href="http://tieba.baidu.com/">贴吧</a></li>
						<li rel="6"><a href="http://www.tmall.com/">天猫</a></li>
						<li rel="7"><a href="http://www.taobao.com/">淘宝网</a></li>
						<li rel="8"><a href="http://www.jd.com/">京东</a></li>
						<li rel="8"><a href="http://www.ifeng.com/">凤凰网</a></li>
						<li rel="9"><a href="http://www.top81.com.cn/">军事头条</a></li>
						<li rel="10"><a href="http://china.nba.com/">NBA</a></li>
					</ul>
				</dd>
				<dt class="open">其他网站</dt>
				<dd>
					<ul>
						<li rel="1"><a href="http://sz.58.com/">58同城</a></li>
						<li rel="2"><a href="http://sz.fang.com/">搜房网</a></li>
						<li rel="3"><a href="http://www.ctrip.com/">携程网</a></li>
						<li rel="4"><a href="http://www.qunar.com/">去哪儿</a></li>
						<li rel="5"><a href="http://www.ganji.com/">赶集网</a></li>
						<li rel="6"><a href="http://n.vip.com/">唯品会</a></li>
						<li rel="7"><a href="http://www.tuniu.com//">途牛网</a></li>
						<li rel="8"><a href="http://www.renren.com/">人人网</a></li>
						<li rel="8"><a href="http://www.zhaopin.com/">智联招聘</a></li>
						<li rel="9"><a href="http://www.yhd.com/">1号店</a></li>
						<li rel="10"><a href="http://www.amazon.cn/">亚马逊</a></li>
					</ul>
				</dd>
			</dl>
		</div>
	</div>
	 <div id="background" class="background" style="display: none; "></div> 
<div id="progressBar" class="progressBar" style="display: none; ">
<img src="<%=basePath%>resource/images/loading.gif" alt="loading.." />加载中...</div> 
</body>
</html>