/**
 * 批量反过账按钮
 */	
Ext.define('erp.view.core.button.VastResPost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastResPostButton',
		text: $I18N.common.button.erpVastResPostButton,
    	tooltip: '可以重启多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastResPostButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});