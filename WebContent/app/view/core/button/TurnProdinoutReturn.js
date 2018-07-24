/**
 * 转销售退货单按钮
 */	
Ext.define('erp.view.core.button.TurnProdinoutReturn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProdinoutReturnButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnProdinoutReturnButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});