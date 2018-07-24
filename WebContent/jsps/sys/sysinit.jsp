<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%
	String url = request.getRequestURL().toString();
	String basePath = url.substring(0, url.length()
			- request.getRequestURI().length())
			+ request.getContextPath() + "/";
	String caller = request.getParameter("caller");
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<style type="text/css">
 .x-monthpicker{   
	height:200px !important;
} 
 .arrow{
	background-image: url('../jsps/sys/images/arrows2.png') no-repeat 0 -55px; 
} 
  .baseconfirmbutton{
 	left:47% !important;
 } 
 .baseconfirmtpl{
 	color:black !important;
 	font-family: 黑体 !important;
 	/* font-style: italic !important; */
 }
#enterprisesave-btnInnerEl{
	color:white;
}
/* .x-grid-cell-inner-treecolumn{
	margin-left:-30px;
} */
/* .x-grid-cell-inner-treecolumn{
	margin-left:-30px;
} */
/* .jptreecolumn{
	margin-left:-30px !important;
} */
.jptreecolumn .x-grid-cell-inner-treecolumn{
	margin-left:-30px !important;
}
 .mui-switch {
  width: 35px;
  height: 18px;
  position: relative;
  border: 1px solid #dfdfdf;
  background-color: #fdfdfd;
  box-shadow: #dfdfdf 0 0 0 0 inset;
  border-radius: 8px;
  background-clip: content-box;
  display: inline-block;
  -webkit-appearance: none;
  user-select: none;
  outline: none; }
  .mui-switch:before {
    content: '';
    width: 15px;
    height: 15px;
    position: absolute;
    top: 0px;
    left: 0;
    border-radius: 20px;
    border-top-left-radius: 20px;
    border-top-right-radius: 20px;
    border-bottom-left-radius: 20px;
    border-bottom-right-radius: 20px;
    background-color: #fff;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.4); }
  .mui-switch:checked {
    border-color: #64bd63;
    box-shadow: #64bd63 0 0 0 16px inset;
    background-color: #64bd63; }
    .mui-switch:checked:before {
      left: 19px; }
  .mui-switch.mui-switch-animbg {
    transition: background-color ease 0.4s; }
    .mui-switch.mui-switch-animbg:before {
      transition: left 0.3s; }
    .mui-switch.mui-switch-animbg:checked {
      box-shadow: #dfdfdf 0 0 0 0 inset;
      background-color: #64bd63;
      transition: border-color 0.4s, background-color ease 0.4s; }
      .mui-switch.mui-switch-animbg:checked:before {
        transition: left 0.3s; }
  .mui-switch.mui-switch-anim {
    transition: border cubic-bezier(0, 0, 0, 1) 0.4s, box-shadow cubic-bezier(0, 0, 0, 1) 0.4s; }
    .mui-switch.mui-switch-anim:before {
      transition: left 0.3s; }
    .mui-switch.mui-switch-anim:checked {
      box-shadow: #64bd63 0 0 0 16px inset;
      background-color: #64bd63;
      transition: border ease 0.4s, box-shadow ease 0.4s, background-color ease 1.2s; }
      .mui-switch.mui-switch-anim:checked:before {
        transition: left 0.3s; }

.noboder{
	border:0px solid #B5B8C8;
	padding:10px;
	margin-bottom:10px;
	display:block;
}
#jppaneltree-body{
	border-width: 0px !important;
}

#simplejprocess-body{
	border-width:0px !important;
}
#simplejpform-body{
	border-width:0px !important;
}
#saveProcessBtc-btnInnerEl{
	padding-left: 10px !important;
	color:white;
}
#textfield-1098-labelCell{
	width:55px !important;
}
#textfield-1099-labelCell{
	width:55px !important;
}
#textfield-1100-labelCell{
	width:55px !important;
}
#textarea-1101-labelCell{
	width:55px !important;
}
#hrgrouptabpanel{
	background-color: #e8e8e8;
    border-image-source: initial;
    border-image-slice: initial;
    border-image-width: initial;
    border-image-outset: initial;
    border-image-repeat: initial;
    border-width: 0px;
    border-style: solid;
    border-color: #e8e8e8;
    }
