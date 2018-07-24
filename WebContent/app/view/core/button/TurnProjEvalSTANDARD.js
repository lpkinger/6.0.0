/**
 * 项目评估定制转标准
 */	
Ext.define('erp.view.core.button.TurnProjEvalSTANDARD',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnProjEvalSTANDARDButton',
		iconCls: 'x-button-icon-turn',
    	cls: 'x-btn-gray',
    	text: $I18N.common.button.erpTurnProjEvalSTANDARDButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 110,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});