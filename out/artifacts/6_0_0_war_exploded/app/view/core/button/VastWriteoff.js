/**
 * 批量核销按钮
 */	
Ext.define('erp.view.core.button.VastWriteoff',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastWriteoffButton',
		text: $I18N.common.button.erpVastWriteoffButton,
    	tooltip: '可以核销多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastWriteoffButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});