.x-button-icon-pic {
    background-image: url('../resource/images/icon/pic.png');
}
.x-form-group-label {
	width: 30%;
	padding-left:10px;
	height: 20px;
	line-height: 20px;
	z-index: 2;
	position: relative;
	cursor: pointer;
	font-size: 15px;
	color: gray;
	border: 1px solid #8b8970;
	-moz-box-shadow: 1px 1px 2px rgba(0, 0, 0, .2);
	-webkit-box-shadow: 1px 1px 2px rgba(0, 0, 0, .2);
	box-shadow: 1px 1px 2px rgba(0, 0, 0, .2);
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	border-radius: 5px;
	 background: #f1f1f1 url('../resource/images/icons.png') no-repeat 0 -55px !important 
}
.search-item {
	font: normal 11px tahoma, arial, helvetica, sans-serif;
	padding: 3px 10px 3px 10px;
	border: 1px solid #fff;
	border-bottom: 1px solid #eeeeee;
	white-space: normal;
	color: #555;
}
#fieldset-1070 {
	border: 0px solid #b5b8c8 !important;
}
#fieldset-1073 {
	border: 0px solid #b5b8c8 !important;
}
.image-tishi {
    font-size: 13px;
    color: blue;
    font-style:italic
}
.hello-button {   
        background: url(images/hello.png) left top no-repeat;   
}
.search-item h3 {
	display: block;
	font: inherit;
	font-weight: bold;
	color: #222;
}

.search-item h3 span {
	float: right;
	font-weight: normal;
	margin: 0 0 5px 5px;
	width: 150px;
	display: block;
	clear: none;
}

.msg .x-box-mc {
	font-size: 14px;
}
#msg-div {
	position: absolute;
	left: 50%;
	top: 10px;
	width: 400px;
	margin-left: -200px;
	z-index: 20000;
}

#msg-div .msg {
	border-radius: 8px;
	-moz-border-radius: 8px;
	background: #F6F6F6;
	border: 2px solid #ccc;
	margin-top: 2px;
	padding: 10px 15px;
	color: #555;
}

#msg-div .msg h3 {
	margin: 0 0 8px;
	font-weight: bold;
	font-size: 15px;
}

#msg-div .msg p {
	margin: 0;
}
#jppanel{top: 0px !important; }

#basicgrouptabpanel {
	background-color: #E0E0E0 !important;
    border: solid 0px ;}
#basicgrouptabpanel {
	background-color: #E0E0E0 !important;
    border: solid 0px ;}
    
.x-button-icon-excel {
	background-image: url('<%=basePath %>resource/images/excel.png');
}
 .button-readed {
	background-image: url('<%=basePath %>resource/images/readed.png')
}
</style>
<link rel="icon" href="<%=basePath%>resource/images/icon_title.png"
	type="image/x-icon" />
<%-- <link rel="stylesheet"
	href="<%=basePath%>resource/css/main.css"
	type="text/css"></link> --%>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-gray/ext-theme-gray-all.css"
	type="text/css"></link>
<link rel="stylesheet"
	href="<%=basePath%>resource/ext/4.2/resources/ext-theme-neptune/tree-neptune.css"
	type="text/css"></link>
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>resource/ux/css/CheckHeader.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/GroupTabPanel.css" />
<link rel="stylesheet" type="text/css"
	href="<%=basePath%>jsps/sys/css/init.css" />
<style type="text/css">
.x-toolbar-sencha {
	background: #e0e0e0;
	color: #304c33;
	border: none !important;
}
.x-form-group-label h6, .x-form-group-label-close h6{
	margin-left: 23px !important;
    /* padding-bottom: 10px; */
    top: -28px;
    position: absolute;
}
.x-toolbar-sencha .x-logo {
	padding: 10px 10px 10px 31px;
	/*  background: url(../images/logo.png) no-repeat 10px 11px; */
	color:#666; 
	text-align:center;
	font-size: 22px;
	font-weight: bold;
	text-shadow: 0 1px 0 #4e691f;
}
#processview-1014{
	width:305px!important;
}
#jprocesstab-body{
	border-width:0px !important;
}
.x-grouptabbar .x-panel-body {
    background-color: #e5e5e5!important;
}
.x-grouptabbar .x-grid-cell-inner {
    color: #696969!important;
}

