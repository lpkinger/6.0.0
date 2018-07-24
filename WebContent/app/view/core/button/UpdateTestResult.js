/**
 * 更新测试结果
 */	
Ext.define('erp.view.core.button.UpdateTestResult',{
	extend: 'Ext.Button', 
	alias: 'widget.erpUpdateTestResultButton',
	iconCls: 'x-button-icon-submit',
	cls: 'x-btn-gray',
	id:'updatetestresult',
	text: '更新结果',
	initComponent : function(){ 
		this.callParent(arguments); 
	},
	width: 120
});