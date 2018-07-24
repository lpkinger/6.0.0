<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link rel="stylesheet" href="<%=basePath %>resource/ext/resources/css/ext-all-gray.css" type="text/css"></link>
<link rel="stylesheet" href="<%=basePath %>resource/css/main.css" type="text/css"></link>
<style type="text/css">
.x-panel,.x-panel-body {
	overflow: inherit;
}
.x-grid-empty,.x-grid-tip {
	position: absolute;
	display: none;
}
.x-grid-empty {
	top: 30%;
	left: 10px;
	right: 10px;
	text-align: center;
}
.x-grid-tip {
	left: 70%;
	right: 10px;
	top: -65px;
	height: 60px;	
	z-index: 10;
}
.alert {
	margin: 0 auto;
	padding: 15px;
	color: #8a6d3b;
  	background-color: #fcf8e3;
  	border: 1px solid #faebcc;
  	border-radius: 4px;
  	-webkit-box-shadow: 0 0 7px 0 rgba(119,119,119,0.2);
  	box-shadow: 0 0 7px 0 rgba(119,119,119,0.2);
}
.arrow-border:before,.arrow-border:after {
	content: '';
	position: absolute;
	bottom: 0;
	width: 0;
	height: 0;
	border: 9px solid transparent;
}
.arrow-border.arrow-bottom-right:before {
	margin-bottom: -19px;
	right: 29px;
	border-top-color: #faebcc;
	border-right-color: #faebcc;
}
.arrow-border.arrow-bottom-right:after {
	margin-bottom: -17px;
	right: 30px;
	border-top-color: #fcf8e3;
	border-right-color: #fcf8e3;
}
.x-action-col-icon {
	cursor: pointer;
}
.x-form-field-help {
	height: 21px;
	line-height: 21px;
	color: #777;
}
.x-form-field-help>i {
	height: 16px;
	padding: 0 5px 0 14px;
	margin-top: 2px;
	float: left;
}
.pull-right {
	float: right;
}
.close {
	display: block;
}
.searchBox{
	margin-right: 15px !important;
	margin-top: 30px;
}
.btn-search,.Inquiry{
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
   /*  margin-top: 28px; */
}

.x-toolbar-default{
	background:none !important;
}
#erpVendorImportProdFormPanel-body{
overflow: hidden !important;
}

.x-button-icon-import{
    background-repeat: no-repeat;
    background-position: 2px 2px;
	background-image: url('<%=basePath %>resource/images/basket_put.png') ;
}
#vendorImportBtn{
    position: unset !important;
    margin: 0px 0px 0px 30px !important;
    height: 24px;
  	padding: 0px 4px 0px 4px !important;
}	
.x-vendor-searchline,.x-vendor-psearch{
	    margin-top: 2.5% !important;
}
.x-vendor-psearch{
	margin-left:20px !important;
}
.disabledInquiry{
	background-color:#ccc !important;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>
<script type="text/javascript">	
function searchProd(info){
	var vendorInfo = Ext.JSON.decode(info);
}
function importVendor(en_uu,tabid){
	Ext.create('erp.util.FormUtil').onAddFromBench('PreVendor'+en_uu, '供应商引进', 'jsps/scm/purchase/preVendor.jsp?whoami=PreVendor&tabid=' + tabid);
}
function InquiryToVendor(index){
	var grid = Ext.getCmp('erpVendorImportProdGridPanel');
	var form = Ext.getCmp('erpVendorImportProdFormPanel');
	var currenttab = parent.Ext.getCmp("content-panel");
	var tabid = 999999999;
	if(currenttab&&currenttab.getActiveTab()){
		currenttab = currenttab.getActiveTab();
	}else{//兼容工作台
		currenttab = window.parent.parent.Ext.getCmp("content-panel").getActiveTab();
	}
	if(grid&&form){
		if(parent.Ext.getCmp(form.tabid)){
			batchInquirydatas = parent.Ext.getCmp(form.tabid).detaildatas;
			batchInquirydatas['pr_code']= grid.store.data.items[index%pageSize].data.ifMatched;
			currenttab.batchInquirydatas = batchInquirydatas;
			tabid = currenttab.id;
		}
	}
	Ext.create('erp.util.FormUtil').onAddFromBench('BatchInquiry'+en_uu, '公共询价', 'jsps/scm/purchase/batchInquiry.jsp?tabid=' + tabid);
}
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.purchase.VendorImportProd'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.vendorImportFromB2B.VendorImportProd');//创建视图
    }
});
var caller = 'VendorImpoertProd';
var formCondition = '';
var gridCondition = '';
var basePath = '<%=basePath%>';
var page = 1;
var pageSize = 20;
</script>
</head>
<body >
</body>
</html>