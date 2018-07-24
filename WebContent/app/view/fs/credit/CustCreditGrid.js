Ext.define('erp.view.fs.credit.CustCreditGrid',{ 
	extend: 'Ext.grid.Panel', 
	alias: 'widget.erpCustCreditGridPanel',
	requires: ['erp.view.core.toolbar.Toolbar', 'erp.view.core.plugin.CopyPasteMenu'],
	id: 'custCreditGrid', 
 	emptyText : $I18N.common.grid.emptyText,
    columnLines : true,
    autoScroll : true,
    store: Ext.create('Ext.data.Store', {      
	    fields: [{name: "CRA_ID", type: "number"},
	    	{name: "CRA_CUVECODE", type: "string"},
			{name: "CRA_CUVENAME", type: "string"},
			{name: "CRA_YEARMONTH", type: "number"},
			{name: "CRA_SYSSCORE", type: "number"},
			{name: "CRA_SYSDATE", type: "string"},
			{name: "CRA_MANSCORE", type: "number"},
			{name: "CRA_MANDATE", type: "String"},
			{name: "CRA_MANSTATUS", type: "string"},
			{name: "CRA_FINALSTATUS", type: "string"}
		]       
    }),
    bodyStyle:'background-color:#f1f1f1;',
    plugins: [Ext.create('Ext.grid.plugin.CellEditing', {
        clicksToEdit: 1
    }), Ext.create('erp.view.core.plugin.CopyPasteMenu')],
	initComponent : function(){ 
		var columns = [{
			text : '编号',
			xtype: 'rownumberer', 
			width: 35, 
			cls: 'x-grid-header-1', 
			align: 'center'
		},{
			text : '申请单ID',
			cls: 'x-grid-header-1', 
			dataIndex : 'CRA_ID',
			hidden:true,
			width:120
		},{
			text : '客户UU',
			cls: 'x-grid-header-1', 
			dataIndex : 'CRA_CUVECODE',
			hidden:true,
			width:120
		},{
			text : '客户名称',
			cls: 'x-grid-header-1', 
			dataIndex : 'CRA_CUVENAME',
			width:300
		},{
			text : '财务报表年份',
			cls: 'x-grid-header-1', 
			dataIndex : 'CRA_YEARMONTH',
			align:'center',
			width:100
		},{
			text : '系统评级得分',
			cls: 'x-grid-header-1', 
			xtype : "numbercolumn",
			dataIndex : 'CRA_SYSSCORE',
			width:100
		},{
			text : '系统评级日期',
			xtype:'datecolumn',
			cls: 'x-grid-header-1', 
			dataIndex : 'CRA_SYSDATE',
			format:'Y-m-d',
			width:100
		},{
			xtype : "numbercolumn",
			text : '人工评级得分',
			cls: 'x-grid-header-1', 
			width:100,
			dataIndex : 'CRA_MANSCORE'
		},{
			
			text : '人工评级日期',
			xtype:'datecolumn',
			cls: 'x-grid-header-1',
			format:'Y-m-d',
			width:100,
			dataIndex : 'CRA_MANDATE'
		},{
			text : '人工评级状态',
			cls: 'x-grid-header-1', 
			width:100,
			dataIndex : 'CRA_MANSTATUS'
		},{
			text : '最终评级状态',
			cls: 'x-grid-header-1', 
			width:100,
			dataIndex : 'CRA_FINALSTATUS'
		}]
			
		Ext.apply(this,{
			columns:columns
		});
		
    	this.callParent(arguments); 
	}
});