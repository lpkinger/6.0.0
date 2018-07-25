/**
 * 生成招标单
 */
Ext.define('erp.view.core.button.TurnTender',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnTender',
		text: $I18N.common.button.erpTurnTender,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 80,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});