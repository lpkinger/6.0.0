Ext.define('erp.view.core.button.ScanDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScanDetailButton',
		iconCls: 'x-button-icon-start',
    	cls: 'x-btn-gray',	    
    	text: $I18N.common.button.erpScanDetailButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});