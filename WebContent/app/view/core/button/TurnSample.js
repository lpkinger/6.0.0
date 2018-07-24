/**
 * 生成送样单
 */
Ext.define('erp.view.core.button.TurnSample',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnSample',
		text: $I18N.common.button.erpTurnSample,
		iconCls: 'x-button-icon-add',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});