/**
 * 批量发出按钮
 */	
Ext.define('erp.view.core.button.VastSend',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSendButton',
		iconCls: 'x-button-icon-delete',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastSendButton',
    	tooltip: '发出多条记录',
    	text: $I18N.common.button.erpVastSendButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});