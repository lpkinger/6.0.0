/**
 * 业务员预测批量处理
 */	
Ext.define('erp.view.core.button.TurnPreSaleFTSaleF',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPreSaleFTSaleFButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPreSaleFTSaleFButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});