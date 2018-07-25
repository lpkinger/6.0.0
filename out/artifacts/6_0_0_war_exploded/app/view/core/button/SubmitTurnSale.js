/**
 * 提交(转正常)按钮
 */	
Ext.define('erp.view.core.button.SubmitTurnSale',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSubmitTurnSaleButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '提交(转正常)',
    	style: {
    		marginLeft: '10px'
        },
        width:120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});