</style>
<style>
.loading {
	background: url("<%=basePath %>resource/images/loading.gif") no-repeat center!important; 
}
.checked {
	background: url("<%=basePath %>resource/images/renderer/finishrecord.png") no-repeat center!important; 
}
.error {
	background: url("<%=basePath %>resource/images/renderer/important.png") no-repeat center!important; 
}
.refresh{
    background: url('<%=basePath %>resource/images/refresh.gif')  no-repeat;
}
.x-btn-primary {
	padding: 3px 12px
}
.x-btn-primary.x-btn-default-large .x-btn-inner {
	font-size: 14px;
	font-weight: 700
}
.simpleactiongrid-addbtn {
    background: #d5d5d5;
    background: -moz-linear-gradient(top, #fff 0, #efefef 38%, #d5d5d5 88%);
    background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #fff),
 color-stop(38%, #efefef), color-stop(88%, #d5d5d5));
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#fff',
 endColorstr='#d5d5d5', GradientType=0);
    border-color: #bfbfbf;
    border-radius: 2px;
    vertical-align: bottom;
    text-align: center;
}
</style>
 <script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all.js"></script>
 <script type="text/javascript" src="<%=basePath %>resource/sources/jquery-3.0.0.min.js"></script>
<%-- <script type="text/javascript"
	src="<%=basePath%>resource/ext/4.2/ext-all-debug.js"></script> --%>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath%>jsps/sys/sysinit.js"></script>
<script type="text/javascript">	
var msgCt;
var index;
var nowvalue="enterprise";
var newvalue="enterprise";
var nowhtml="企业信息";
var newhtml="企业信息";
var initabled;
var dept;
var title;
/* var selete;
var seleteId; */
var selectWindow;
var table="enterprise";
var paneltype;
var span;
$(document).ready(function(){ 
	$.ajax({
		type : "POST",
		contentType : "application/json",
		url : basePath + "common/saas/common/sysinitnavigation.action",
		success : function(c) { 
				initabled=c.color;
		}
	})
	}); 
var showResult =function(title,s){
	  if(!msgCt){
        msgCt = Ext.DomHelper.insertFirst(document.body, {id:'msg-div'}, true);
    }
    var m = Ext.DomHelper.append(msgCt, createBox(title, s), true);
    m.hide();
    m.slideIn('t').ghost("t", { delay: 1000, remove: true});
};
function createBox(t, s){
    return '<div class="msg"><h3>' + t + '</h3><p>' + s + '</p></div>';
 }
 function choiceInfo(s){
	 if(s.parentNode.childNodes[3].style.display=="none"){
		 s.parentNode.childNodes[3].style.display="block";
	 }else{
		 s.parentNode.childNodes[3].style.display="none";
	 }
 }
 function add(s){
	 var parentnode=s.parentNode.parentNode.parentNode;
	 var newEl="<div class='node' id='jiddian' style='position: relative;'>"+
		"<div class='arrow' id=''><svg xmlns='http://www.w3.org/2000/svg' version='1.1'><line x1='50' y1='0' x2='50' y2='45' style='stroke:rgba(51, 51, 51, 0.88);stroke-width:4'/><line x1='50' y1='47' x2='42' y2='38'style='stroke:rgba(51, 51, 51, 0.88);stroke-width:4'/><line x1='50' y1='47' x2='58' y2='38'style='stroke:rgba(51, 51, 51, 0.88);stroke-width:4'/><line x1='50' y1='47' x2='50' y2='48'style='stroke:rgba(51, 51, 51, 0.88);stroke-width:4'/></svg></div>"+
			"<div class='jpcontent' id='' style='position: relative'><span class='simplejprocess-delete' onclick='deletejp(this);' style='cursor:pointer;position:absolute;right:4px;top:-12px;'><img src='../jsps/sys/images/spjdelete.png'></img></span><span style='color:black;cursor:pointer;width:105px;height:50px;' class='assignInfo' name='assignInfo' data-code='' data-name='' data-type='' data-contact=''  onclick='choiceInfo(this)' onmouseover='show(this);' onmouseout= 'hide(this);'>[新节点]</span><span class='simplejprocess-add' style='cursor:pointer;position:absolute;right:4px;top:18px;' onclick='add(this);'><img src='../jsps/sys/images/spjadd.png'></img></span><div class='choicecontent' id='' style='position: absolute;z-index:99;left:-6px;background-color:white;top: 48px;line-height: 15px;display:block;'><a href='#' onclick='choiceMan(this);'>人员</a><a href='#' onclick='choiceMan(this);'>岗位</a><a href='#' onclick='choiceRole(this);'>领导</a></div><span class='showmanInfo' id='' style='position: absolute;z-index:99;left:0px;background-color:rgb(199, 199, 199);border-radius: 5px;padding: 5px;font-size: 10px;top: 48px;line-height: 15px;display:none;min-width:100px;min-height:28px;color: #333;'></span></div>"+
			/* "<div class='choicecontent' id='' style='position: absolute;z-index:99;left:230px;background-color:white;display:none;'><a href='#' onclick='choiceMan(this);'>人员</a><a href='#' onclick='choiceMan(this);'>岗位</a><a href='#' onclick='choiceRole(this);'>角色</a></div>"+ */
		"</div>";
     $(newEl).insertAfter(s.parentNode.parentNode);
 }
 function deletejp(s){
	 var parentnode=s.parentNode.parentNode.parentNode;
	 parentnode.removeChild(s.parentNode.parentNode);
 }
 function choiceMan(s){
	span=s;
 	var k="man";
	var store;
	if(span.parentNode.parentNode.childNodes[1].getAttribute("data-contact")!="" && span.parentNode.parentNode.childNodes[1].getAttribute("data-contact")!=null && s.innerHTML=="人员" && span.parentNode.parentNode.childNodes[1].getAttribute("data-type")=='assignee'){
		var selete=span.parentNode.parentNode.childNodes[1].getAttribute("data-contact").split(",");
	}else if(span.parentNode.parentNode.childNodes[1].getAttribute("data-contact")!="" && span.parentNode.parentNode.childNodes[1].getAttribute("data-contact")!=null && s.innerHTML=="岗位" && span.parentNode.parentNode.childNodes[1].getAttribute("data-type")=='candidate-groups'){
		var selete=span.parentNode.parentNode.childNodes[1].getAttribute("data-contact").split(",");
	}
	else{
		var selete=[];
	}
	
	if(span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!="" && span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!=null && s.innerHTML=="人员" && span.parentNode.parentNode.childNodes[1].getAttribute("data-type")=='assignee'){
		var seleteId=span.parentNode.parentNode.childNodes[1].getAttribute("data-code").split(",");
	}else if(span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!="" && span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!=null && s.innerHTML=="岗位" && span.parentNode.parentNode.childNodes[1].getAttribute("data-type")=='candidate-groups'){
		var seleteId=span.parentNode.parentNode.childNodes[1].getAttribute("data-code").split(",");
	}
	else{
		var seleteId=[];
	}
	var me = this;
	var requestUrl  = '';
	if(s.innerHTML=="人员"){
		 var Morgname="or_name",Mname="em_name",Mcode="em_code"; 
		 requestUrl = basePath+'common/getSimpleOrgAssignees.action';
	 }else{ 
		var Morgname="JO_ORGNAME",Mname="",Mcode="";
		 requestUrl = basePath+'common/getSimpleJobOfOrg.action';
	 }
	Ext.Msg.wait('获取数据中...');
	Ext.Ajax.request({//拿到tree数据       	
		url:requestUrl,        	
    	method:'post',
    	timeout:60000,
    	success: function(response){
    		Ext.Msg.hide();
    		res = new Ext.decode(response.responseText);
     		if(res.tree){
    		store = new Ext.decode(res.tree);
    		var window=getWindow(store,selete,seleteId,Morgname,Mname,Mcode,span);
    		window.show();
           /*  me.orgTree.cleanCheck(); */
           var roonodes = me.orgTree.getRootNode().childNodes;   //获取主节点
           findchildnode(roonodes);  
           function findchildnode(node){
               for(var i=0;i<node.length;i++){  //从节点中取出子节点依次遍历
            	   var rootnode = node[i];
                   for(var y=0;y<seleteId.length;y++){
                	    for(var j=0;j<node[i].childNodes.length;j++){
                		   if(seleteId[y]==node[i].childNodes[j].data.qtip){
                			   node[i].childNodes[j].set("checked",true);
                		   }
                	   } 
                   }
               }
           }
            window.items.items[0].items.items[3].setValue(selete); //设置  已选择    */
    		}  
    	}
    });
 }
 function choiceRole(s){
	 span=s;
	 if(span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!="" && span.parentNode.parentNode.childNodes[1].getAttribute("data-code")!=null && s.innerHTML=="领导" && span.parentNode.parentNode.childNodes[1].getAttribute("data-type")=='rolAssignee'){
			var seleteId=span.parentNode.parentNode.childNodes[1].getAttribute("data-code");
		}
		else{
			var seleteId="";
		}
	 this.createRoleWindow(s,seleteId);
 }
 function getWindow(store,selete,seleteId,Morgname,Mname,Mcode,span) {
     selectWindow = this.createWindow(store,selete,seleteId,Morgname,Mname,Mcode);
     return selectWindow;
 }
 function createRoleWindow(s,seleteId){
		  var formpanel = new Ext.form.FormPanel(
				{
					region : 'west',
					width : '100%',
					id:'rolechoiceform',
					defaultType: 'radiofield',
					layout: 'vbox',
					items : [
										{
										    boxLabel  : '上节点组织领导',
										    name      : 'role',
										    inputValue: 'm',
										    id        : 'radio1',
										}, /* {
										    boxLabel  : '上一步父组织负责领导',
										    name      : 'role',
										    inputValue: 'l',
										    id        : 'radio2'
										}, */ {
										    boxLabel  : '上节点岗位领导',
										    name      : 'role',
										    inputValue: 'xl',
						                    id        : 'radio3'
						                }/* ,{
										    boxLabel  : '上一步岗位直属领导',
										    name      : 'role',
										    inputValue: 'xll',
						                    id        : 'radio4'
						                } */
					 ],
				listeners : {
					 beforerender:function(form, eOpts ){
							  for(var i=0;i<form.items.items.length;i++){
								  if(form.items.items[i].boxLabel==seleteId){
									   Ext.getCmp(form.items.items[i].id).setValue(true); //或者setValue("on")
								  }
							  }
							}
					}
				});
	 
		var win = new Ext.Window({
			title : '领导',
			layout : 'border',
			height : 200,
			width : 200,
			modal : true,
			items : [formpanel],
			buttonAlign:'center',
			buttons : [
					{
						text : '确定',
						handler : function(de) {
							var value="";
							for(var i=0;i<Ext.getCmp('rolechoiceform').items.items.length;i++){
								if(Ext.getCmp('rolechoiceform').items.items[i].checked==true){
									value=Ext.getCmp('rolechoiceform').items.items[i].boxLabel;
								}
							}
							confirmRole(value);
							de.ownerCt.ownerCt.close();
						},
						scope : this
					},
					{
						text : '取消',
						handler : function(de) {
							de.ownerCt.ownerCt.close();
						},
						scope : this
					}],
			listeners : {
				
			}
		});
		win.show();
 }
 function confirmRole(value){
	 span.parentNode.parentNode.childNodes[1].setAttribute("data-type", "rolAssignee");
	 span.parentNode.parentNode.childNodes[1].innerHTML = "领导:"+ value;
	 span.parentNode.parentNode.childNodes[1].setAttribute("data-contact", value);
	 span.parentNode.parentNode.childNodes[1].setAttribute("data-code", value);
	 span.parentNode.parentNode.childNodes[1].setAttribute("data-name", value);
	 span.parentNode.style.display="none";
 }
 function createWindow(store,selete,seleteId,Morgname,Mname,Mcode,span) {
 	var me = this;
     var tree = new Ext.tree.TreePanel({
         autoScroll: true,
         width:300,
         region:'center',
         checked:true,
         enableDD: false,
         containerScroll: true,
         selModel:{}/*  new Ext.tree.CheckNodeMultiSelectionModel() */, 
         rootVisible: false,
         count:1,
         listeners:{
          	'checkchange':function(node,checked){
						if (node.isLeaf()) {
								if (!checked) {
									selete = arrayremove(selete, node.data.text);
									seleteId = arrayremove(seleteId,
											node.data.id);
									this.ownerCt.items.items[0].items.items[3]
											.setValue(selete);
								} else {
									if (selete.indexOf(node.data.text) == -1) {
										selete.push(node.data.text);
										seleteId.push(node.data.id);
										this.ownerCt.items.items[0].items.items[3].setValue(selete);
									}
								}
							} else {
								if (checked) {
									for (var i = 0; i < node.childNodes.length; i++) {
										node.childNodes[i].set('checked',checked);
										if (selete.indexOf(node.childNodes[i].data.text) == -1) {
											selete.push(node.childNodes[i].data.text);
											seleteId.push(node.childNodes[i].data.id);
										}
										this.ownerCt.items.items[0].items.items[3].setValue(selete);
									}
								} else if (!checked) {
									for (var i = 0; i < node.childNodes.length; i++) {
										node.childNodes[i].set('checked',checked);
										selete = arrayremove(selete,node.childNodes[i].data.text);
										seleteId = arrayremove(seleteId,node.childNodes[i].data.id);
										this.ownerCt.items.items[0].items.items[3].setValue(selete);
									}
								}
							}
						},
					}
				});
		var arrayindexof = function(val0, val1) {
			for (var i = 0; i < val0.length; i++) {
				if (val0[i] == val1) {
					return i;
				}
			}
			return -1;
		};
		var arrayremove = function(val0, val1) {
			var index = arrayindexof(val0, val1);
			if (index > -1) {
				val0.splice(index, 1);
			}
			return val0;
		};

		var root = {
			text : 'root',
			draggable : false,
			leaf : false,
			nodeType : 'async',
			children : store,
			expanded : true
		};
		tree.setRootNode(root);
		tree.cleanCheck = function(node) {
			if (typeof node == 'undefined') {
				node = this.rootVisible ? this.getRootNode() : this.getRootNode().firstChild;
			}
			if (node) {
				if (!node.isLeaf()) {
					node.ui.checkboxImg.className = 'x-tree-node-checkbox-none';
					node.attributes.checked = false;
					for (var i = 0; i < node.childNodes.length; i++) {
						this.cleanCheck(node.childNodes[i]);
					}
				}
			}

		};
		tree.getChecked = function(node) {
			var checked = [], i;
			if (typeof node == 'undefined') {
				node = this.getRootNode();
			} else if (node.ui.checkboxImg
					&& node.ui.checkboxImg.className == 'x-tree-node-checkbox-all') {
				if (node.isLeaf()) {
					checked.push(node.text);
				} else {
					node.ui.checkboxImg.className = 'x-tree-node-checkbox-none';
				}
			}
			if (!node.isLeaf()) {
				for (var i = 0; i < node.childNodes.length; i++) {
					checked = checked.concat(this
							.getChecked(node.childNodes[i]));
				}
			}
			return checked;
		};
		this.orgTree = tree;
		//加筛选    根据 所属组织   人员名称  以及编号 做筛选
		var formpanel = new Ext.form.FormPanel(
				{
					height : 100,
					region : 'west',
					 width : '30%',
					 labelWidth:80,
					labelAlign : 'right',
					border : false,
					defaultType : 'textfield',
					 defaults : {
						anchor : '90%',
						width : '100px !important',
					},
					bodyStyle : {
						padding : '6px 0 0'
					},
					items : [ {
						xtype : 'textfield',
						name : 'orname',
						labelWidth:60,
						//cls:'sysinit-textfield',
						fieldLabel : '所属组织'
					}, {
						xtype : 'textfield',
						labelWidth:60,
						name : 'name',
						fieldLabel : '员工名称'
					}, {
						xtype : 'textfield',
						labelWidth:60,
						name : 'code',
						fieldLabel : '员工编号'
					}, {
						xtype : 'textarea',
						name : 'selected',
						labelWidth:60,
						readOnly : true,
						//disabled:true,
						fieldLabel : '已选择',
						width:55
					} ],
					buttonAlign : 'center',
					buttons : [ {
						text : '筛选',
						iconCls : 'x-form-search-trigger',
						style : 'padding-bottom:0px',
						handler : function() {
							var condition = getCondition(this.ownerCt.ownerCt.ownerCt, Morgname,Mname, Mcode);
							if (condition) {
								var requestUrl = '';
								if (Morgname == 'or_name') {
									requestUrl = basePath
											+ 'common/getSimpleOrgAssignees.action';
								} else {
									requestUrl = basePath
											+ 'common/getSimpleJobOfOrg.action';
								}
								Ext.Ajax.request({//拿到tree数据       	
											url : requestUrl,
											timeout : 60000,
											params : {
												condition : condition
											},
											method : 'post',
											success : function(response) {
												res = new Ext.decode(response.responseText);
												if (res.tree) {
													var cstore = new Ext.decode(res.tree);
													var hisroot = tree.getRootNode();
													var cn = hisroot.childNodes, n;
													while ((n = cn[0])) {
														hisroot.removeChild(n);
													}
													var fn = function(node, ch) {
														for ( var i in ch) {
															var n = ch[i];
															if (n.text) {
																node.appendChild({
																			text : n.text,
																			draggable : false,
																			leaf : false,
																			children : n.children
																		});
															}
														}
													};
													fn(hisroot, cstore);
													tree.expandAll();
													for (var n = 0; n < seleteId.length; n++) {
														if (tree.getStore().getNodeById(seleteId[n])) {
															tree.getStore().getNodeById(seleteId[n]).set("checked",true);
														}
													}
												}

											}
										});
							}
						}
					} ]
				});
		var win = new Ext.Window({
			title : '人事',
			id:'simplejpwindow',
			layout : 'border',
			height : window.innerHeight * 0.9,
			width : 600,
			modal : true,
			items : [ formpanel, tree ],
			buttons : [
					{
						text : '确定',
						handler : function(de) {
							var value = de.ownerCt.ownerCt.items.items[0].form
									.getValues().selected;
							confirm(value);
							de.ownerCt.ownerCt.close();
						},
						scope : this
					},
					{
						text : '取消',
						handler : function(de) {
							de.ownerCt.ownerCt.close();
						},
						scope : this
					},
					{
						text : '刷新',
						handler : function(refrash) {
							var myMask = new Ext.LoadMask(Ext.getCmp('simplejpwindow').getEl(), {//也可以是Ext.getCmp('').getEl()窗口名称
		  						msg    : "正在加载数据...",//你要写成Loading...也可以
		  						msgCls : 'z-index:10000;'
		  					});
		  		    	 	myMask.show();
		  		    	 	var requestUrl = '';
							if (Morgname == 'or_name') {
								requestUrl = basePath
										+ 'common/getSimpleOrgAssignees.action';
							} else {
								requestUrl = basePath
										+ 'common/getSimpleJobOfOrg.action';
							}
							 Ext.Ajax.request({//拿到tree数据       	
								url:requestUrl,        	
						    	method:'post',
						    	timeout:60000,
						    	success: function(response){
						    		myMask.hide();
						    		res = new Ext.decode(response.responseText);
						     		if(res.tree){
						    		store = new Ext.decode(res.tree);
						    		var root ={
									         text: 'root',
									         draggable: false,
									         leaf:false,
									         children:store,
									         expanded:true
									     };
						    		 tree.setRootNode(root);
						    		 selete=[];
						    		 seleteId=[];
						    		}
						    	}
						    }); 
						}
					} ],
			listeners : {
				'beforehide' : function(c) {
				
				},
				'beforeshow' : function(c) {
					
				}

			}
		});
		win.field = this;
		return win;
	};
	function confirm(value) {
		var values = value.split(",");
		var code = "";
		var name = "";
		for (var i = 0; i < values.length; i++) {
			code = code+ values[i].substring(values[i].indexOf("(") + 1, values[i].indexOf(")")) + ",";
		}
		for (var i = 0; i < values.length; i++) {
			name = name + values[i].substring(0, values[i].indexOf("(")) + ",";
		}
		if (span.innerHTML == "人员") {
			span.parentNode.parentNode.childNodes[1]
					.setAttribute("data-type", "assignee");
			span.parentNode.parentNode.childNodes[1].innerHTML = "人员:"
					+ name.substring(0, name.length - 1);
		}
		if (span.innerHTML == "岗位") {
			span.parentNode.parentNode.childNodes[1]
					.setAttribute("data-type", "candidate-groups");
			span.parentNode.parentNode.childNodes[1].innerHTML = "岗位:"
					+ name.substring(0, name.length - 1);
		}
		if (span.innerHTML == "领导") {
			span.parentNode.parentNode.childNodes[1]
					.setAttribute("data-type", "rolAssignee");
			span.parentNode.parentNode.childNodes[1].innerHTML = "领导:"
					+ name.substring(0, name.length - 1);
		}
		span.parentNode.parentNode.childNodes[1].setAttribute("data-contact", value);
		span.parentNode.parentNode.childNodes[1].setAttribute("data-code", code.substring(0,code.length-1));
		span.parentNode.parentNode.childNodes[1].setAttribute("data-name", name.substring(0,name.length-1));
		/*  var code=value.substring(value.indexOf("(")+1,value.indexOf(")")); */
		 span.parentNode.style.display="none";
	}
	function getCondition(win, Morgname, Mname, Mcode) {
		var form = win.items.items[0].form;
		var values = form.getValues();
		var orname = values.orname;
		var name = values.name;
		var code = values.code;
		var condition = "";
		if ((!orname && !name && !code)
				|| (orname == "" && name == "" && code == "")) {
			return null;
		} else {
			condition += (orname == null || orname == "") ? "1=1 #" : " "
					+ Morgname + " like '%" + orname + "%' #";
			condition += (name == null || name == "") ? "1=1 " : " " + Mname
					+ " like '%" + name + "%'";
			condition += (code == null || code == "") ? " " : " and " + Mcode
					+ " like '%" + code + "%'";
			return condition;
		}
	}
	//审批流启用控制
	function changeJpEnable(s){
		var jd_enabled;
		if(s.getAttribute("data-value")=='是'){
			jd_enabled='否';
		}else{
			jd_enabled='是';
		}
		Ext.Ajax.request({
			url : basePath + 'common/updateJpEnabled.action',
			params : {
				jd_id :s.getAttribute("data-id"),
				jd_enabled :jd_enabled
			},
			callback : function(options, success, response) {
				var res = new Ext.decode(response.responseText);
				if (res.success) {
					if(s.getAttribute("data-value")=='是'){
						showResult('提示','流程已不启用！');
						s.setAttribute("data-value","否");
					}else{
						s.setAttribute("data-value","是");
						showResult('提示','流程已启用！');
					}
				} else if (res.exceptionInfo) {
					showError(res.exceptionInfo);
				}
			}
		});
	}
	//基础资料grid
	function changeEnable(f) {
		var flag = f.getAttribute("name");
		var keyvalue = f.getAttribute("data-id");
		if (keyvalue) {
			var v = Ext.ComponentQuery.query('defaultpanel[flag="' + flag
					+ '"]')[0];
			var grid = v.down('simpleactiongrid');
			if (f.checked) {//添加
				grid.adddetail(grid, keyvalue);
			} else {//删除
				var res=grid.removeDetail(grid, keyvalue);
				if(!res){
					f.checked = true;
				}
			}
		} else {
			f.checked = false;
		}
	}
	Ext.Loader.setConfig({
		enabled : true
	});//开启动态加载
	Ext.application({
		name : 'erp',//为应用程序起一个名字,相当于命名空间
		appFolder : basePath + 'app',//app文件夹所在路径
		controllers : [//声明所用到的控制层
		'sys.SysInit' ],
		launch : function() {
			Ext.create('erp.view.sys.ViewPort');//创建视图
		}
	});
	function show(s,e){
		/* s.parentNode.childNodes[4].innerHTML=s.parentNode.childNodes[1].innerHTML.substring(3,s.parentNode.childNodes[1].innerHTML.length); */
		if(s.parentNode.childNodes[1].innerHTML.substring(3,s.parentNode.childNodes[1].innerHTML.length)!="" && s.parentNode.childNodes[1].innerHTML!="[新节点]"){
			s.parentNode.childNodes[4].innerHTML=s.parentNode.childNodes[1].innerHTML.substring(3,s.parentNode.childNodes[1].innerHTML.length);
			s.parentNode.childNodes[4].style.display="block";
			s.parentNode.childNodes[4].style.left=event.layerX+'px';
			s.parentNode.childNodes[4].style.top=event.layerY+'px';
		}
	}
	function hide(s){
		s.parentNode.childNodes[4].style.display="none";
	}
	function changeModule(type) {
		if(type.getAttribute("value")==newvalue){
			return false;
		}
		var initPortal = Ext.getCmp('syspanel');
		var lis = document.getElementById('progress')
				.getElementsByTagName('li');
		var disabled = 1;
		table = type.getAttribute("data-table");
		for (var x = 0; x < initabled.length; x++) {
			if (initabled[x].VALUE == newvalue) {
				/*  if(initabled[x].INITABLED==1){
					Ext.getCmp('confirm').hide();
				}else{
					Ext.getCmp('confirm').show();
				} */
			}
			if (initabled[x].VALUE == newvalue && initabled[x].INITABLED == 0) {
				disabled = 0;
			}
		}
		if (disabled == 0) {
			Ext.Msg.confirm('提示', '[' + nowhtml + ']初始化未完成,是否切换到其他界面?',
					function(choice) {
						if (choice === 'yes') {
							beginchangeModule(type);
						} else {
							return false;
						}
					});
		} else {
			beginchangeModule(type);
		}
	}
	function beginchangeModule(type) {
		nowvalue = newvalue;
		newvalue = type.getAttribute("value");
		newhtml = type.getElementsByTagName('span')[1].innerHTML;
		nowhtml = newhtml;
		var initPortal = Ext.getCmp('syspanel');
		var lis = document.getElementById('progress')
				.getElementsByTagName('li');
		for (var x = 0; x < initabled.length; x++) {
			if (initabled[x].VALUE == newvalue) {
				if(initabled[x].INITABLED==1){
					Ext.getCmp('confirm').hide();
					Ext.getCmp('row').hide();
				}else{
					Ext.getCmp('confirm').show();
					Ext.getCmp('row').show();
					Ext.getCmp('row').getEl().update('请确认您的【'+newhtml+'】初始化工作已完成?');
				}
			}
		}
		if(newvalue=='enterprise'||newvalue=='PreProduct1'){
			Ext.getCmp('confirm').hide();
			Ext.getCmp('row').hide();
		}
		for (var i = 0; i < lis.length; i++) {
			if (lis[i].getAttribute("value") == type.getAttribute("value")) {
				title = lis[i].getElementsByTagName('span')[1].innerHTML;
				if (lis[i].getElementsByTagName('span')[1].innerHTML == '企业信息') {
					lis[i].setAttribute("class", "active");
				} else {
					lis[i].setAttribute("class", "normal active");
				}
				dept = lis[i].getAttribute("data-dept");
				index = parseInt(dept);
				flag = lis[i].getAttribute("data-flag");
				importcaller = lis[i].getAttribute("data-importcaller");
			} else {
				if (lis[i].getElementsByTagName('span')[1].innerHTML == '企业信息') {
					lis[i].setAttribute("class", "start");
				} else {
					lis[i].setAttribute("class", "normal");
				}
			}
		}
		var allchildren = type.parentNode.parentNode.parentNode.children;
		for (var y = 0; y < allchildren.length; y++) {
			if (type.parentNode.parentNode == allchildren[y]) {
				allchildren[y].getElementsByTagName('font')[0].setAttribute(
						"class", "bluefont");
			} else {
				allchildren[y].getElementsByTagName('font')[0].setAttribute(
						"class", "normalfont");
			}
		}
		var syspanel = Ext.getCmp('syspanel');
		syspanel.changeCard(syspanel, null, index, flag, importcaller, title);
	}
	var emid =
<%=session.getAttribute("em_uu")%>;
var activeItem = null;
var installtype='${installtype}';
var caller='';
</script>
</head>
<body>
	<div id="legalese" style="display: none;">
		<h2>使用条款</h2>
	</div>
</body>
</html>