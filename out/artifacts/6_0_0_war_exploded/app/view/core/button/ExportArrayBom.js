/**
 * 导出矩阵bOM
 */	
Ext.define('erp.view.core.button.ExportArrayBom',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpExportArrayBomButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id: 'ExportArrayBom',
    	text: $I18N.common.button.erpExportArrayBomButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});