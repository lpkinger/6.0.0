/**
 * 批量转制造单按钮
 */	
Ext.define('erp.view.core.button.VastTurnMake',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnMakeButton',
		text: $I18N.common.button.erpVastTurnMakeButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 110,
    	id: 'erpVastTurnMakeButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});