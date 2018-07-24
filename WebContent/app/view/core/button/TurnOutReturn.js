/**
 * 转借货归还单
 */	
Ext.define('erp.view.core.button.TurnOutReturn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnOutReturnButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	id:'turnoutreturn',
    	text: '转借货归还单',
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});