/**
 * 含税打印按钮
 */	
Ext.define('erp.view.core.button.Printyestax',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpPrintyestaxButton',
		iconCls: 'x-button-icon-print',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpPrintyestaxButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 90,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});