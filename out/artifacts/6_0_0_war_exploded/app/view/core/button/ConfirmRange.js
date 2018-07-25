Ext.define('erp.view.core.button.ConfirmRange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpConfirmRangeButton',
		param: [],
		id: 'erpConfirmRangeButton',
		text: $I18N.common.button.erpConfirmRangeButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});