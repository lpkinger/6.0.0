/**
 * 批量转验收单按钮
 */	
Ext.define('erp.view.core.button.VastTurnCheckAccept',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnCheckAcceptButton',
		text: $I18N.common.button.erpVastTurnCheckAcceptButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnCheckAcceptButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});