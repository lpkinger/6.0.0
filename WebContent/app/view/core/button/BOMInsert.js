/**
 * 导入建立BOM的产品
 */	
Ext.define('erp.view.core.button.BOMInsert',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpBOMInsertButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	id: 'erpBOMInsertButton',
    	text: $I18N.common.button.erpBOMInsertButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 155,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});