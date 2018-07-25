/**
 * 条码补打
 */
Ext.define('erp.view.core.button.PrintRepair',{ 
		id:'printRepair',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintRepairButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintRepairButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});