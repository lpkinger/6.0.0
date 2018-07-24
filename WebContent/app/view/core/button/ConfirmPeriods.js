Ext.define('erp.view.core.button.ConfirmPeriods',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmPeriodsButton',
		param: [],
		id:'ConfirmPeriodsbutton',
		text: $I18N.common.button.erpConfirmPeriodsButton,
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