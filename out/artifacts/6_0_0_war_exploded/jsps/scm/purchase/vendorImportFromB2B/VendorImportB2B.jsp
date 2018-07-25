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

.searchBox-One{
	margin-top: 1.5% !important;
}
.searchBox{
	margin-right: 10px !important;
	margin-top: 5px;
	/* max-width:240px; */
}
#vendorProdSearch,#vendorProdSearch2{
	margin-left:20px !important;
	margin-top: 10px !important;
}
#vendorformSearch{
	margin-left:20px !important;
}
#isPrecision-labelEl{
	float: right;
	text-align: left;
}
#isPrecision-bodyEl{
	width: 19px !important;
    float: right;
}
.productDetail,.vendorName{
	float: left;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.vendorName{
    max-width: 180px;
}
.showProductDetail,.showVendorDetail,.importVendor{
    color: #fff;
    background-color: #357ebd;
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
    margin: 3px 4px 0px 4px;
    font-size: 13px;
    line-height: 1.42857143;
    border-radius: 4px;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    text-decoration: none;
}
.showProductDetail,.showVendorDetail{
	float:right;
}
.importVendor{
	margin-top: -2px;
}
.vdetail-left{
	margin-left:10px
}
.vdetail-one{
	margin-top:10px
}
.vdetail-top{
	margin-top:5px
}
::-webkit-input-placeholder { /* WebKit browsers */
    color:    #999;
}
:-moz-placeholder { /* Mozilla Firefox 4 to 18 */
    color:    #999;
}
::-moz-placeholder { /* Mozilla Firefox 19+ */
    color:    #999;
}
:-ms-input-placeholder { /* Internet Explorer 10+ */
    color:    #999;
}
.x-form-field{
	color:    #000;
}
#menus{
	position:relative;
	x:0px;
	y:0px;
}
.x-button-icon-import{
	background-image: url('<%=basePath %>resource/images/basket_put.png') ;
}
.productDetail{
	width:99.9%;
}
#query{
	margin-top: 2px;
}
</style>
<script type="text/javascript" src="<%=basePath %>resource/ext/ext-all.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/i18n/i18n.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/grid/GridHeaderFilters.js"></script>
<script type="text/javascript" src="<%=basePath %>resource/ux/data/PagingMemoryProxy.js"></script>

<script type="text/javascript">
function showProductDetail(index){
	var grid = Ext.getCmp('erpVendorImportGridPanel');
	var form = Ext.getCmp('erpVendorImportFormPanel');
	var detaildatas = grid.store.data.items[index%pageSize].data;
	var currenttab = parent.Ext.getCmp("content-panel");
	if(currenttab&&currenttab.getActiveTab()){
		currenttab = currenttab.getActiveTab();
	}else{//兼容工作台
		currenttab = window.parent.parent.Ext.getCmp("content-panel").getActiveTab();
	}
	
	currenttab.detaildatas = detaildatas;
	currenttab.searchcondition = getFormCondition();
	url = 'jsps/scm/purchase/vendorImportFromB2B/VendorImportProd.jsp?whoami=VendorImpoertProd'
			+'&en_uu='+detaildatas.en_uu
			//+'&productMatchCondition='+condition
			+'&tabid=' + currenttab.id;
	var newTabId = 'VendorImpoertProd'+detaildatas.en_uu;
	if(Ext.getCmp(newTabId)){
		
	}
	Ext.create('erp.util.FormUtil').onAddFromBench(newTabId, '供应商物料列表', url);
}
function getFormCondition(){
	var searchcondition = {};
	var vendorProdSearch = Ext.getCmp('vendorProdSearch');
	if(vendorProdSearch){
		var items = Ext.getCmp('vendorProdSearch').items.items;
		searchcondition['pr_brand']= Ext.getCmp('pr_brand').value;
		searchcondition['pr_cmpcode'] = Ext.getCmp('pr_orispeccode').value;
		Ext.Array.each(items, function(item){
			if((item.xtype=='textfield'||item.xtype=='dbfindtrigger')&&item.value&&item.value!=null&&item.value!=''){
				searchcondition[item.name] = item.value;
			}
		});
	}
	return searchcondition;
}
function importVendor(index){
	var currenttab = parent.Ext.getCmp("content-panel");
	if(currenttab&&currenttab.getActiveTab()){
		currenttab = currenttab.getActiveTab();
	}else{//兼容工作台
		currenttab = window.parent.parent.Ext.getCmp("content-panel").getActiveTab();
	}
	var detaildatas = Ext.getCmp('erpVendorImportGridPanel').store.data.items[index%pageSize].data;
	currenttab.detaildatas = detaildatas;
	Ext.create('erp.util.FormUtil').onAddFromBench('PreVendor'+detaildatas.en_uu, '供应商引进', 'jsps/scm/purchase/preVendor.jsp?whoami=PreVendor&tabid=' + currenttab.id);
}
function onmenucheck(item){
   	var en_profession = Ext.getCmp("en_profession");
   	en_profession.setValue(item.text);  
} 
Ext.Loader.setConfig({
	enabled: true
});//开启动态加载
Ext.application({
    name: 'erp',//为应用程序起一个名字,相当于命名空间
    appFolder: basePath+'app',//app文件夹所在路径
    controllers: [//声明所用到的控制层
        'scm.purchase.VendorImport'
    ],
    launch: function() {
    	Ext.create('erp.view.scm.purchase.vendorImportFromB2B.VendorImport');//创建视图
    }
});
var caller = 'VendorImportFromB2B';
var formCondition = '';
var gridCondition = '';
var basePath = '<%=basePath%>';
var page = 1;
var pageSize = 20;
var veUU = 0;
</script>
<script type="text/javascript" src="profession.js"></script>
</head>
<body >
</body>
</html>