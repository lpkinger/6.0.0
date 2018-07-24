/**
 * 批量作废按钮
 */	
Ext.define('erp.view.core.button.VastCancel',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastCancelButton',
		text: $I18N.common.button.erpVastCancelButton,
    	tooltip: '可以重启多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastCancelButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});