<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@page import="com.uas.erp.model.Employee"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
Object obj = session.getAttribute("employee");
long em_uu = 0;
if(obj != null) {
	Employee employee = (Employee)obj;
	em_uu = employee.getEm_uu()==null?0:employee.getEm_uu();
}
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css"/>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<link rel="stylesheet" type="text/css" href="<%=basePath %>resource/ux/css/CheckHeader.css" /> 
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/Export.js"></script>
<script type="text/javascript" src="<%=basePath%>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<style type="text/css">
.btn-search,.btn-inviteVendor{
	color: #fff !important;
    background-color: #428bca;
    border-color: #357ebd;
    display: inline-block;
    margin-bottom: 0;
    font-weight: normal;
    text-align: center;
    vertical-align: middle;
    touch-action: manipulation;
    cursor: pointer;
    background-image: none;
    border: 1px solid transparent;
    white-space: nowrap;
    padding: 4px 8px;
    margin: 2px 15px 0px 0px;
    font-size: 1px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}
.btn-inviteVendor span,.btn-search span{
	color: #fff !important;
}
.btn-search{
    background-color: #428bca;
    border-color: #357ebd;
}
.btn-inviteVendor{
    background-color: #F39800;
    border-color: #F39800;
}

#keySearch{
	margin-right: 20px;
} 
.ico-invite {
	background: url('<%=basePath %>/resource2/resources/images/invite.png') no-repeat;
}
.ico-qq {
	background: url('<%=basePath %>/resource2/resources/images/qq.png') no-repeat;
}
.ico-close {
	background: url('<%=basePath %>/jsps/oa/doc/resources/images/images/button/close.png') no-repeat;
}
.invitetip {
	font-family: "microsoft yahei";
	font-size: 13px;
	color: #787878;
}

.invitetip span {
	color: #D08200;
}
.btn-invite,.btn-close{
	position:relative;
}
.alreadyInviteTip a{
	color:#7495CF;
}
.alreadyInviteTip{
	font-family: "microsoft yahei";
	font-size: 13px;
	color: #787878;
}
.warmInput .x-form-item-body input{
	border-color: red; 
}
.warmInputB{
	margin-top: -20px !important;
}
#invitByQQ{
	cursor: pointer;
    text-underline: initial;
    display: block;
    width: 100px;
    height: 100px;
    border: solid #fff 1px;
    border-radius: 50px;
    background-image: url('<%=basePath %>/resource2/resources/images/qq.png') ;
    background-repeat: no-repeat;
    background-position: center;
    position: absolute;
    left: 50%;
    top: 50%;
    margin-top: -50px;
    margin-left: -50px;
	
}
#invitByQQ:hover{
	border-color: #dcdcdc;
}

