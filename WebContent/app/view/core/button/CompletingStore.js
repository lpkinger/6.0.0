/**
 * 完工入库
 */
Ext.define('erp.view.core.button.CompletingStore',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpCompletingStoreButton',
		param: [],
		id: 'erpCompletingStoreButton',
		text: '完工入库',
		iconCls: 'x-button-icon-save',
    	cls: 'x-btn-gray',
    	width: 100,
    	style: {
    		marginLeft: '10px'
        },
		initComponent : function(){ 
			this.callParent(arguments); 
		},
		handler : function(){ 
		}
	});