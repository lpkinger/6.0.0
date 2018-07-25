/**
 * 转应付暂估
 */	
Ext.define('erp.view.core.button.TurnEstimate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnEstimateButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnEstimateButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});