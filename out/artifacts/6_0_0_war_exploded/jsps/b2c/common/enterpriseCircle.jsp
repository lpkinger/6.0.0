<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.uas.erp.model.Employee"%>
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
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<style type="text/css">

.test{
	width: 500px;
    height: 28px;
    margin-top: 2px;
    margin-left: 2px;
}
.btn-primary {
    color: #fff;
    background-color: #428bca;
    border-color: #357ebd;
}
.btn {
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
    padding: 3px 3px;
    margin : -3px 0px 0px 0px;
    font-size: 1px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}
.btn-sync {
	color: #fff;
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
    padding: 3px 8px;
    margin : 4px 0px 25px 40px;
    font-size: 1px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}
.btn-search{
	color: #fff;
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
    padding: 0px 8px;
    margin : 3px 4px 0px 4px;
    font-size: 1px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}
.btn-newCompany{
	color: #fff;
    background-color: #F39800;
    border-color: #F39800;
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
    padding: 0px 8px;
    margin : 3px 4px 0px 4px;
    font-size: 1px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
}
.text-search{
    display: block;
    padding:1px 3px;
    width:100%;
    border-width:0 0 1px;
    border-radius: 3px;
    border:1px solid #000;
    border-style: solid;
    border-color: rgba(0,0,0,.12);
    box-shadow: inset 0 -1px 0 rgba(0,0,0,0);
    font-size: 16px;
    background: transparent;
    outline: none;
}
.btn-status-active{
  	background:#7CCD7C;
}
/* #enterpriseSearch{
	width:200px!important
}

#enterpriseSearch .x-form-item-body{
	width:200px!important
} 

#enterpriseSearch input{
	width:200px!important
}  */
.range{
    margin-top:30px;
	font-size:20px;
}
 .mui-switch {
  width: 35px;
  height: 18px;
  top: 5px;
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
.x-column-header-inner{
	text-align:center;
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
function openCustomer(vendName){
	var formStore = {};
	formStore.cu_name = vendName ;
	Ext.Ajax.request({
		url: basePath + '/scm/sale/saveCustomerSimple.action',
		params:{
			formStore:Ext.encode(formStore)
		},
		callback: function(opt, success, resp) {
			var rs = Ext.JSON.decode(resp.responseText);
			var id = rs.cu_id;
			Ext.create('erp.util.FormUtil').onAdd('createCustomer', '客户资料', 'jsps/scm/sale/customerBase.jsp?formCondition=cu_idIS'+id);
		}
	});
		
};

function openSeller(vendName){
	var formStore = {};
	formStore.ve_name = vendName ;
	Ext.Ajax.request({
		url: basePath + '/scm/purchase/saveVendorSimple.action',
		params:{
			formStore:Ext.encode(formStore)
		},
		callback: function(opt, success, resp) {
			var rs = Ext.JSON.decode(resp.responseText);
			var id = rs.ve_id;
			Ext.create('erp.util.FormUtil').onAdd('createCustomer', '供应商资料', 'jsps/scm/purchase/vendor.jsp?formCondition=ve_idIS'+id);
		}
	});
	
};

function openEnquiry(ve_id,ve_code,ve_name,uu){
	Ext.create('erp.util.FormUtil').onAdd('createEnquiry', '采购询价单', 'jsps/scm/purchase/inquiry.jsp?veid='+ve_id+'&vecode='+ve_code+'&vename='+ve_name+'&veuu='+uu+'');
}
//主动报价uuSet
function openQuotation(cuid,cucode,cuname){
	Ext.create('erp.util.FormUtil').onAdd('createQuotation', '销售报价单', 'jsps/scm/sale/quotation.jsp?cuid='+cuid+'&cucode='+cucode+'&cuname='+cuname+'');
}
function uuSet(ve_id){
	Ext.create('erp.util.FormUtil').onAdd('openVendor', '供应商资料', 'jsps/scm/purchase/vendor.jsp?formCondition=ve_id='+ve_id+'');
}
function customerUUSet(cu_id){
	Ext.create('erp.util.FormUtil').onAdd('openCustomer', '客户资料', 'jsps/scm/sale/customerBase.jsp?formCondition=cu_id='+cu_id+'');
}
function vendUse(id,vendorId,hasRelative,vendUID,grid){
	var btn = document.getElementById(id);
	//判断调用启用还是禁用接口 1:启用  0:禁用
	var type = 0;
	if(btn.checked){
		type = 1;
	}
	Ext.Ajax.request({
		url: basePath + 'ac/vendUse.action',
		params:{
			id : (vendorId=='null'?0:vendorId),
			hasRelative:hasRelative,
			type : type,
			vendUID : vendUID
		},
		method : 'POST',
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp(grid).store.load();
		}
	});
}
function serviceUse(id,vendorId,hasRelative,vendUID,grid){
	var btn = document.getElementById(id);
	//判断调用启用还是禁用接口 1:启用  0:禁用
	var type = 0;
	if(btn.checked){
		type = 1;
	}
	Ext.Ajax.request({
		url: basePath + 'ac/serviceUse.action',
		params:{
			id : (vendorId=='null'?0:vendorId),
			hasRelative:hasRelative,
			type : type,
			vendUID : vendUID
		},
		method : 'POST',
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp(grid).store.load()
			/* var task = new Ext.util.DelayedTask(function(){
				Ext.getCmp(grid).store.load();
			});
			//调用延迟加载对象的delay方法执行
			task.delay(3000); */
			//setTimeout(function(){Ext.getCmp(grid).store.load();},3000);
		}
	});
}
function customerUse(id,custId,hasRelative,vendUID,grid){
	var btn = document.getElementById(id);
	//判断调用启用还是禁用接口 1:启用  0:禁用
	var type = 0;
	/* if(btn.value=='启用'){
		type = 1;
		btn.value='禁用';
	}else{
		btn.value='启用';
	} */
	if(btn.checked){
		type = 1;
	}
	Ext.Ajax.request({
		url: basePath + 'ac/customerUse.action',
		params:{
			id : (custId=='null'?0:custId),
			hasRelative:hasRelative,
			type : type,
			vendUID : vendUID
		},
		method : 'POST',
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp(grid).store.load();
		}
	});
}
function invite(info,type){
	var partnerInfo = Ext.JSON.decode(info);
	var name = partnerInfo.name;
	var contact = partnerInfo.adminName;
	var tel = partnerInfo.adminTel;
	var mail = partnerInfo.adminEmail;
	Ext.create('Ext.window.Window', {
        id : 'window',
		width: 380,
        height: 220,
        closeAction: 'destroy',
        title: '<h1>邀请合作伙伴</h1>',
        items : [{
        	xtype : 'form',
        	layout : 'column',
        	defaultTtpe : 'textfield',
        	height: 180,
        	id : 'invite',
            items: [{
                margin: '15 0 0 5',
                xtype: 'textfield',
                fieldLabel: '企业名称',
                value : name,
                name: 'name',
                allowBlank : false,
                id: 'en_name'
            },{
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '联系人',
                value : (contact==null?"":contact),
                allowBlank : false,
                name: 'contact',
                id: 'contact'
            },{
            	margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '手机',
                value : (tel==null?"":tel),
                name: 'tel',
                id: 'tel',
                regex:/^1[3|4|5|7|8][0-9]{9}$/,//  /^1[0-9]{10}$/
    			regexText:'手机号必须合法'
            },{
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '邮箱',
                value : (mail==null?"":mail),
                name: 'email',
                id: 'email',
                regex : /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/,
                regexText:'邮箱必须合法'
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '邀请',
                width: 60,
                iconCls: 'x-button-icon-save',
                formbind : true,
                handler: function(btn) {
                    var w = btn.up('window');
                    var form = Ext.getCmp('invite').getForm();
                    var en_name=Ext.getCmp('en_name').value,contact=Ext.getCmp('contact').value,tel=Ext.getCmp('tel').value,email=Ext.getCmp('email').value;
                    var formStore = {};
                    if(!form.isValid()){
                    	showError("请输入正确的信息");
                    	return false;
                    }
                    if((tel==null || tel=="") && (email==null || email=="")){
                    	showError("手机邮箱必填一项！");
                    	return false;
                    }
                    formStore.vendname = en_name;
                    formStore.vendusername = contact;
                    formStore.vendusertel = tel;
                    formStore.venduseremail = email;
                    if(partnerInfo.id&&partnerInfo.id>0){
                    	formStore.id = partnerInfo.id;
                    }
                    Ext.Ajax.request({
                		url: basePath + 'ac/invite.action',
                		params:{
                			formStore : Ext.JSON.encode(formStore)
                		},
                		callback:function(opt, success, resp){
                			var rs = Ext.JSON.decode(resp.responseText);
                			if(rs.success){
                				alert('邀请成功!');
                				var grid = Ext.getCmp(type);
                				grid.store.load();
                			}
                			/* Ext.getCmp(type).store.load({params:{
                				statusCode : 311
                			}});  */
                		}
                	});
                    w.close();
                }
            },
            {
                xtype: 'button',
                columnWidth: 0.1,
                text: '取消',
                width: 60,
                iconCls: 'x-button-icon-close',
                margin: '0 0 0 10',
                handler: function(btn) {
                    var win = btn.up('window');
                    win.close();
                }
            }]
        }],
    }).show();
}
function newPartners(info,type){
	var partnerInfo = Ext.JSON.decode(info);
	var name = partnerInfo.name;
	var businessCode = partnerInfo.businessCode;
	Ext.Ajax.request({
		url: basePath + 'ac/newPartners.action',
		params:{
			name : name,
			businessCode: businessCode
		},
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp(type).store.load();
		}
	});
};
function addprevendor(info,type){
	var partnerInfo = Ext.JSON.decode(info);
	var name = partnerInfo.name;
	var businessCode = partnerInfo.businessCode;
	Ext.Ajax.request({
		url: basePath + 'ac/addprevendor.action',
		params:{
			info : info
		},
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			if(rs.success){
				alert("添加成功");
			}
			if(rs.log!=null){
				showError(rs.log);
			}
			Ext.getCmp(type).store.load();
		}
	});
};
function sync(){
	Ext.Ajax.request({
		url : basePath + 'ac/sync.action',
		callback : function(opt,suc,res){
			var rs = Ext.JSON.decode(resp.responseText);
		}
	});
};
function agreeRequest(id){
	var id =parseInt(id);
	Ext.Ajax.request({
		url: basePath + 'ac/agreeRequest.action',
		params:{
			id : id
		},
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp('myPartnerGrid1').store.load(/* {params:{
				statusCode : 311
			}} */);
		}
	});
};

