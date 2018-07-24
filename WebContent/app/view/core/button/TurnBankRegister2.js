/**
 * 采购单转银行登记
 */	
Ext.define('erp.view.core.button.TurnBankRegister2',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnBankRegisterButton2',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: '生成银行转存单',
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});