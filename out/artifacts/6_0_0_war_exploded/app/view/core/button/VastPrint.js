/**
 * 批量打印按钮
 */	
Ext.define('erp.view.core.button.VastPrint',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastPrintButton',
		text: $I18N.common.button.erpVastPrintButton,
    	tooltip: '点击进入批量选择模式，可以回复多条记录',
    	iconCls: 'x-button-icon-print',
    	id:'erpVastPrintButton',
    	cls: 'x-btn-gray',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});