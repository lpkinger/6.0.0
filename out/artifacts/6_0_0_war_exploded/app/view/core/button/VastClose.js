/**
 * 批量结案按钮
 */	
Ext.define('erp.view.core.button.VastClose',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastCloseButton',
		text: $I18N.common.button.erpVastCloseButton,
    	tooltip: '可以结案多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastCloseButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});