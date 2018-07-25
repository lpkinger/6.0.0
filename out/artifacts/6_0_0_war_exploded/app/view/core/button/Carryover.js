Ext.define('erp.view.core.button.Carryover',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCarryoverButton',
		param: [],
		id:'carryoverbutton',
		text: $I18N.common.button.erpCarryoverButton,
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