/**
 * 转任务交接
 */	
Ext.define('erp.view.core.button.TurnEmpTransferCheck',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnEmpTransferCheckButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnEmpTransferCheckButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});