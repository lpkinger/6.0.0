Ext.define('erp.view.core.button.CardLogImp',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCardLogImpButton',
		param: [],
		id:'CardLogImpbutton',
		text: $I18N.common.button.erpCardLogImpButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	formBind: true,//form.isValid() == false时,按钮disabled
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});