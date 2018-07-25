/**
 * BOM成本计算按钮
 */	
Ext.define('erp.view.core.button.BOMCost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMCostButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBOMCostButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});