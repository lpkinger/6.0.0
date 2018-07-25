Ext.define('erp.view.core.button.Design',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDesignButton',
		param: [],
		text: $I18N.common.button.erpDesignButton,
		iconCls: 'x-button-icon-help',
		id: 'designbutton',
    	cls: 'x-btn-gray',
    	width: 60,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});