function refuseRequest(id,reason){	
	Ext.Ajax.request({
		url: basePath + 'ac/refuseRequest.action',
		params:{
			id : id,
			reason : reason
		},
		callback:function(opt, success, resp){
			var rs = Ext.JSON.decode(resp.responseText);
			Ext.getCmp('myPartnerGrid1').store.load(/* {params:{
				statusCode : 311
			}} */);
		}
	});
};//maintab
function watch(info){
	var partnerInfo = Ext.JSON.decode(info);
	var tabpanel = Ext.getCmp('maintab');
	var keyword = partnerInfo.businessCode;
	tabpanel.setActiveTab(tabpanel.items.get(0));
	Ext.getCmp('myPartnerGrid1').store.load({params:{
		keyword: keyword
	}});
	
};
function invite2(keyword){
	Ext.create('Ext.window.Window', {
        id : 'window2',
		width: 380,
        height: 220,
        closeAction: 'destroy',
        title: '<h1>邀请合作伙伴</h1>',
        items : [{
        	xtype : 'form',
        	layout : 'column',
        	defaultTtpe : 'textfield',
        	height: 180,
        	id : 'invite2',
            items: [{
                margin: '15 0 0 5',
                xtype: 'textfield',
                fieldLabel: '企业名称',
                value : keyword,
                name: 'name',
                allowBlank : false,
                id: 'en_name2'
            },{
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '联系人',
                allowBlank : false,
                name: 'contact',
                id: 'contact2'
            },{
            	margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '手机',
                name: 'tel',
                id: 'tel2',
                regex:/^1[3|4|5|7|8][0-9]{9}$/,//  /^1[0-9]{10}$/
    			regexText:'手机号必须合法'
            },{
                margin: '5 0 0 5',
                xtype: 'textfield',
                fieldLabel: '邮箱',
                name: 'email',
                id: 'email2',
                regex : /^(\w-*\.*)+@(\w-?)+(\.\w{2,})+$/,
                regexText:'邮箱必须合法'
            }],
            buttonAlign: 'center',
            buttons: [{
                xtype: 'button',
                text: '邀请',
                width: 60,
                iconCls: 'x-button-icon-save',
                formbind : true,
                handler: function(btn) {
                    var w = Ext.getCmp('window2');
                    var form = Ext.getCmp('invite2').getForm();
                    var en_name=Ext.getCmp('en_name2').value,contact=Ext.getCmp('contact2').value,tel=Ext.getCmp('tel2').value,email=Ext.getCmp('email2').value;
                    var formStore = {};
                    if(!form.isValid()){
                    	showError("请输入正确的信息");
                    	return false;
                    }
                    if((tel==null || tel=="") && (email==null || email=="")){
                    	showError("手机邮箱必填一项！");
                    	return false;
                    }
                    formStore.vendname = en_name;
                    formStore.vendusername = contact;
                    formStore.vendusertel = tel;
                    formStore.venduseremail = email;
                    Ext.Ajax.request({
                		url: basePath + 'ac/invite.action',
                		params:{
                			formStore : Ext.JSON.encode(formStore)
                		},
                		callback:function(opt, success, resp){
                			var rs = Ext.JSON.decode(resp.responseText);
                			if(rs.success){
                				alert('邀请成功!');
                				//var grid = Ext.getCmp('enterpriseListGrid');
                				//grid.store.load();
                			}
                			/* Ext.getCmp(type).store.load({params:{
                				statusCode : 311
                			}});  */
                		}
                	});
                    w.close();
                }
            },
            {
                xtype: 'button',
                columnWidth: 0.1,
                text: '取消',
                width: 60,
                iconCls: 'x-button-icon-close',
                margin: '0 0 0 10',
                handler: function(btn) {
                    var win = Ext.getCmp('window2');
                    win.close();
                }
            }]
        }],
    }).show();
};
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
				var form = Ext.getCmp("addNewPartner");
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
};
function showCompanyMsg(){
	var companyInfo = Ext.getCmp("addNewPartner").companyInfo;
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
					value:companyInfo.enCorporation 
				},{
					fieldLabel: '管理员',
					value:companyInfo.adminName
				},{
					fieldLabel: '联系电话',
					value:companyInfo.enTel
				},{
					fieldLabel: '注册日期',
					value:Ext.Date.format(new Date(companyInfo.enEstablishDate),'Y-m-d H:i:s')
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
};
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
};
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
							cls: 'invite-text',
							//value:'wuyx（测试）'
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
							style:"margin-top:15px;",
							//value:'吴雨骁'
						}, {
							fieldLabel: '联系电话',
							id:'companyContactTel',
							style:"margin-top:15px;",
							//value:'13699876151'
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
Ext.Loader.setConfig({
	enabled: true
});
Ext.application({
    name: 'erp',
    appFolder: basePath+'app',
    controllers: [
        'b2c.common.enterpriseCircle'
    ],
    launch: function() {
        Ext.create('erp.view.b2c.common.enterpriseCircle');
    }
});

</script>
</head>
<body>
</body>
</html>