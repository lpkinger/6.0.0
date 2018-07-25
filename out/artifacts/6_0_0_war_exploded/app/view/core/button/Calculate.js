Ext.define('erp.view.core.button.Calculate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCalculateButton',
		param: [],
		id: 'calculate',
		text: $I18N.common.button.erpCalculateButton,
		iconCls: 'x-button-icon-save',
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