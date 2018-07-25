/**
 * BOM成本计算按钮
 */	
Ext.define('erp.view.core.button.BOMOfferCost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMOfferCostButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: '报价材料成本计算',
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});