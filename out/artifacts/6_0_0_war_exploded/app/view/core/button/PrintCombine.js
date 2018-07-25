/**
 * 打印拼板号
 */
Ext.define('erp.view.core.button.PrintCombine',{ 
		id:'printCombine',
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintCombineButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintCombineButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});