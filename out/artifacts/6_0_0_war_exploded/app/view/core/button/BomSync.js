/**
 * BOM批量同步
 */	
Ext.define('erp.view.core.button.BomSync',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBomSyncButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBomSyncButton,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});