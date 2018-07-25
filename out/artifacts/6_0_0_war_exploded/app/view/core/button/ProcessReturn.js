/**
 * 工序退制
 */
Ext.define('erp.view.core.button.ProcessReturn',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessReturnButton',
		param: [],
		id: 'erpProcessReturnButton',
		text: '工序退制',
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