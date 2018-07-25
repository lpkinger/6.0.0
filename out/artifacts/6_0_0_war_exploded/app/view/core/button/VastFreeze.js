/**
 * 批量冻结按钮
 */	
Ext.define('erp.view.core.button.VastFreeze',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastFreezeButton',
		text: $I18N.common.button.erpVastFreezeButton,
    	tooltip: '可以冻结多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastFreezeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});