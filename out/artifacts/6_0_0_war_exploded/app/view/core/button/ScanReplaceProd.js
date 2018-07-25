Ext.define('erp.view.core.button.ScanReplaceProd',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpScanReplaceProdButton',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpScanReplaceProdButton,
    	style: {
    		marginLeft: '5px'
        },
        width: 100,  
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});