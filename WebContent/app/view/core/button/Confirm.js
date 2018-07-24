Ext.define('erp.view.core.button.Confirm',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmButton',
		param: [],
		id:'confirmbutton',
		text: $I18N.common.button.erpConfirmButton,
		iconCls: 'x-button-icon-check', 
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 65,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});