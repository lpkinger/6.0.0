Ext.define('erp.view.core.button.NoConfirm',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpNoConfirmButton',
		param: [],
		text: $I18N.common.button.erpNoConfirmButton,
		iconCls: 'x-button-icon-submit', 
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});