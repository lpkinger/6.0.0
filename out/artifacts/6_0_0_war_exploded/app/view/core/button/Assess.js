/**
 * 评估按钮
 */	
Ext.define('erp.view.core.button.Assess',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpAssessButton',
		param: [],
		id: 'Assess',
		text: '评 估',
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