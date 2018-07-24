/**
 * 生成发票箱单按钮
 */	
Ext.define('erp.view.core.button.TurnPaIn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnPaInButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnPaInButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 130,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});