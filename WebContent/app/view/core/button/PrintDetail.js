/**
 * 打印清单按钮
 */	
Ext.define('erp.view.core.button.PrintDetail',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintDetailButton',
		param: [],
		id: 'printDetail',
		text: $I18N.common.button.erpPrintDetailButton,
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});