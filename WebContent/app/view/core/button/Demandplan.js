Ext.define('erp.view.core.button.Demandplan',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpDemandplanButton',
		param: [],
		id: 'Demandplan',
		text: $I18N.common.button.erpDemandplanButton,
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