/**
 * 制造单转委外单按钮
 */	
Ext.define('erp.view.core.button.TurnOSMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnOSMakeButton',
		text: $I18N.common.button.erpTurnOSMakeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpTurnOSMakeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});