Ext.define('erp.view.core.button.TurnYPOut',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpTurnYPOutButton',
		param: [],
		id: 'turnYPOut',
		text: $I18N.common.button.erpTurnYPOutButton,
		iconCls: 'x-button-icon-save',
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