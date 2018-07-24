/**
 * 批量计算产品BOM成本
 */	
Ext.define('erp.view.core.button.BOMVastCost',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMVastCostButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpBOMVastCostButton',
    	text: $I18N.common.button.erpBOMVastCostButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});