/**
 * 委外单转制造单按钮
 */	
Ext.define('erp.view.core.button.TurnOSToMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnOSToMakeButton',
		text: $I18N.common.button.erpTurnOSToMakeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpTurnOSToMakeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});