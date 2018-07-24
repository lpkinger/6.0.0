Ext.define('erp.view.core.button.Back',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBackButton',
		iconCls: 'icon-clear',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBackButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});