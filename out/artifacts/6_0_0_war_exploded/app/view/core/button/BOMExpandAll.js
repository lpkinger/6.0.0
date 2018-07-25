/**
 * 展开所有BOM
 */	
Ext.define('erp.view.core.button.BOMExpandAll',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMExpandAllButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: "展开所有BOM",
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});