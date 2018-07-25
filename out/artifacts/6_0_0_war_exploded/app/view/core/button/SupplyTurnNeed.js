/**
 * 供应转需求
 */	
Ext.define('erp.view.core.button.SupplyTurnNeed',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSupplyTurnNeedButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '供应转需求',
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});