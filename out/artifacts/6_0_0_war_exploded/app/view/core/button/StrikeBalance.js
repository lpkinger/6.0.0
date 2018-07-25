/**
 * 过账按钮
 */	
Ext.define('erp.view.core.button.StrikeBalance',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpStrikeBalanceButton',
		iconCls: 'x-button-icon-check',
    	cls: 'x-btn-gray',
	    id:'strikebalancebutton',
    	text: $I18N.common.button.erpStrikeBalanceButton,
    	style: {
    		marginLeft: '10px'
        },

        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});