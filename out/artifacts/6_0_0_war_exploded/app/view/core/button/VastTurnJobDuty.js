/**
 * 批量转岗位职责
 */	
Ext.define('erp.view.core.button.VastTurnJobDuty',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpVastTurnJobDutyButton',
		text: $I18N.common.button.erpVastTurnJobDutyButton,
    	iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	width: 150,
    	id: 'erpVastTurnJobDutyButton',
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});