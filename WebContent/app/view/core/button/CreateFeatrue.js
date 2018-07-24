Ext.define('erp.view.core.button.CreateFeatrue',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCreateFeatrueButton',
		param: [],
		text: $I18N.common.button.erpCreateFeatrueButton,
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	text:'生成特征件',
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});