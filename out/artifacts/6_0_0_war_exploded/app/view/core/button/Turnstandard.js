Ext.define('erp.view.core.button.Turnstandard',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnstandardButton',
		text: $I18N.common.button.erpTurnstandardButton,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});