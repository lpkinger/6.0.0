/**
 * 筛选按钮
 */	
Ext.define('erp.view.core.button.Query',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpQueryButton',
		iconCls: 'x-button-icon-query',
		cls: 'x-btn-gray',
    	id: 'querybtn',
    	text: $I18N.common.button.erpQueryButton,
    	style: {
    		marginLeft: '10px'
        },
        width: 60,
		initComponent : function(){ 
			this.callParent(arguments); 
		}
	});