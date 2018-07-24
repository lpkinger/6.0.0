/**
 * 转采购按钮
 */	
Ext.define('erp.view.core.button.TurnPurc',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erp2PurcButton',
		iconCls: 'x-button-icon-submit',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erp2PurcButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 70,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});