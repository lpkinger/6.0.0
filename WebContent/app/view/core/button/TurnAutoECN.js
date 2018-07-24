/**
 * ECN自然执行转立即执行
 */	
Ext.define('erp.view.core.button.TurnAutoECN',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnAutoECNButton',
		iconCls: 'x-button-icon-turn',
    	cls: 'x-btn-gray',
	    id:'turnautoecnbutton',
    	text: $I18N.common.button.erpTurnAutoECNButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});