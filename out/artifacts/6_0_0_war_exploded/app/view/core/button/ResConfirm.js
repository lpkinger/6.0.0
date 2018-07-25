Ext.define('erp.view.core.button.ResConfirm',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResConfirmButton',
		param: [],
		id:'resconfirmbutton',
		text: $I18N.common.button.erpResConfirmButton,
	    iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});