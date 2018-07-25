Ext.define('erp.view.core.button.TurnCostChange',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnCostChangeButton',
		param: [],
		id:'turncostchange',
		text: $I18N.common.button.erpTurnCostChangeButton,
		iconCls: 'x-button-icon-submit', 
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 120,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});