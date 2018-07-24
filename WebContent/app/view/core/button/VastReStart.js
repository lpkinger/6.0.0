/**
 * 批量重启按钮
 */	
Ext.define('erp.view.core.button.VastReStart',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastReStartButton',
		text: $I18N.common.button.erpVastReStartButton,
    	tooltip: '可以重启多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastReStartButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
});