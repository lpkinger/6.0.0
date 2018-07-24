Ext.QuickTips.init();

Ext.define('erp.view.scm.sale.TenderPublicGridPanel',{
	extend : 'Ext.grid.Panel',
	alias : 'widget.erpTenderPublicGridPanel',
	id:'grid',
	title:'产品明细',
	plugins : [Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	store: Ext.create('Ext.data.Store', {
		fields: ['id','index', 'prodCode','brand','prodTitle', 'brand','unit', 'qty']
	}),
	columns:[{
		header:'ID',
		dataIndex:'id',
		width:80,
		hidden:true
	},{
		header:'序号',
		align : 'center',
		dataIndex:'index',
		cls : 'x-grid-header-1',
		width:40
	},{
		header:'型号',
		dataIndex:'prodCode',
		cls : 'x-grid-header-1',
		width:200
	},{
		header:'产品名称',
		dataIndex:'prodTitle',
		cls : 'x-grid-header-1',
		width:300 
	},{
		header:'品牌',
		dataIndex:'brand',
		cls : 'x-grid-header-1',
		width:100
	},{
		header:'单位',
		dataIndex:'unit',
		cls : 'x-grid-header-1',
		width:100
	},{
		header:'采购数量',
		dataIndex:'qty',
		align:"right",
		cls : 'x-grid-header-1',
		width:100
	}]
});