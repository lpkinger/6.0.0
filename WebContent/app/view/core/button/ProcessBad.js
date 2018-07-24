/**
 * 工序报废
 */
Ext.define('erp.view.core.button.ProcessBad',{ 
		extend: 'Ext.Button', 
		alias: 'widget.erpProcessBadButton',
		param: [],
		id: 'erpProcessBadButton',
		text: '工序报废',
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