</style>
<script type="text/javascript">
var em_name = '<%= session.getAttribute("em_name") %>';
var em_uu = <%= em_uu %>;
var en_uu = '<%=session.getAttribute("en_uu")%>';
function inviteNew(){
	Ext.create('Ext.window.Window', {
			id: 'inviteNew',
			width: 500,
			height: 300,
			closeAction: 'destroy',
			title: '<h1>邀请企业注册</h1>',
			items: [{
					xtype: 'form',
					id:'inviteNewForm',
					layout: {
	                    type: 'vbox',
	                    align: 'center'
	                },
	                msgTarget:'side',
					width: '100%',
					height: '100%',
					bodyStyle:{
						padding:'20px 0 0 0'
					},
					defaults: {
						xtype: 'textfield',
						labelAlign: "right",
						xtype: 'textfield',
						cls: 'invite-text',
						width: 370,
						height: 24,
						labelWidth: 70
					},
					items: [
						{
							xtype: 'displayfield',
							id:'invitetip',
							width: 410,
							height: 24,
							hideLabel:true,
							value: '<p class=\'invitetip\'>请输入<span>完整的企业名称</span>,系统将根据企业名称验证企业是否已注册优软云</p>'
						},{
							fieldLabel: '企业名称',
							xtype: 'textfield',
							id:'companyName',
							xtype: 'textfield',
							labelAlign: "right",
							xtype: 'textfield',
							cls: 'invite-text'
						},{
							xtype: 'displayfield',
							id:'alreadyInviteTip',
							labelSeparator:'',
							fieldLabel: ' ',
							cls: '',
							hidden:true,
							value: '<p class=\'alreadyInviteTip\'>您邀请企业已注册优软云 <a href=\'#\' onclick = showCompanyMsg()>查看详情</a></p>'
						}, {
							fieldLabel: '联系人',
							id:'companyContact',
							style:"margin-top:15px;"
						}, {
							fieldLabel: '联系电话',
							id:'companyContactTel',
							style:"margin-top:15px;"
						}
					],
					buttonAlign: 'center',
					buttons: [{
						xtype: 'button',
						text: '发送邀请',
						cls:'btn-invite',
						iconCls: 'ico-invite',
						handler: function(btn) {
							var companyName = Ext.getCmp('companyName');
							var companyContact = Ext.getCmp('companyContact');
							var companyContactTel = Ext.getCmp('companyContactTel');
							var form = Ext.getCmp('inviteNewForm');
							//判断 companyName 是否为空
							if(companyName&&companyName.value){
								//不为空 判断联系人+联系电话 （请填写完整的邀请信息，包含联系人和联系电话）
								var companyContact = Ext.getCmp('companyContact');
								var companyContactTel = Ext.getCmp('companyContactTel');
								if(companyContact&&companyContact.value&&companyContactTel&&companyContactTel.value){
									//不为空，则调用接口判断是否已注册
									checkCompany(companyName.value);
								}else{
									showError("请填写完整的邀请信息，包含联系人和联系电话。");
								}
							}else{
								//companyName为空  默认群分享 分享邀请信息
								inviteCompany("group");
							}
						}
					},{
						xtype: 'button',
						columnWidth: 0.1,
						text: '取消',
						width: 60,
						cls:'btn-close',
						iconCls: 'ico-close',
						margin: '0 0 0 10',
						handler: function(btn) {
							var win = btn.up('window');
							win.close();
						}
					}]
			}]
		}).show();
}
function showCompanyMsg(){
	var companyInfo = Ext.getCmp("searchForm").companyInfo;
	var inviteNew = Ext.getCmp('inviteNew');
		inviteNew.close();
	Ext.create('Ext.window.Window', {
		id: 'showCompanyMsg',
		width: 450,
		height: 350,
		closeAction: 'destroy',
		title: '<h1>企业详情</h1>',
		items: [{
				xtype: 'form',
				id:'inviteNewForm',
				layout: {
                    type: 'vbox',
                    align: 'center'
                },
                width:'100%',
                height:'100%',
				bodyStyle:'padding:20px 0 0 0',
				defaults: {
					xtype: 'textfield',
					labelAlign: "right",
					xtype: 'textfield',
					cls: 'invite-text',
					width: 370,
					height: 24,
					labelWidth: 70,
					readOnly:true
				},
				items: [{
					fieldLabel: '企业名称',
					value:companyInfo.enName
				},{
					fieldLabel: '地址',
					value:companyInfo.enAddress
				},{
					fieldLabel: '法人',
					value:companyInfo.enName
				},{
					fieldLabel: '管理员',
					value:companyInfo.adminName
				},{
					fieldLabel: '联系电话',
					value:companyInfo.enTel
				},{
					fieldLabel: '注册日期',
					value: Ext.Date.format(new Date(companyInfo.enEstablishDate),'Y-m-d H:i:s')
				},{
					fieldLabel: '邀请人',
					value:companyInfo.inviteUserName
				},{
					fieldLabel: '邀请企业',
					value:companyInfo.inviteEnName
				}],
				buttonAlign: 'center',
				buttons: [{
							xtype: 'button',
							columnWidth: 0.1,
							text: '关闭',
							width: 60,
							cls:'btn-close',
							iconCls: 'ico-close',
							margin: '0 0 0 10',
							handler: function(btn) {
								var win = btn.up('window');
								win.close();
							}
						}]
		}]
	}).show();
}
function checkCompany(name){
	// addNewPartner
	//后台判断是否存在 返回三个参数：
	var returns ={};
	Ext.Ajax.request({
		url: basePath + '/ac/checkCompanyExist.action',
		params:{
			name:name
		},
		method: 'get',
        async:false,
        callback: function(options, success, response) {
            var res = new Ext.decode(response.responseText);
			var alreadyInviteTip = Ext.getCmp('alreadyInviteTip');
			var companyName = Ext.getCmp('companyName');
			var companyContact = Ext.getCmp('companyContact');
			var companyContactTel = Ext.getCmp('companyContactTel');
			 if (res.exceptionInfo) {
                showError(res.exceptionInfo);
                return false;
            } 
			if(res.notExist){// success :ture 没有注册
				// ture 未注册出现分享界面，分享邀请信息
				if(!alreadyInviteTip.hidden){
					companyName.removeCls('warmInput');
					companyContact.removeCls('warmInputB');
					companyContactTel.removeCls('warmInputB');
					alreadyInviteTip.setVisible(false);
				}
				inviteCompany("ptop",name,companyContact.value,companyContactTel.value);
			}
			if(res.isExist){//注册已存在
				var form = Ext.getCmp("searchForm");
				//false 已注册展示： 并提供查看详情接口
				if(alreadyInviteTip.hidden){
					companyName.addCls('warmInput');
					companyContact.addCls('warmInputB');
					companyContactTel.addCls('warmInputB');
					alreadyInviteTip.setVisible(true);
				}
				if(form){
					form.companyInfo = res.data;
				}
			}
			
		}
	});
}
function inviteCompany(type,name,vendusername,userTel){
	//调用接口 记录邀请记录于平台，同时返回 当前邀请人和邀请人uu 当前邀请链接（需要根据类型、账套环境判断）
	// ptop 表示点对点邀请  group 表示群邀请
	var url ="https://sso.ubtob.com/register/enterpriseRegistration?inviteUserUU="+em_uu+"&inviteSpaceUU="+en_uu+"&invitationTime="+Date.parse(new Date())+"&source=UAS";
	Ext.Ajax.request({
			url: basePath + '/ac/getInviteUrl.action',
			params:{
				name: name,
				vendusername: vendusername,
				userTel: userTel,
				type: type
			},
			method: 'get',
	        async:true,
	        callback: function(options, success, response) {
	            var res = new Ext.decode(response.responseText);
				 if (res.exceptionInfo) {
	                showError(res.exceptionInfo);
	                return false;
	            } 
			}
		});
		Ext.create('Ext.window.Window', {
			id: 'sendMyInvite',
			width: 215,
			height: 215,
			closeAction: 'destroy',
			title: '<h1>分享邀请链接</h1>',
			items: [{
				xtype:'panel',
				width:'100%',
				height:'100%',
				items: [{
					xtype:'button',
					id:'invitByQQ',
					handler : function() {
						 var p = { 
				        	url : url,
					        title : '来自'+em_name+"的邀请", /*分享标题(可选)*/
					        summary : '邀您注册优软平台', /*分享摘要(可选)*/
					        site : '优软云'+em_name,
					        style:'100',
							width:50,
							height:50
						};
						var s = [];
						for(var i in p){
							s.push(i + '=' + encodeURIComponent(p[i]||''));
						}
			        	window.open('http://connect.qq.com/widget/shareqq/index.html?' + s.join('&')); 
			        	var inviteNew = Ext.getCmp('inviteNew');
						inviteNew.close();
						var sendMyInvite = Ext.getCmp('sendMyInvite');
						sendMyInvite.close();
				}
				}]
			}]
		}).show();
}
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.purchase.MoreInviteInfo'
    ],
    launch: function() {
        Ext.create('erp.view.scm.purchase.moreInviteInfo.Viewport');
    }
});
var urlcondition = getUrlParam('urlcondition');
var formCondition = '';
var gridCondition = '';
var pageSize =20;
</script>
</head>
<body>
</body>
</html>