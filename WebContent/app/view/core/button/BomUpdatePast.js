Ext.define('erp.view.core.button.BomUpdatePast',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBomUpdatePastButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'BomUpdatePast',
    	text: $I18N.common.button.erpBomUpdatePastButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});