/**
 * 批量转收料通知按钮
 */	
Ext.define('erp.view.core.button.VastTurnAccNotify',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnAccNotifyButton',
		text: $I18N.common.button.erpVastTurnAccNotifyButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnAccNotifyButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});