/**
 * 根据原厂的型号自动匹配标准料号
 */	
Ext.define('erp.view.core.button.GetUUId',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpGetUUIdButton',
		iconCls : 'x-button-icon-submit',
		cls : 'x-btn-gray',
	    id: 'getuuid',
    	text: $I18N.common.button.erpGetUUIdButton,
    	style: {
    		marginLeft: '10px'
        },

        width:100,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});