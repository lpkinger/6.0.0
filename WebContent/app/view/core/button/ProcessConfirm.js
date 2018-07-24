Ext.define('erp.view.core.button.ProcessConfirm',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessConfirmButton',
		param: [],
		id:'confirmbutton',
		text: $I18N.common.button.erpConfirmButton,
		iconCls: 'x-button-icon-save', 
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