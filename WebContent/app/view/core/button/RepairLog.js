/**
 * 维修登记，飞达
 */	
Ext.define('erp.view.core.button.RepairLog',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpRepairLogButton',
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	id: 'repairbtn',
    	text: $I18N.common.button.erpRepairLogButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});