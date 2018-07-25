Ext.define('erp.view.core.button.ConfirmBill',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmBillButton',
		param: [],
		id:'ConfirmBillbutton',
		text: $I18N.common.button.erpConfirmBillButton,
		iconCls: 'x-button-icon-save', 
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 110,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});