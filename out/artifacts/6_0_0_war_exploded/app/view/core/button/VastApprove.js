/**
 * 批量批准按钮
 */	
Ext.define('erp.view.core.button.VastApprove',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastApproveButton',
		text: $I18N.common.button.erpVastApproveButton,
    	tooltip: '可以批准多条记录',
    	iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastApproveButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});