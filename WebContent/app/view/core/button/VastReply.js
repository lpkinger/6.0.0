/**
 * 批量回复按钮
 */	
Ext.define('erp.view.core.button.VastReply',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastReplyButton',
		text: $I18N.common.button.erpVastReplyButton,
    	tooltip: '可以回复多条记录',
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	width: 90,
    	id: 'erpVastReplyButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});