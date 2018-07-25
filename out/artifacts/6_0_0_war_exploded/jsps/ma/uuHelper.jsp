<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>UU助手</title>
<link rel="icon" href="<%=basePath %>resource/images/icon_title.png" type="image/x-icon"/>
<link rel="stylesheet" href="<%=basePath %>resource/bootstrap/bootstrap.min.css" />
<script type="text/javascript" src="<%=basePath %>resource/sources/jquery-3.0.0.min.js"></script>
<link rel="stylesheet" href="<%=basePath %>resource/css/uuHelper.css" />
<script type="text/javascript">
	//数据列表
	var data;
	//默认页码
	var page ="${page}";
	//默认每页
	var pagesize = "${pageSize}";
	
	function getData(page,pagesize){
		$.ajax({
			url: "http://www.usoftchina.com/api?method=queryContentPage&module=uuhelper&status=normal&orderBy=meta:push_time",
			type:"get",
			data:{
				"pagesize":pagesize,
				"page":page
			},
			dataType:"json",
			success:function(result){
				//返回登录结果成功
				data = result.data;
				setPage(data);
				
	          	setTimeout(function(){
					hideLoading();
					setListDiv(data);
	           	}, 800);
			}
		});
		
	}
	//显示页码
	function setPage(data){
		if($(".pageNumber").length>0){
			$(".pageNumber").remove();
		}
		$(".totalPage").html("共"+data.totalPage+"页");
		for(var i = 1;i<=data.totalPage;i++){
			if(data.pageNumber==i){
				var pageNumber = '<li class="pageNumber active"><a onclick="getData('+i+','+pagesize+')">'+i+'</a></li>';
			}else{
				var pageNumber = '<li class="pageNumber"><a onclick="getData('+i+','+pagesize+')">'+i+'</a></li>';
			}
			
			$(".Next").before(pageNumber);
		}
	}
	//隐藏预加载元素
	function hideLoading(){
		$("#uuloading").hide();
	}
	
	//显示列表
	function setListDiv(data){
		$("#uulist").show();
		$("#uuUl").empty();
		var title,created,summary,text,temp,link,thumbnail,author;
		for(var i = 1;i<data.list.length;i++){
			thumbnail = '<div class="temp_l fl"><img class="thumbnail" src="http://www.usoftchina.com/'+data.list[i].thumbnail+'"/></div>'
			title = '<h3 class="title">'+data.list[i].title+'</h3>';
			summary = '<p class="summary">'+data.list[i].summary+'</p>';
			created = '<span class="created">'+data.list[i].created+'</span>';
			author = '<span class="author">'+data.list[i].author+'</span>';
			temp = '<div class="temp">'+thumbnail+'<div class="temp_r fl" onclick="getdetail('+i+')">'+title+summary+created+author+'</div>'+'</div>';
			$("#uuUl").append(temp);
		}
	}
	
	//跳转详情
	function getdetail(i){
		$("#uulist").hide();
		var title = '<h3 class="title">'+data.list[i].title+'</h3>';
		var created = '<span class="created">'+data.list[i].created+'</span>';
		var author = '<span class="author">'+data.list[i].author+'</span>';
		var tempdetail = title +'<div class="Detail_head">'+created + author+"</div>" + checkText(data.list[i].text) + '<div class="Detail_Bottom"></div>';
		
		$(".content").append(tempdetail);
		$("#uudetail").show();
	}
	//检查内容
	function checkText(content){
		return content.replace(new RegExp('src=\"/attachment','g'),'src=\"http://www.usoftchina.com/attachment');
	}
	//后退
	function back(){
		$("#uulist").show();
		$("#uudetail").hide();
		$(".content").empty();
	}
	
	$(function(){
		getData(page,pagesize);
		$(".Previous").on('click',function(){
			if(data.pageNumber>1){
				getData(data.pageNumber-1,pagesize);
			}	
		})
		$(".Next").on('click',function(){
			if(data.pageNumber<data.totalPage){
				getData(data.pageNumber+1,pagesize);
			}	
		})
	});
</script>
</head>
<body>
<div>
	<div class="container">
		<div id = "uuloading">
			<div id='loading'>
				<div class='block' id='rotate_01'></div>
				<div class='block' id='rotate_02'></div>
				<div class='block' id='rotate_03'></div>
				<div class='block' id='rotate_04'></div>
				<div class='block' id='rotate_05'></div>
				<div class='block' id='rotate_06'></div>
				<div class='block' id='rotate_07'></div>
				<div class='block' id='rotate_08'></div>
			</div>
		</div>
		<div id = "uulist">
			<div>
				<div id = "uuUl"></div>
			</div>
			<div id="pagelist">
				<nav style="text-align: center" aria-label="Page navigation">
				  <ul class="pagination">
				    <li class="Previous">
				      <a href="#" aria-label="Previous">
				        <span aria-hidden="true">上一页</span>
				      </a>
				    </li>
				    <li class="Next">
				      <a href="#" aria-label="Next">
				        <span aria-hidden="true">下一页</span>
				      </a>
				    </li>
   				    <li>
				        <span class="totalPage"></span>
				    </li>
				  </ul>
				</nav>
			</div>
		</div>
		<div id = "uudetail">
			<div>
			    <a href="#top"></a>
			</div>
		 	<div class = "content">
		 	</div>
			<div class="sideBar">
				<a class="btn btn-danger  btn-block" role="button" onclick="back()">后退</a>
				<a name="top" href="#" class="btn btn-success  btn-block" role="button">回到顶部</a>
			</div>
		</div>
	</div>
</div>
</body>
</html>