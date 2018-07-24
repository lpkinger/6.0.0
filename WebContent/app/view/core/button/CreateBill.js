/**
 * 生成形式发票
 */	
Ext.define('erp.view.core.button.CreateBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateBillButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCreateBillButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});