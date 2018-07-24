/**
 * 批量转制造单按钮
 */	
Ext.define('erp.view.core.button.VastTurnMakeMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnMakeMakeButton',
		text: $I18N.common.button.erpVastTurnMakeMakeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnMakeMakeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});