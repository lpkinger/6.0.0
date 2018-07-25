/**
 * 可引进供应商
 * */
var vendorImportStore = Ext.create('Ext.data.Store', {
	fields : [
	{name:'title'},
	{name:'spec'},
	{name:'cmpCode'},
	{name:'unit'},
	{name:'kind'},
	{name:'minPack'},
	{name:'minOrder'},
	{name:'brand'},
	{name:'standard'},
	{name:'ifMatched'}
	],
	autoLoad : true,
	pageSize : pageSize,
	proxy : {
		type : 'ajax',
		method:'post',
		url : basePath + 'scm/purchase/getVendorImpoertProdDetail.action',
		headers : { "Content-Type" : 'application/json' },
		reader : {
			encode : true,
			type : 'json',
			root : 'data',
			totalProperty : 'count'
		},
		timeout:180000,
		extraParams:{
			caller:'VendorImpoertProd',
			ve_uu : Ext.create('erp.util.BaseUtil').getUrlParam("en_uu")
		}
	},
	listeners : {
		beforeload : function() {
			this.BaseUtil = Ext.create('erp.util.BaseUtil');
			var grid = Ext.getCmp('erpVendorImportProdGridPanel');
			var form = Ext.getCmp('erpVendorImportProdFormPanel');
			var productMatchCondition = " 1=1";
			var whereCondition = " and 1=1";
			if(form){
				whereCondition = form.getCondition();
			}
			if(grid){
				Ext.apply(grid.getStore().proxy.extraParams, {
					productMatchCondition:productMatchCondition,
					whereCondition:whereCondition
				});
			}
		},
		afterrender:function(){
			var panel = parent.Ext.getCmp('tree-tab');
			if(panel && !panel.collapsed) {
				panel.toggleCollapse();
			}
		}
	}
});
Ext.define('erp.view.scm.purchase.vendorImportFromB2B.VendorImportProdGrid',{
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpVendorImportProdGridPanel',
	region: 'south',
	layout : 'fit',
	id: 'erpVendorImportProdGridPanel',
	requires: [ 'erp.view.core.plugin.CopyPasteMenu','erp.view.core.plugin.GridMultiHeaderFilters'],
	emptyText : '无数据',
    columnLines : true,
	store: vendorImportStore,
	enMsg:"",
    plugins: [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
    columns: [
    	Ext.create('Ext.grid.RowNumberer' , {
			text : '序号',
			width : 20 ,
			height :25 ,
			align : 'center',
			cls : 'x-grid-header-1'
		}),
		{text: '物料名称', dataIndex:'title'},
        {text: '规格', dataIndex:'spec'},
        {text: '品牌', dataIndex:'brand'},
        {text: '原厂型号', dataIndex:'cmpCode'},
        {text: '单位', width : 30 ,dataIndex:'unit'},
        {text: '类目', dataIndex:'kind'},
        {text: '最小包装量',width : 40 , dataIndex:'minPack'},
        {text: '最小采购量',width : 40 , dataIndex:'minOrder'},
        /*{text: '价格', width : 30 ,dataIndex:'price'},*/
        {text: '可匹配物料号',dataIndex:'ifMatched'},
        {
          text: '操作', 
          width:80,
          align: 'center',
          renderer: function (value,metaData,record) {
          	if(record.data){
          		metaData.tdAttr = 'data-qtip="发起询价"'; 
				return "<a class='btn btn-primary Inquiry' href='#' onclick = InquiryToVendor('"+record.index+"') type='button'>发起询价</a>";
          	}else{
          		return "<a class='btn btn-primary Inquiry disabledInquiry' href='#' type='button'>发起询价</a>";
          	}
          }
        }
    ],
    forceFit: true,
    sortable: true,
	columnLines : true,
	autoScroll : true, 
	sync: true,
	bodyStyle: 'background-color:#f1f1f1;',
	bbar: Ext.create('Ext.PagingToolbar', {   
            store: vendorImportStore,   
            displayInfo: true,   
            displayMsg: '显示 {0} - {1} 条，共计 {2} 条',   
            emptyMsg: "没有数据"   
          }) ,
   listeners: {
		scrollershow: function(scroller) {
			if (scroller && scroller.scrollEl) {
				scroller.clearManagedListeners();  
				scroller.mon(scroller.scrollEl, 'scroll', scroller.onElScroll, scroller);  
			}
		}
	},
	initComponent:function(){
		this.callParent(arguments);
	}
});
