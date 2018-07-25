/**
 * BOM展开
 */	
Ext.define('erp.view.core.button.BOMExpand',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMExpandButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpBOMExpandButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});