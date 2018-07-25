Ext.define('erp.view.core.button.SelectThrow',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpSelectThrowButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray-1',
    	id: 'erpSelectThrowButton',
    	text: $I18N.common.button.erpSelectThrowButton,
    	style: {
    		marginLeft: '10px'
        },
        width:90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});