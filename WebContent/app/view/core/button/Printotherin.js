/**
 * 打印返修机入仓单
 */	
Ext.define('erp.view.core.button.Printotherin',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintotherinButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintotherinButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});