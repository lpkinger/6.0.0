Ext.define('erp.view.core.button.ResCarryover',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpResCarryoverButton',
		param: [],
		text: $I18N.common.button.erpResCarryoverButton,
		iconCls: 'x-button-icon-save',
		id:'rescarryoverbutton',
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