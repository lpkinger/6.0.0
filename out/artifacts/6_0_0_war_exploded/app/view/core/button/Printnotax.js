/**
 * 不含税打印按钮
 */	
Ext.define('erp.view.core.button.Printnotax',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintnotaxButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintnotaxButton,
    	style: {
    		marginLeft: '10px'
        },
        width:110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});