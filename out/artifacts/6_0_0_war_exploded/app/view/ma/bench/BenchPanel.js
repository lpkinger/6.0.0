Ext.define('erp.view.ma.bench.BenchPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias:'widget.erpBenchPanel',
	layout:'card',
	id:'benchs',
	border:false,
	title: '工作台场景',
	activeItem: 0,
	tools : [{
		xtype: 'button',
		text:'工作台按钮',
		id:'benchbtn',
		cls: 'x-btn-gray',
		iconCls:'x-button-icon-code',
		disabled : true,
		style:'margin-right:10px'
	}],
	initComponent : function(){ 
		this.callParent(arguments); 
	} 
});