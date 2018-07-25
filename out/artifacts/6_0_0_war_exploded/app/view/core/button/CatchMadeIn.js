/**
 * 抓取发票明细产地
 */	
Ext.define('erp.view.core.button.CatchMadeIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCatchMadeInButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpCatchMadeInButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 150,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});