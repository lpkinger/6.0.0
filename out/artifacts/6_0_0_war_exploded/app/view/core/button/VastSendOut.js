/**
 * 确认寄出按钮
 */	
Ext.define('erp.view.core.button.VastSendOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastSendOutButton',
		iconCls: 'x-button-icon-SendOut',
    	cls: 'x-btn-gray-1',
    	id: 'sendout',
    	tooltip: '确认寄出多条记录',
    	id: 'erpVastSendOutButton',
    	text: $I18N.common.button.erpVastSendOutButton,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 110,
		handler: function(){
			
		}
	});