/**
 * 设备批量保养
 */	
Ext.define('erp.view.core.button.VastMaintain',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastMaintainButton',
		text: $I18N.common.button.erpVastMaintainButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 100,
    	id: 'erpVastMaintainButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});