/**
 * 飞达批量转保养
 */
Ext.define('erp.view.core.button.VastTurnMaintain',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnMaintainButton',
		text: $I18N.common.button.erpVastTurnMaintainButton,
    	tooltip: '批量转保养',
    	iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'erpVastTurnMaintainButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		width: 90
	});