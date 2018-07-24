/**
 * 自动汇总
 */	
Ext.define('erp.view.core.button.GetSumAmount',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetSumAmountButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id: 'erpGetSumAmountButton',
    	text: $I18N.common.button.erpGetSumAmountButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 120,
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler:function(url){
			
		}
	});