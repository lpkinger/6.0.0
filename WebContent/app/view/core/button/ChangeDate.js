Ext.define('erp.view.core.button.ChangeDate',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpChangeDateButton',
		param: [],
		text: $I18N.common.button.erpChangeDateButton,
		iconCls: 'x-button-icon-submit',
		id:'changedate',
    	cls: 'x-btn-gray',
    	width: 90,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});