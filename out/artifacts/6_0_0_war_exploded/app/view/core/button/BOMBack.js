/**
 * BOM批量反查
 */	
Ext.define('erp.view.core.button.BOMBack',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMBackButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBOMBackButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});