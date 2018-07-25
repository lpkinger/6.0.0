/**
 * 批量生成按钮(信扬)
 */	
Ext.define('erp.view.core.button.VastOccurXY',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastOccurXYButton',
		text: $I18N.common.button.erpVastOccurXYButton,
    	tooltip: '可以生成多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpVastOccurXYButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});