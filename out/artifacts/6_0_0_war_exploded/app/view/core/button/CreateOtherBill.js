/**
 * 生成其它应收单
 */	
Ext.define('erp.view.core.button.CreateOtherBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateOtherBillButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id:'CreateOtherBill',
    	text: $I18N.common.button.erpCreateOtherBillButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});