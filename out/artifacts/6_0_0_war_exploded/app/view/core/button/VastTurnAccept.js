/**
 * 转收料单按钮
 */	
Ext.define('erp.view.core.button.VastTurnAccept',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnAcceptButton',
		text: $I18N.common.button.erpVastTurnAcceptButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 100,
    	id: 'erpVastTurnAcceptButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});