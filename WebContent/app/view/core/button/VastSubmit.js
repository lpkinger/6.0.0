/**
 * 批量提交按钮
 */	
Ext.define('erp.view.core.button.VastSubmit',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSubmitButton',
		text: $I18N.common.button.erpVastSubmitButton,
    	tooltip: '点击进入批量选择模式，可以提交多条记录',
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray-1',
    	width: 90,
    	id: 'erpVastSubmitButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});