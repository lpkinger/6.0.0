/**
 *冻结字段筛选勾选保存面板
 */
Ext.define('erp.view.sysmng.basicset.fixed.FixedPanel',{ 
	extend: 'Ext.panel.Panel', 
	alias: 'widget.erpFixedPanel',
	id: 'fixedpanel', 
	closeAction:'hide',
	layout:{
		type: 'vbox',
        align: 'stretch'
        },
	border:0,
	cls:"x-panel-header-default",
	requires:['erp.view.sysmng.basicset.fixed.FreezeGridPanel2','erp.view.sysmng.basicset.fixed.FreezeGridPanel1','erp.view.sysmng.basicset.fixed.FreezeFormPanel'],
	FormUtil: Ext.create('erp.util.FormUtil'),
	BaseUtil: Ext.create('erp.util.BaseUtil'),
	items:[{
				region: 'center', 
				xtype:'FreezeFormPanel',
				height:50
				
			},
			{
				region: 'south', 
				xtype:'FreezeGridPanel1',
				flex:3				
			},
			{
				region: 'south', 
				xtype:'FreezeGridPanel2',
				flex:3
				
			}],
	initComponent : function(){ 
		this.callParent(arguments);		
	}
	
});