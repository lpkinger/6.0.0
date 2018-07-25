/**
 * 项目评估标准转定制
 */	
Ext.define('erp.view.core.button.TurnProjEvalCUSTOM',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProjEvalCUSTOMButton',
		iconCls: 'x-button-icon-turn',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnProjEvalCUSTOMButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});